package com.zikpak.facecheck.services.sharedServiceForValidation;


import com.zikpak.facecheck.entity.User;
import com.zikpak.facecheck.entity.employee.WorkSite;
import com.zikpak.facecheck.entity.employee.WorkerSchedule;
import com.zikpak.facecheck.repository.UserRepository;
import com.zikpak.facecheck.repository.WorkerScheduleRepository;
import com.zikpak.facecheck.repository.WorkerSiteRepository;
import com.zikpak.facecheck.services.amazonS3Service.AmazonS3Service;
import com.zikpak.facecheck.services.transferService.TransferRequest;
import com.zikpak.facecheck.services.userService.UserService;
import com.zikpak.facecheck.services.workSiteService.WorkSiteService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
@RequiredArgsConstructor
public class SharedServiceForBusinessLogic {

    private final WorkSiteService workSiteService;
    private final UserRepository userRepository;
    private final WorkerSiteRepository workerSiteRepository;
    private final WorkerScheduleRepository workerScheduleRepository;


    private final AmazonS3Service amazonS3Service;


    private static final ExecutorService PHOTO_UPLOAD_EXECUTOR = Executors.newFixedThreadPool(10);





    public boolean isAdmin(Authentication authentication) {

        User user = (User) authentication.getPrincipal();

        if(!user.getRoles().stream().anyMatch(role -> role.getName().equals("ADMIN"))){
            throw new AccessDeniedException("You don't have permission for this operation!");
        }

        return true;
    }

    public boolean isAppOwner(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        if(!user.getRoles().stream().anyMatch(role -> role.getName().equals("AppOwner"))){
            throw new AccessDeniedException("You don't have permission for this operation!");
        }
        return true;
    }

    public boolean hasAccessForOperation(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        if(!user.getRoles().stream().anyMatch(role -> role.getName().equals("AppOwner") || role.getName().equals("ADMIN"))){
            throw new AccessDeniedException("You don't have permission for this operation!");
        }

        return true;
    }



    public boolean validateLocationForOperation(Double latitude,
                                                Double longitude,
                                                WorkSite workSite) {

        if(latitude == null || longitude == null){
            throw new IllegalArgumentException("Coordinates cannot be null");
        }

        if(latitude < -90 || latitude > 90){
            throw new IllegalArgumentException("Latitude must be between -90 and 90 degrees");
        }

        if(longitude < -180 || longitude > 180){
            throw new IllegalArgumentException("Longitude must be between -180 and 180 degrees");
        }

        boolean isInRadius = workSiteService.isWithinRadiusForPunchInOut(
                workSite.getId(),
                latitude,
                longitude
        );

        if(!isInRadius){
            throw new IllegalStateException("Error! You are not in allowed radius of the work site!");
        }
        return isInRadius;
    }





    public User validateAndGetUserByEmail(Authentication authentication) {
        User user = ((User) authentication.getPrincipal());


        if(user == null || user.getId() == null){
            throw new RuntimeException("User not found");
        }

        return userRepository.findByEmail(user.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));
    }


    public CompletableFuture<String> uploadPhotoAsync(String base64, String email, String type){
        return CompletableFuture.supplyAsync(() ->
                amazonS3Service.uploadAttendancePhoto(base64, email, type), PHOTO_UPLOAD_EXECUTOR);
    }

    public WorkSite validateAndGetWorkSite(Integer workSiteId) {
        var workSite = workerSiteRepository.findById(workSiteId)
                .orElseThrow(() -> new RuntimeException("Work site not found"));
        if(!workSite.getIsActive()){
            throw new IllegalStateException("Work site is not active");
        }
        return workSite;
    }

    public WorkerSchedule getWorkerScheduleForDate(User worker, LocalDate date) {
        DayOfWeek dayOfWeek = date.getDayOfWeek();

        return workerScheduleRepository.findByWorkerAndDayOfWeekAndIsTemplateTrue(worker, dayOfWeek)
                .orElseThrow(() -> new IllegalStateException(
                        String.format("No schedule template found for worker on %s", dayOfWeek)
                ));
    }




}
