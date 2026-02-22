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
import com.zikpak.facecheck.services.sharedServiceForValidation.SharedServiceForBusinessLogic;
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
    private final WorkerScheduleRepository workerScheduleRepository;
    private final AmazonS3Service amazonS3Service;
    private final SharedServiceForBusinessLogic sharedServiceForBusinessLogic;
    private final AsyncNotificationService notificationService;


    private static final String typeTransfer = "TRANSFER";
    private static final ExecutorService PHOTO_UPLOAD_EXECUTOR = Executors.newFixedThreadPool(10);




    @Transactional()
    public TransferResponse makeTransfer(Authentication authentication, TransferRequest transferRequest){
        User user = sharedServiceForBusinessLogic.validateAndGetUserByEmail(authentication);

        CompletableFuture<String> photoUrlAsyncTransfer = sharedServiceForBusinessLogic.uploadPhotoAsync(
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

            WorkSite workSite = sharedServiceForBusinessLogic.validateAndGetWorkSite(transferRequest.getWorkSiteId());
            LocalDate today = LocalDate.now();

            sharedServiceForBusinessLogic.getWorkerScheduleForDate(user, today);

            sharedServiceForBusinessLogic.validateLocationForOperation(
                    transferRequest.getLatitude(),
                    transferRequest.getLongitude(),
                    workSite);


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
                .message("Transferred successful")
                .build();
    }




}
