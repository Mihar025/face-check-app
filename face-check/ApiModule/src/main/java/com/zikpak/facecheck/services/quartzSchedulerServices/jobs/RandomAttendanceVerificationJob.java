package com.zikpak.facecheck.services.quartzSchedulerServices.jobs;

import com.zikpak.facecheck.entity.Notification;
import com.zikpak.facecheck.entity.RandomAttendanceVerification;
import com.zikpak.facecheck.entity.Status;
import com.zikpak.facecheck.entity.User;
import com.zikpak.facecheck.repository.NotificationRepository;
import com.zikpak.facecheck.repository.UserRepository;
import com.zikpak.facecheck.repository.WorkerAttendanceVerificationRepository;
import com.zikpak.facecheck.services.fcmService.FcmPushService;
import com.zikpak.facecheck.services.quartzSchedulerServices.services.BaseSchedulerJob;
import com.zikpak.facecheck.services.quartzSchedulerServices.services.JobResult;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

@Component
@Slf4j
public class RandomAttendanceVerificationJob extends BaseSchedulerJob {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WorkerAttendanceVerificationRepository verificationRepository;

    @Autowired
    private FcmPushService fcmPushService;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private Scheduler scheduler;

    private static final int CHANCE_PERCENT = 55;
    private static final int TIMEOUT_MINUTES = 5;

    @Override
    protected JobResult executeJob(JobExecutionContext context) throws Exception {

        LocalDate today = LocalDate.now(ZoneId.of("America/New_York"));
        List<User> activeRemoteWorkers = userRepository.findActiveRemoteWorkers(today);

        if (activeRemoteWorkers.isEmpty()) {
            log.info("📋 No active remote workers found, skipping");
            return JobResult.success(0);
        }

        int sent = 0;

        for (User worker : activeRemoteWorkers) {

            // 20% шанс что проверка сработает в этом 30-мин интервале
            if (ThreadLocalRandom.current().nextInt(100) >= CHANCE_PERCENT) {
                continue;
            }

            // Не отправляем если уже есть PENDING для этого воркера сегодня
            boolean hasPending = verificationRepository
                    .existsByWorkerIdAndStatusAndCreatedAt(worker.getId(), Status.PENDING, today);
            if (hasPending) {
                continue;
            }

            // Создаём PENDING запись
            RandomAttendanceVerification verification = new RandomAttendanceVerification();
            verification.setWorker(worker);
            verification.setStatus(Status.PENDING);
            verification.setIsMissed(false);
            verification.setIsSuccessful(false);
            verification.setCreatedAt(today);
            verification.setRandomAttendanceVerificationTime(LocalDateTime.now(ZoneId.of("America/New_York")));

            RandomAttendanceVerification saved = verificationRepository.save(verification);

            // Отправляем FCM push с data payload
            sendVerificationPush(worker, saved.getId());

            // Планируем timeout через 5 минут
            scheduleTimeoutJob(saved.getId());

            sent++;
            log.info("📲 Verification #{} sent to worker {} ({})",
                    saved.getId(), worker.getId(), worker.fullName());
        }

        String message = String.format("Sent %d verifications to %d active remote workers",
                sent, activeRemoteWorkers.size());
        log.info("✅ {}", message);
        return JobResult.success(sent);
    }

    private void sendVerificationPush(User worker, Integer verificationId) {
        try {
            if (worker.getFcmToken() == null || worker.getFcmToken().isBlank()) {
                log.warn("⚠️ Worker {} has no FCM token", worker.getId());
                return;
            }

            // Push с data payload для Flutter
            fcmPushService.sendToTokenWithData(
                    worker.getFcmToken(),
                    "Attendance Verification",
                    "Please verify your presence by taking a photo",
                    Map.of(
                            "type", "PRESENCE_CHECK",
                            "verificationId", verificationId.toString()
                    )
            );

            // Сохраняем notification для конкретного юзера
            Notification notification = Notification.builder()
                    .company(worker.getCompany())
                    .targetUser(worker)
                    .title("Attendance Verification Required")
                    .createdAt(LocalDateTime.now())
                    .adminOnly(false)
                    .isRead(false)
                    .build();
            notificationRepository.save(notification);

        } catch (Exception e) {
            log.error("❌ Failed to send push to worker {}", worker.getId(), e);
        }
    }

    private void scheduleTimeoutJob(Integer verificationId) throws SchedulerException {
        JobDetail timeoutJob = JobBuilder.newJob(AttendanceVerificationTimeoutJob.class)
                .withIdentity("timeout_verification_" + verificationId, "VERIFICATION_TIMEOUT")
                .usingJobData("verificationId", verificationId)
                .build();

        Trigger timeoutTrigger = TriggerBuilder.newTrigger()
                .withIdentity("timeout_trigger_" + verificationId, "VERIFICATION_TIMEOUT")
                .startAt(Date.from(
                        LocalDateTime.now()
                                .plusMinutes(TIMEOUT_MINUTES)
                                .atZone(ZoneId.of("America/New_York"))
                                .toInstant()))
                .build();

        scheduler.scheduleJob(timeoutJob, timeoutTrigger);
        log.info("⏰ Timeout scheduled for verification #{} in {} minutes",
                verificationId, TIMEOUT_MINUTES);
    }
}