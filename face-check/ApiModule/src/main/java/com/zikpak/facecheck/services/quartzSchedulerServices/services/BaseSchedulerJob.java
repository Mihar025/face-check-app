package com.zikpak.facecheck.services.quartzSchedulerServices.services;

import com.zikpak.facecheck.entity.JobStatus;
import com.zikpak.facecheck.entity.SchedulerExecutionHistory;
import com.zikpak.facecheck.repository.SchedulerExecutionHistoryRepository;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;

@Slf4j
public abstract class BaseSchedulerJob implements Job {
    @Autowired
    private  SchedulerExecutionHistoryRepository historyRepo;
    @Autowired
    private  AlertService alertService;



    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        String jobName = context.getJobDetail().getKey().getName();
        String jobGroup = context.getJobDetail().getKey().getGroup();

        SchedulerExecutionHistory history = SchedulerExecutionHistory.builder()
                .jobName(jobName)
                .jobGroup(jobGroup)
                .startTime(LocalDateTime.now())
                .status(JobStatus.RUNNING)
                .retryCount(0)
                .build();

        history = historyRepo.save(history);

        try {
            log.info("🚀 Starting job: {}", jobName);

            JobResult result = executeJob(context);

            // Обновляем историю
            history.setStatus(result.isSuccess() ? JobStatus.SUCCESS : JobStatus.FAILED);
            history.setEndTime(LocalDateTime.now());
            history.setRecordsProcessed(result.getProcessedCount());
            history.setRecordsFailed(result.getFailedCount());
            history.setErrorMessage(result.getErrorMessage());

            // Считаем duration
            long duration = java.time.Duration.between(
                    history.getStartTime(),
                    history.getEndTime()
            ).getSeconds();
            history.setDurationSeconds(duration);

            historyRepo.save(history);

            log.info("✅ Job {} completed in {} seconds. Processed: {}, Failed: {}",
                    jobName, duration, result.getProcessedCount(), result.getFailedCount());

            if (result.getFailedCount() > 0) {
               alertService.sendJobWarningAlert(jobName, result);
            }

        } catch (Exception e) {
            log.error("❌ Job {} failed with error", jobName, e);

            history.setStatus(JobStatus.FAILED);
            history.setEndTime(LocalDateTime.now());
            history.setErrorMessage(e.getMessage());
            historyRepo.save(history);

            alertService.sendJobFailureAlert(jobName, e);

            throw new JobExecutionException(e);
        }
    }

    // Этот метод реализуют наследники
    protected abstract JobResult executeJob(JobExecutionContext context) throws Exception;
}