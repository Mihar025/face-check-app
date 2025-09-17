package com.zikpak.facecheck.taxesServices.services.notificationService;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NotificationResponse {

    private Integer notificationId;

    private String message;

    private LocalDateTime createdAt;


}
