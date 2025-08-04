package com.zikpak.facecheck.services.quartzSchedulerServices.services;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class JobResult {
    private boolean success;
    private int processedCount;
    private int failedCount;
    private String errorMessage;

    public static JobResult success(int processed) {
        return JobResult.builder()
                .success(true)
                .processedCount(processed)
                .failedCount(0)
                .build();
    }

    public static JobResult partialSuccess(int processed, int failed, String message) {
        return JobResult.builder()
                .success(false)
                .processedCount(processed)
                .failedCount(failed)
                .errorMessage(message)
                .build();
    }

    public static JobResult failure(String error) {
        return JobResult.builder()
                .success(false)
                .processedCount(0)
                .failedCount(0)
                .errorMessage(error)
                .build();
    }
}