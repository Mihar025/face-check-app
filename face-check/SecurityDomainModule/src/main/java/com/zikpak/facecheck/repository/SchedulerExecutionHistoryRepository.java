package com.zikpak.facecheck.repository;

import com.zikpak.facecheck.entity.JobStatus;
import com.zikpak.facecheck.entity.SchedulerExecutionHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface SchedulerExecutionHistoryRepository extends JpaRepository<SchedulerExecutionHistory, Integer> {

    Page<SchedulerExecutionHistory> findByStatus(JobStatus status, Pageable pageable);

    Page<SchedulerExecutionHistory> findByCompanyId(Integer companyId, Pageable pageable);

    Page<SchedulerExecutionHistory> findByStatusAndCompanyId(JobStatus status, Integer companyId, Pageable pageable);

    List<SchedulerExecutionHistory> findByJobNameAndStatus(String jobName, JobStatus status);

    @Query("SELECT h FROM SchedulerExecutionHistory h WHERE h.status = 'FAILED' AND h.createdAt >= :since")
    List<SchedulerExecutionHistory> findRecentFailures(LocalDateTime since);

    @Query("SELECT COUNT(h) FROM SchedulerExecutionHistory h WHERE h.jobName = :jobName AND h.status = 'RUNNING'")
    long countRunningJobs(String jobName);

    Optional<SchedulerExecutionHistory> findTopByJobNameAndCompanyIdOrderByStartTimeDesc(String jobName, Integer companyId);
}
