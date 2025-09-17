package com.zikpak.facecheck.taxesServices.services.notificationService;


import com.zikpak.facecheck.entity.Notification;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationMapper {


    public NotificationResponse toNotification(Notification notification) {
    return  NotificationResponse.builder()
            .notificationId(notification.getId())
            .message(notification.getTitle())
            .createdAt(notification.getCreatedAt())
            .build();

    }
}
