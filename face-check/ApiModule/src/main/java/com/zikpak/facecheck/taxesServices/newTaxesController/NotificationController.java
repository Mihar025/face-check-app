package com.zikpak.facecheck.taxesServices.newTaxesController;

import com.zikpak.facecheck.requestsResponses.PageResponse;
import com.zikpak.facecheck.taxesServices.services.notificationService.NotificationRequest;
import com.zikpak.facecheck.taxesServices.services.notificationService.NotificationResponse;
import com.zikpak.facecheck.taxesServices.services.notificationService.NotificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @PostMapping("/company/{companyId}")
    public ResponseEntity<Void> createNotification(
            @PathVariable Integer companyId,
            @RequestBody @Valid NotificationRequest request) {
        notificationService.createNotification(companyId, request);
        return ResponseEntity.accepted().build();
    }

    @GetMapping("/company/{companyId}/today")
    public ResponseEntity<PageResponse<NotificationResponse>> getTodaysNotifications(
            @PathVariable Integer companyId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(notificationService.findAllNotificationsForToday(companyId, page, size));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNotification(@PathVariable Integer id) {
        notificationService.deleteNotificationById(id);
        return ResponseEntity.noContent().build();
    }
}