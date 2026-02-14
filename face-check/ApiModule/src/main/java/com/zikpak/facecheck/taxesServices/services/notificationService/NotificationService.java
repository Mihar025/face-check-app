package com.zikpak.facecheck.taxesServices.services.notificationService;


import com.zikpak.facecheck.entity.Company;
import com.zikpak.facecheck.entity.Notification;
import com.zikpak.facecheck.entity.User;
import com.zikpak.facecheck.entity.employee.WorkSite;
import com.zikpak.facecheck.repository.CompanyRepository;
import com.zikpak.facecheck.repository.NotificationRepository;
import com.zikpak.facecheck.repository.UserRepository;
import com.zikpak.facecheck.requestsResponses.PageResponse;
import com.zikpak.facecheck.requestsResponses.workSite.WorkSiteResponse;
import com.zikpak.facecheck.services.fcmService.FcmPushService;
import io.micrometer.core.instrument.Timer;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {


    private final NotificationRepository notificationRepository;
    private final CompanyRepository companyRepository;
    private final NotificationMapper notificationMapper;

    private final UserRepository userRepository;
    private final FcmPushService fcmPushService;




    @Async("notificationExecutor")
    public CompletableFuture<Void> createNotification(
            Integer companyId,
            NotificationRequest request
    ) {
        try {
            Company company = companyRepository.findById(companyId)
                    .orElseThrow(() -> new RuntimeException("Company not found"));

            boolean adminOnly = Boolean.TRUE.equals(request.getAdminOnly());

            Notification notification = Notification.builder()
                    .company(company)
                    .title(request.getMessage())
                    .createdAt(LocalDateTime.now())
                    .adminOnly(adminOnly)
                    .isRead(false)
                    .build();

            notificationRepository.save(notification);

            // ===== PUSH ЛОГИКА (ЕДИНСТВЕННАЯ) =====
            List<User> users = userRepository.findByCompanyId(companyId);

            for (User user : users) {
                if (user.getFcmToken() == null || user.getFcmToken().isBlank()) {
                    continue;
                }

                // 🔒 если adminOnly — пуш ТОЛЬКО админам
                if (adminOnly && !user.isAdmin()) {
                    continue;
                }

                // ✅ иначе — пуш всем
                fcmPushService.sendToToken(
                        user.getFcmToken(),
                        "FaceCheck",
                        request.getMessage()
                );
            }

        } catch (Exception e) {
            log.error("Notification creation failed", e);
        }

        return CompletableFuture.completedFuture(null);
    }


    public PageResponse<NotificationResponse> findAllNotificationsForToday(Authentication authentication, Integer companyId, int page, int size){
        User user = (User) authentication.getPrincipal();

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1);

        // Используем метод с диапазоном дат
        Page<Notification> notifications;

        boolean isAdmin = user.getRoles().stream()
                .anyMatch(role -> role.getName().equals("ADMIN") ||
                        role.getName().equals("AppOwner"));

        if (isAdmin) {
            // Админ видит ВСЁ
            notifications = notificationRepository
                    .findByCompanyIdAndCreatedAtBetweenOrderByCreatedAtDesc(
                            companyId, startOfDay, endOfDay, pageable);
        } else {
            // Worker видит только где adminOnly = false
            notifications = notificationRepository
                    .findByCompanyIdAndAdminOnlyFalseAndCreatedAtBetweenOrderByCreatedAtDesc(
                            companyId, startOfDay, endOfDay, pageable);
        }

        List<NotificationResponse> notificationResponses = notifications.getContent().stream()
                .map(notificationMapper::toNotification)
                .toList();

        return new PageResponse<>(
                notificationResponses,
                notifications.getNumber(),
                notifications.getSize(),
                notifications.getTotalElements(),
                notifications.getTotalPages(),
                notifications.isFirst(),
                notifications.isLast()
        );
    }

    public long getUnreadCount(Authentication authentication, Integer companyId){

        User user = (User) authentication.getPrincipal();
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfTheDay = startOfDay.plusDays(1);

        boolean isAdmin = user.getRoles().stream()
                .anyMatch( role -> role.getName().equals("ADMIN") ||
                                role.getName().equals("AppOwner"));

        if(isAdmin){
            return notificationRepository
                    .countByCompanyIdAndIsReadFalseAndCreatedAtBetween(
                            companyId, startOfDay, endOfTheDay);
        }
        else {
            return notificationRepository.countByCompanyIdAndAdminOnlyFalseAndIsReadFalseAndCreatedAtBetween(
                            companyId, startOfDay, endOfTheDay);
        }
    }

    @Transactional
    public void makeAllAsRead(Authentication authentication, Integer companyId){
        User user = (User) authentication.getPrincipal();
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1);


        boolean isAdmin = user.getRoles().stream()
                .anyMatch( role -> role.getName().equals("ADMIN") ||
                        role.getName().equals("AppOwner"));

        Page<Notification> notifications;
        Pageable pageable = PageRequest.of(0, 100);

        if(isAdmin){
            notifications = notificationRepository.findByCompanyIdAndCreatedAtBetweenOrderByCreatedAtDesc(
                    companyId, startOfDay, endOfDay, pageable);
        }
        else {
            notifications = notificationRepository.findByCompanyIdAndAdminOnlyFalseAndCreatedAtBetweenOrderByCreatedAtDesc(
                    companyId, startOfDay, endOfDay, pageable);
        }
        notifications.getContent().forEach(n -> n.setIsRead(true));
        notificationRepository.saveAll(notifications.getContent());
    }








    @Transactional
    public void deleteNotificationById(Integer id){
        var foundedNotification = notificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notification not found"));
        notificationRepository.delete(foundedNotification);
    }

}
