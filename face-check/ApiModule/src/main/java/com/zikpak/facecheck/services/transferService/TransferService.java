package com.zikpak.facecheck.services.transferService;


import com.zikpak.facecheck.entity.User;
import com.zikpak.facecheck.entity.employee.WorkSite;
import com.zikpak.facecheck.entity.employee.WorkerAttendance;
import com.zikpak.facecheck.entity.employee.WorkerSchedule;
import com.zikpak.facecheck.repository.UserRepository;
import com.zikpak.facecheck.repository.WorkerAttendanceRepository;
import com.zikpak.facecheck.repository.WorkerScheduleRepository;
import com.zikpak.facecheck.repository.WorkerSiteRepository;
import com.zikpak.facecheck.services.amazonS3Service.AmazonS3Service;
import com.zikpak.facecheck.services.workSiteService.WorkSiteService;
import com.zikpak.facecheck.taxesServices.services.AsyncNotificationService;
    import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransferService {

    private final WorkerAttendanceRepository workerAttendanceRepository;
    private final UserRepository userRepository;
    private final WorkerSiteRepository workSiteRepository;
    private final WorkerScheduleRepository workerScheduleRepository;
    private final AmazonS3Service amazonS3Service;
    private final WorkSiteService workSiteService;
    private final AsyncNotificationService notificationService;


    private static final String typeTransfer = "TRANSFER";
    private static final ExecutorService PHOTO_UPLOAD_EXECUTOR = Executors.newFixedThreadPool(10);






    @Transactional()
    public TransferResponse makeTransfer(Authentication authentication, TransferRequest transferRequest){
        User user = validateAndGetUserByEmail(authentication);

        CompletableFuture<String> photoUrlAsyncTransfer = uploadPhotoAsync(
                transferRequest.getPhotoBase64(),
                user.getEmail(),
                "transfer"
        );

        try {
            LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
            LocalDateTime endOfDay = LocalDate.now().atTime(LocalTime.MAX);

            WorkerAttendance existingAttendance = workerAttendanceRepository
                    .findTodayActivePunchIn(user, startOfDay, endOfDay)
                    .orElseThrow(() -> new IllegalStateException("No active punch in found for today!"));

            WorkSite workSite = validateAndGetWorkSite(transferRequest.getWorkSiteId());
            LocalDate today = LocalDate.now();

            getWorkerScheduleForDate(user, today);

            validateLocationForTransfer(transferRequest, workSite);


            photoUrlAsyncTransfer.thenAccept(url -> {
                        existingAttendance.setTransferPhotoUrl(url);
                        workerAttendanceRepository.save(existingAttendance);
                    })
                    .exceptionally(ex -> {
                        log.error("Failed to upload photo for transfer {}", existingAttendance.getId(), ex);
                        existingAttendance.setTransferPhotoUrl("upload-failed");
                        workerAttendanceRepository.save(existingAttendance);
                        return null;
                    });


            existingAttendance.setTransferTime(LocalDateTime.now());
            existingAttendance.setTransferLatitude(transferRequest.getLatitude());
            existingAttendance.setTransferLongitude(transferRequest.getLongitude());
            existingAttendance.setTransferLocation(workSite.getAddress());

            user.setCurrentWorkSite(workSite);


            WorkerAttendance savedAttendance = workerAttendanceRepository.save(existingAttendance);

            notificationService.buildAsyncNotificationForPunchInOut(
                    user.getFirstName(),
                    user.getLastName(),
                    workSite.getSiteName(),
                    today,
                    workSite.getAddress(),
                    user.getCompany().getId(),
                    typeTransfer
            );


            return createSuccessResponseForTransfer(user, workSite, savedAttendance);
        } catch (Exception e) {

            log.error("Error during transfer", e);
            return createErrorResponseForTransfer(e.getMessage());
        }
    }



    private TransferResponse createErrorResponseForTransfer(String message) {
        return TransferResponse.builder()
                .isSuccessful(false)
                .message("Error during transfer: " + message)
                .build();
    }

    private TransferResponse createSuccessResponseForTransfer(User user, WorkSite workSite, WorkerAttendance attendance) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return TransferResponse.builder()
                .workerId(user.getId())
                .workSiteId(workSite.getId())
                .workSiteName(workSite.getSiteName())
                .workerFullName(user.getFirstName() + " " + user.getLastName())
                .transferTime(attendance.getTransferTime())
                .formattedTransferTime(attendance.getTransferTime() != null ?
                        attendance.getTransferTime().format(formatter) : null)
                .transferPhotoUrl(attendance.getTransferPhotoUrl())
                .transferLatitude(attendance.getTransferLatitude())
                .transferLongitude(attendance.getTransferLongitude())
                .workSiteAddress(workSite.getAddress())
                .transferLocation(attendance.getTransferLocation())
                .isSuccessful(true)
                .message("Transfered successful")
                .build();
    }


    private boolean validateLocationForTransfer(TransferRequest transferRequest, WorkSite workSite) {

        if(transferRequest.getLatitude() == null || transferRequest.getLongitude() == null){
            throw new IllegalArgumentException("Coordinates cannot be null");
        }

        if(transferRequest.getLatitude() < -90 || transferRequest.getLatitude() > 90){
            throw new IllegalArgumentException("Latitude must be between -90 and 90 degrees");
        }

        if(transferRequest.getLongitude() < -180 || transferRequest.getLongitude() > 180){
            throw new IllegalArgumentException("Longitude must be between -180 and 180 degrees");
        }

        boolean isInRadius = workSiteService.isWithinRadiusForPunchInOut(
                workSite.getId(),
                transferRequest.getLatitude(),
                transferRequest.getLongitude()
        );

        if(!isInRadius){
            throw new IllegalStateException("Error! You are not in allowed radius of the work site!");
        }
        return isInRadius;
    }


    public CompletableFuture<String> uploadPhotoAsync(String base64, String email, String type){
        return CompletableFuture.supplyAsync(() ->
                amazonS3Service.uploadAttendancePhoto(base64, email, type), PHOTO_UPLOAD_EXECUTOR);
    }

    private User validateAndGetUserByEmail(Authentication authentication) {
        User user = ((User) authentication.getPrincipal());


        if(user == null || user.getId() == null){
            throw new RuntimeException("User not found");
        }

        return userRepository.findByEmail(user.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));
    }


    private WorkSite validateAndGetWorkSite(Integer workSiteId) {
        var workSite = workSiteRepository.findById(workSiteId)
                .orElseThrow(() -> new RuntimeException("Work site not found"));
        if(!workSite.getIsActive()){
            throw new IllegalStateException("Work site is not active");
        }
        return workSite;
    }


    private WorkerSchedule getWorkerScheduleForDate(User worker, LocalDate date) {
        DayOfWeek dayOfWeek = date.getDayOfWeek();

        return workerScheduleRepository.findByWorkerAndDayOfWeekAndIsTemplateTrue(worker, dayOfWeek)
                .orElseThrow(() -> new IllegalStateException(
                        String.format("No schedule template found for worker on %s", dayOfWeek)
                ));
    }



}
