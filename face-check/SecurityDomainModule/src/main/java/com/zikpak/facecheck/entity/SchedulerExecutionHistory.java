package com.zikpak.facecheck.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "scheduler_execution_history")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SchedulerExecutionHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "job_name", nullable = false, length = 200)
    private String jobName;

    @Column(name = "job_group", nullable = false, length = 200)
    private String jobGroup;

    @Column(name = "company_id")
    private Integer companyId;

    @Column(name = "company_name")
    private String companyName;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @Column(name = "status", nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private JobStatus status;

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    @Column(name = "retry_count")
    private Integer retryCount = 0;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "duration_seconds")
    private Long durationSeconds;

    @Column(name = "records_processed")
    private Integer recordsProcessed;

    @Column(name = "records_failed")
    private Integer recordsFailed;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (retryCount == null) {
            retryCount = 0;
        }
    }

    @PostUpdate
    protected void onUpdate() {
        if (startTime != null && endTime != null) {
            durationSeconds = java.time.Duration.between(startTime, endTime).getSeconds();
        }
    }


}
