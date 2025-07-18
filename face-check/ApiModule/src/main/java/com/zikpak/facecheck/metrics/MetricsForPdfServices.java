package com.zikpak.facecheck.metrics;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class MetricsForPdfServices {

    private final MeterRegistry meterRegistry;

    public Timer.Sample startTimer() {
        return Timer.start(meterRegistry);
    }

    public void recordOperationTime(Timer.Sample sample, String operation) {
        sample.stop(meterRegistry.timer(
                "pdf.operation.duration",
                "operation", operation
        ));
    }

    public void recordRequest(String formName){
        meterRegistry.counter(
                "pdf.requests",
                "form", formName
        ).increment();
    }


    public void recordGenerated(String formName, boolean success){
        meterRegistry.counter(
                "pdf.generated",
                "form", formName,
                "status", success ? "success" : "failed"
        ).increment();
    }

    public void recordS3UploadTime(String formName, boolean success, long millis) {
        meterRegistry.counter(
                "pdf.s3_upload",
                "form", formName,
                "status", success ? "success" : "failure"
        ).increment();

        if (success) {
            meterRegistry.timer(
                    "pdf.s3_upload_time",
                    "form", formName
            ).record(millis, java.util.concurrent.TimeUnit.MILLISECONDS);
        }
    }

    public void recordError(String reportName, String errorType, Exception e) {
        meterRegistry.counter(
                "pdf.errors",
                "reportName", reportName,
                "error_type", errorType,
                "exception", e.getClass().getSimpleName()
        ).increment();
        log.error("Error in PDF:  '{}': {}", reportName, errorType, e);
    }

}
