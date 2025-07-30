package com.zikpak.facecheck.services.quartzSchedulerServices.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class AlertService {

    private final JavaMailSender mailSender;

    @Value("${alerts.email.to:mishamay583@gmail.com}")
    private String alertEmail;

    @Value("${alerts.email.enabled:true}")
    private boolean alertsEnabled;

    @Async
    public void sendJobFailureAlert(String jobName, Exception error) {
        if (!alertsEnabled) return;

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(alertEmail);
            message.setSubject("🚨 Scheduler Alert: " + jobName + " FAILED");
            message.setText(
                    "Job Name: " + jobName + "\n" +
                            "Time: " + LocalDateTime.now() + "\n" +
                            "Error: " + error.getMessage() + "\n\n" +
                            "Please check the application logs for more details."
            );

            mailSender.send(message);
            log.info("Alert email sent for job: {}", jobName);

        } catch (Exception e) {
            log.error("Failed to send alert email for job: {}", jobName, e);
        }
    }

    @Async
    public void sendJobWarningAlert(String jobName, JobResult result) {
        if (!alertsEnabled) return;

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(alertEmail);
            message.setSubject("⚠️ Scheduler Warning: " + jobName + " had issues");
            message.setText(
                    "Job Name: " + jobName + "\n" +
                            "Time: " + LocalDateTime.now() + "\n" +
                            "Processed: " + result.getProcessedCount() + "\n" +
                            "Failed: " + result.getFailedCount() + "\n" +
                            "Message: " + result.getErrorMessage()
            );

            mailSender.send(message);

        } catch (Exception e) {
            log.error("Failed to send warning email for job: {}", jobName, e);
        }
    }
}