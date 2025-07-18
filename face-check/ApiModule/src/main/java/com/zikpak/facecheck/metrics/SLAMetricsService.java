package com.zikpak.facecheck.metrics;


import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
@Slf4j
public class SLAMetricsService {

    private final MeterRegistry meterRegistry;

    public void recordHttpRequest(String endpoint, String method, int statusCode, long responseTimeMs ){
        boolean isSuccess = statusCode >= 200 && statusCode < 400;

        meterRegistry.counter("http.requests.total",
                "endpoint", endpoint,
                "method", method,
                "status", String.valueOf(statusCode),
                "status_class", getStatusClass(statusCode)).increment();

        meterRegistry.counter("sla.requests",
                "endpoint", endpoint,
                "method", method,
                "success", String.valueOf(isSuccess)).increment();


        meterRegistry.timer("http.requests.duration",
                "endpoint", endpoint,
                "method", method,
                "status", String.valueOf(statusCode)).record(responseTimeMs, TimeUnit.MILLISECONDS);

        if(responseTimeMs > 1000){
            meterRegistry.counter("http.requests.slow",
                    "endpoint", endpoint,
                    "method", method
            ).increment();

            if(responseTimeMs > 3000){
                log.warn("Slow request detected: {} {} took {} ms", method, endpoint , responseTimeMs);
            }
        }
    }

    public void recordDependencyAvailability(String serviceName, boolean isAvailable){
        meterRegistry.gauge("dependency.availability",
                Tags.of("service", serviceName),
                isAvailable ? 1.0 : 0.0);

        if(!isAvailable){
            meterRegistry.counter("dependency.failures",
                    "service", serviceName).increment();
            log.error("Dependency is not available: {}", serviceName);
        }
    }

    public void recordBusinessOperations(String operationName, boolean success, long durationMs){

        meterRegistry.counter("business.operation.sla",
                "operation", operationName,
                "success", String.valueOf(success)).increment();

        meterRegistry.timer("business.operation.duration",
                "operation", operationName,
                "success", String.valueOf(success)).record(durationMs, TimeUnit.MILLISECONDS);

        if(!success && isCriticalOperation(operationName)){
            meterRegistry.counter("critical.failures",
                    "operation", operationName).increment();
            log.error("Critical business operation failure: {}, time: {} ms", operationName, durationMs);
        }
    }

    public void recordHealthCheck(String componentName, boolean isHealthy){
        meterRegistry.gauge("health.status",
                Tags.of("component", componentName),
                isHealthy ? 1.0 : 0.0);
    }

    private String getStatusClass(int statusCode) {
        if (statusCode >= 200 && statusCode < 300) return "2xx";
        if (statusCode >= 300 && statusCode < 400) return "3xx";
        if (statusCode >= 400 && statusCode < 500) return "4xx";
        if (statusCode >= 500) return "5xx";
        return "unknown";
    }

    private boolean isCriticalOperation(String operationName) {
        // Определяем критические операции
        return operationName.contains("payment") ||
                operationName.contains("payroll") ||
                operationName.contains("tax");
    }

}
