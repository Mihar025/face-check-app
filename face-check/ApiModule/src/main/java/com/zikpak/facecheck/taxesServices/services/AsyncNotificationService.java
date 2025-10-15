package com.zikpak.facecheck.taxesServices.services;


import com.zikpak.facecheck.taxesServices.services.notificationService.NotificationRequest;
import com.zikpak.facecheck.taxesServices.services.notificationService.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class AsyncNotificationService {

    private final NotificationService notificationService;


    @Async
    public void buildAsyncNotificationForPunchInOut(
            String firstName,
            String lastName,
            String worksiteName,
            LocalDate today,
            String workSiteAddress,
            Integer companyId,
            String type){
        if(type.equals("PUNCH-IN")){

            NotificationRequest request = NotificationRequest.builder()
                    .message(firstName + " "
                            + lastName +
                            " made punch in, at:" +
                            today + " in " +
                            worksiteName + " " +
                            workSiteAddress)
                    .build();
            notificationService.createNotification(companyId, request);
        }

        else if(type.equals("PUNCH-OUT")){

            NotificationRequest request = NotificationRequest.builder()
                    .message(firstName + " "
                            + lastName +
                            " made punch out, at:" +
                            today + " in " +
                            worksiteName + " " +
                            workSiteAddress)
                    .build();
            notificationService.createNotification(companyId, request);
        }
    }



}
