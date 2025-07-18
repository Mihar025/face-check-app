package com.zikpak.facecheck.metrics;


import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class MetricsAmazonS3Service {


    private final MeterRegistry meterRegistry;


    public void recordError(String operation, String errorType, Exception e) {
        meterRegistry.counter("amazonS3.errors",
                "operation",   operation,
                "error_type",  errorType,
                "exception",   e.getClass().getSimpleName()
        ).increment();
        log.error("Error in {}: {} — {}", operation, errorType, e.getMessage(), e);
    }

    /**
     * Таймер начала операции.
     */
    public Timer.Sample startTimer() {
        return Timer.start(meterRegistry);
    }

    /**
     * Фиксация длительности операции.
     */
    public void recordOperationTime(Timer.Sample sample, String operation) {
        sample.stop(meterRegistry.timer("amazonS3.operation.duration",
                "operation", operation
        ));
    }
}
