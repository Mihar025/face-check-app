package com.zikpak.facecheck.services.quartzSchedulerServices.jobs;


import com.zikpak.facecheck.entity.Status;
import com.zikpak.facecheck.repository.WorkerAttendanceVerificationRepository;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class AttendanceVerificationTimeoutJob implements Job {

    @Autowired
    private WorkerAttendanceVerificationRepository verificationRepository;


    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        Integer verificationId = context.getJobDetail()
                .getJobDataMap()
                .getInt("verificationId");

        verificationRepository.findById(verificationId).ifPresent(verification -> {
            if(verification.getStatus() == Status.PENDING){
                verification.setStatus(Status.MISSED);
                verification.setIsMissed(true);
                verification.setIsMissedMessage("Worker did not respond within 5 minutes");
                verificationRepository.save(verification);
                log.info("⏰ Verification {} marked as MISSED for worker {}",
                        verificationId, verification.getWorker().getId());
            }
        });

    }
}
