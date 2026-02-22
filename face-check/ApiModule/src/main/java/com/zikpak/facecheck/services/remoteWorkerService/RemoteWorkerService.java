package com.zikpak.facecheck.services.remoteWorkerService;

import com.zikpak.facecheck.entity.RandomAttendanceVerification;
import com.zikpak.facecheck.entity.Status;
import com.zikpak.facecheck.entity.User;
import com.zikpak.facecheck.entity.employee.WorkSite;
import com.zikpak.facecheck.entity.employee.WorkerAttendance;
import com.zikpak.facecheck.repository.CompanyRepository;
import com.zikpak.facecheck.repository.UserRepository;
import com.zikpak.facecheck.repository.WorkerAttendanceVerificationRepository;
import com.zikpak.facecheck.requestsResponses.PageResponse;
import com.zikpak.facecheck.services.sharedServiceForValidation.SharedServiceForBusinessLogic;
import com.zikpak.facecheck.services.transferService.TransferResponse;
import com.zikpak.facecheck.taxesServices.services.AsyncNotificationService;
import com.zikpak.facecheck.taxesServices.services.notificationService.NotificationService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class RemoteWorkerService {

    private final UserRepository userRepository;
    private final WorkerAttendanceVerificationRepository workerAttendanceVerificationRepository;


    private final SharedServiceForBusinessLogic sharedServiceForBusinessLogic;
    private final AsyncNotificationService notificationService;
    //private final RemoteWorkerMapper remoteWorkerMapper;

    private static final String typeRandomAttendanceVerification = "RANDOM-ATTENDANCE-VERIFICATION";


    @Transactional(rollbackOn =  RuntimeException.class)
    public void setWorkerAsRemoteWorker(Authentication authentication, Integer workerId){

        // Has accessForOperation return true if User is ADMIN or AppOwner else AccessDinedException
        boolean hasAccess = sharedServiceForBusinessLogic.hasAccessForOperation(authentication);

        if(hasAccess){

            User foundedUser = userRepository.findById(workerId)
                    .orElseThrow(() -> new EntityNotFoundException("Worker with provided id wasn't found"));
            foundedUser.setIsRemoteWorker(Boolean.TRUE);
        }
        else{
            throw new AccessDeniedException("You don't have permission for this operation!");
        }
    }

    @Transactional(rollbackOn =  RuntimeException.class)
    public void setWorkerAsNotRemoteWorker(Authentication authentication, Integer workerId){

        // Has accessForOperation return true if User is ADMIN or AppOwner else AccessDinedException
        boolean hasAccess = sharedServiceForBusinessLogic.hasAccessForOperation(authentication);

        if(hasAccess){

            User foundedUser = userRepository.findById(workerId)
                    .orElseThrow(() -> new EntityNotFoundException("Worker with provided id wasn't found"));
            foundedUser.setIsRemoteWorker(Boolean.FALSE);
        }
        else{
            throw new AccessDeniedException("You don't have permission for this operation!");
        }
    }

    @Transactional(rollbackOn = RuntimeException.class)
    public RandonAttendanceVerificationResponse completeRandomAttendanceVerification(
            Authentication authentication,
            RandonAttendanceVerificationRequest request) {

        User user = sharedServiceForBusinessLogic.validateAndGetUserByEmail(authentication);

        // Находим PENDING запись
        RandomAttendanceVerification verification = workerAttendanceVerificationRepository
                .findById(request.getVerificationId())
                .orElseThrow(() -> new EntityNotFoundException("Verification not found"));

        // Проверяем что это запись этого воркера и она PENDING
        if (!verification.getWorker().getId().equals(user.getId())) {
            throw new AccessDeniedException("This verification doesn't belong to you");
        }
        if (verification.getStatus() != Status.PENDING) {
            return RandonAttendanceVerificationResponse.builder()
                    .isSuccessful(false)
                    .message("Verification already " + verification.getStatus())
                    .build();
        }

        CompletableFuture<String> photoUrlAsync = sharedServiceForBusinessLogic.uploadPhotoAsync(
                request.getPhotoBase64(),
                user.getEmail(),
                "randomAttendanceVerification"
        );

        try {
            WorkSite workSite = sharedServiceForBusinessLogic.validateAndGetWorkSite(request.getWorkSiteId());

            sharedServiceForBusinessLogic.validateLocationForOperation(
                    request.getLatitude(),
                    request.getLongitude(),
                    workSite
            );

            // Обновляем существующую запись
            verification.setStatus(Status.COMPLETED);
            verification.setIsSuccessful(true);
            verification.setIsMissed(false);
            verification.setIsMissedMessage("Successfully passed Random Attendance Verification");
            verification.setRandomAttendanceVerificationLatitude(request.getLatitude());
            verification.setRandomAttendanceVerificationLongitude(request.getLongitude());
            verification.setRandomAttendanceVerificationLocation(workSite.getAddress());
            verification.setRandomAttendanceVerificationTime(LocalDateTime.now());

            RandomAttendanceVerification saved = workerAttendanceVerificationRepository.save(verification);

            // Async фото
            photoUrlAsync.thenAccept(url -> {
                workerAttendanceVerificationRepository.updatePhotoUrl(saved.getId(), url);
            }).exceptionally(ex -> {
                log.error("Failed to upload photo for verification {}", saved.getId(), ex);
                workerAttendanceVerificationRepository.updatePhotoUrl(saved.getId(), "upload-failed");
                return null;
            });

            notificationService.buildAsyncNotificationForPunchInOut(
                    user.getFirstName(),
                    user.getLastName(),
                    workSite.getSiteName(),
                    LocalDate.now(),
                    workSite.getAddress(),
                    user.getCompany().getId(),
                    typeRandomAttendanceVerification
            );

            return createSuccessResponseForRandomAttendanceVerification(user, workSite, saved);

        } catch (Exception e) {
            log.error("Error during attendance verification completion", e);
            return createErrorResponseForRandomAttendanceVerification(e.getMessage());
        }
    }


    public RandonAttendanceVerificationResponse getPendingVerification(Authentication authentication) {
        User user = sharedServiceForBusinessLogic.validateAndGetUserByEmail(authentication);
        LocalDate today = LocalDate.now();

        return workerAttendanceVerificationRepository
                .findByWorkerIdAndStatusAndCreatedAt(user.getId(), Status.PENDING, today)
                .map(v -> RandonAttendanceVerificationResponse.builder()
                        .verificationId(v.getId())
                        .isSuccessful(true)
                        .message("Pending verification found")
                        .build())
                .orElse(RandonAttendanceVerificationResponse.builder()
                        .isSuccessful(false)
                        .message("No pending verification")
                        .build());
    }


    public PageResponse<RemoteVerificationAdminResponse> getAllVerificationsForAdmin(
            Authentication authentication,
            Integer companyId,
            String statusFilter,
            String dateFrom,
            String dateTo,
            int page,
            int size) {

        User user = sharedServiceForBusinessLogic.validateAndGetUserByEmail(authentication);

        // Determine if AppOwner (sees all) or ADMIN (sees own company)
        boolean isAppOwner = user.getRoles().stream()
                .anyMatch(role -> role.getName().equals("AppOwner"));
        boolean isAdmin = user.getRoles().stream()
                .anyMatch(role -> role.getName().equals("ADMIN"));

        if (!isAppOwner && !isAdmin) {
            throw new AccessDeniedException("You don't have permission for this operation!");
        }

        // If ADMIN (not AppOwner), force company filter to their own company
        Integer effectiveCompanyId = isAppOwner ? companyId : user.getCompany().getId();

        // Parse filters
        Status status = null;
        if (statusFilter != null && !statusFilter.isBlank()) {
            try {
                status = Status.valueOf(statusFilter.toUpperCase());
            } catch (IllegalArgumentException ignored) {}
        }

        LocalDate from = null;
        LocalDate to = null;
        if (dateFrom != null && !dateFrom.isBlank()) {
            from = LocalDate.parse(dateFrom);
        }
        if (dateTo != null && !dateTo.isBlank()) {
            to = LocalDate.parse(dateTo);
        }

        Pageable pageable = PageRequest.of(page, size);

        Page<RandomAttendanceVerification> verifications =
                workerAttendanceVerificationRepository.findVerificationsFiltered(
                        effectiveCompanyId, status, from, to, pageable);

        List<RemoteVerificationAdminResponse> responses = verifications.getContent().stream()
                .map(this::mapToAdminResponse)
                .toList();

        return new PageResponse<>(
                responses,
                verifications.getNumber(),
                verifications.getSize(),
                verifications.getTotalElements(),
                verifications.getTotalPages(),
                verifications.isFirst(),
                verifications.isLast()
        );
    }


    /**
     * Get summary stats for dashboard cards
     */
    public RemoteVerificationStatsResponse getVerificationStats(
            Authentication authentication, Integer companyId) {

        User user = sharedServiceForBusinessLogic.validateAndGetUserByEmail(authentication);
        boolean isAppOwner = user.getRoles().stream()
                .anyMatch(role -> role.getName().equals("AppOwner"));

        Integer effectiveCompanyId = isAppOwner ? companyId : user.getCompany().getId();
        LocalDate today = LocalDate.now(ZoneId.of("America/New_York"));

        long completedToday = workerAttendanceVerificationRepository
                .countByWorkerCompanyIdAndStatusAndCreatedAt(effectiveCompanyId, Status.COMPLETED, today);
        long missedToday = workerAttendanceVerificationRepository
                .countByWorkerCompanyIdAndStatusAndCreatedAt(effectiveCompanyId, Status.MISSED, today);
        long pendingToday = workerAttendanceVerificationRepository
                .countByWorkerCompanyIdAndStatusAndCreatedAt(effectiveCompanyId, Status.PENDING, today);

        long total = completedToday + missedToday + pendingToday;
        double complianceRate = total > 0 ? (double) completedToday / total * 100 : 0;

        return RemoteVerificationStatsResponse.builder()
                .completedToday(completedToday)
                .missedToday(missedToday)
                .pendingToday(pendingToday)
                .totalToday(total)
                .complianceRate(Math.round(complianceRate * 10) / 10.0)
                .build();
    }


    private RemoteVerificationAdminResponse mapToAdminResponse(RandomAttendanceVerification v) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        User worker = v.getWorker();

        return RemoteVerificationAdminResponse.builder()
                .verificationId(v.getId())
                .workerId(worker.getId())
                .workerFirstName(worker.getFirstName())
                .workerLastName(worker.getLastName())
                .workerEmail(worker.getEmail())
                .workerPhone(worker.getPhoneNumber())
                .companyId(worker.getCompany() != null ? worker.getCompany().getId() : null)
                .companyName(worker.getCompany() != null ? worker.getCompany().getCompanyName() : "N/A")
                .status(v.getStatus())
                .isSuccessful(v.getIsSuccessful())
                .isMissed(v.getIsMissed())
                .isMissedMessage(v.getIsMissedMessage())
                .message(v.getMessage())
                .photoUrl(v.getRandomAttendanceVerificationPhotoUrl())
                .latitude(v.getRandomAttendanceVerificationLatitude())
                .longitude(v.getRandomAttendanceVerificationLongitude())
                .locationAddress(v.getRandomAttendanceVerificationLocation())
                .verificationTime(v.getRandomAttendanceVerificationTime())
                .formattedVerificationTime(v.getRandomAttendanceVerificationTime() != null
                        ? v.getRandomAttendanceVerificationTime().format(formatter) : null)
                .createdAt(v.getCreatedAt())
                .build();
    }





    private RandonAttendanceVerificationResponse createErrorResponseForRandomAttendanceVerification(String message) {
        return RandonAttendanceVerificationResponse.builder()
                .isSuccessful(Boolean.FALSE)
                .message("Error during Attendance Verification: " + message)
                .build();
    }

    private RandonAttendanceVerificationResponse createSuccessResponseForRandomAttendanceVerification(User user, WorkSite workSite, RandomAttendanceVerification attendance) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        return RandonAttendanceVerificationResponse.builder()
                .workerId(user.getId())
                .workSiteId(workSite.getId())
                .workSiteName(workSite.getSiteName())
                .workerFullName(user.getFirstName() + " " + user.getLastName())
                .randomAttendanceVerificationTime(attendance.getRandomAttendanceVerificationTime())
                .formattedRandomAttendanceVerificationTime(attendance.getRandomAttendanceVerificationTime() != null ?
                        attendance.getRandomAttendanceVerificationTime().format(formatter) : null)
                .randomAttendanceVerificationPhotoUrl(attendance.getRandomAttendanceVerificationPhotoUrl())
                .randomAttendanceVerificationLatitude(attendance.getRandomAttendanceVerificationLatitude())
                .randomAttendanceVerificationLongitude(attendance.getRandomAttendanceVerificationLongitude())
                .workSiteAddress(workSite.getAddress())
                .randomAttendanceVerificationLocation(attendance.getRandomAttendanceVerificationLocation())
                .isSuccessful(true)
                .isMissed(Boolean.FALSE)
                .isMissedMessage("Successfully passed Random Attendance Verification")
                .message("")
                .build();
    }








}
