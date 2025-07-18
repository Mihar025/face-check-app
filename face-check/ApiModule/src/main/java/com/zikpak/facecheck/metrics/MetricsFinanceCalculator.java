package com.zikpak.facecheck.metrics;


import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class MetricsFinanceCalculator {

    private final MeterRegistry meterRegistry;

    public Timer.Sample startTimer() {
        return Timer.start(meterRegistry);
    }

    public void recordOperationTime(Timer.Sample sample, String operation) {
        sample.stop(meterRegistry.timer(
                "finance.operation.duration",
                "operation", operation
        ));
    }

    public void recordCalculationCall(String calculationName) {
        meterRegistry.counter(
                "finance.calculation.calls",
                "calculation", calculationName
        ).increment();
    }

    public void recordTaxAmount(String calculationName, double amount) {
        meterRegistry.summary(
                "finance.calculation.amount",
                "calculation", calculationName
        ).record(amount);
    }


    public void recordError(String calculationName, String errorType, Exception e) {
        meterRegistry.counter(
                "finance.calculation.errors",
                "calculation", calculationName,
                "error_type", errorType,
                "exception", e.getClass().getSimpleName()
        ).increment();
        log.error("Error in finance calculation '{}': {}", calculationName, errorType, e);
    }




}
