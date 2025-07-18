package com.zikpak.facecheck.metrics;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class MetricsWorkSiteService {

    private final MeterRegistry meterRegistry;

    /**
     * Поиск площадки по ID.
     */
    public void recordWorkSiteById(String worksiteName, Integer worksiteId, boolean success) {
        meterRegistry.counter("worksite.find",
                "worksite_id", worksiteId.toString(),
                "status",     success ? "success" : "failure"
        ).increment();
    }

    /**
     * Проверка пользовательского радиуса для конкретного работника.
     */
    public void recordForWorkerRadius(String worksiteName,
                                      String companyName,
                                      String workerName,
                                      Integer worksiteId,
                                      double radius,
                                      boolean success) {
        meterRegistry.counter("worksite.worker_radius.check",
                "worksite_id", worksiteId.toString(),
                "worker_name", workerName,
                "status",      success ? "success" : "failure"
        ).increment();

        if (success) {
            meterRegistry.summary("worksite.worker_radius.meters",
                    "worksite_id", worksiteId.toString()
            ).record(radius);
        }
    }

    /**
     * Общая проверка радиуса площадки.
     */
    public void recordWorkSiteRadius(String worksiteName,
                                     String companyName,
                                     Integer worksiteId,
                                     double radius,
                                     boolean success) {
        meterRegistry.counter("worksite.radius.check",
                "worksite_id", worksiteId.toString(),
                "status",      success ? "success" : "failure"
        ).increment();

        if (success) {
            meterRegistry.summary("worksite.radius.meters",
                    "worksite_id", worksiteId.toString()
            ).record(radius);
        }
    }

    /**
     * Получение координат площадки.
     */
    public void recordWorkSiteLatLon(String worksiteName,
                                     String companyName,
                                     Integer worksiteId,
                                     double latitude,
                                     double longitude,
                                     boolean success) {
        meterRegistry.counter("worksite.location.fetch",
                "worksite_id", worksiteId.toString(),
                "status",      success ? "success" : "failure"
        ).increment();

        if (success) {
            meterRegistry.summary("worksite.location.latitude",
                    "worksite_id", worksiteId.toString()
            ).record(latitude);
            meterRegistry.summary("worksite.location.longitude",
                    "worksite_id", worksiteId.toString()
            ).record(longitude);
        }
    }

    /**
     * Логирование отдельных значений lat/lon (напр. при расчете дистанции).
     */
    public void recordDistance(double lat1,
                               double lon1,
                               boolean success) {
        meterRegistry.counter("worksite.distance.measure",
                "status", success ? "success" : "failure"
        ).increment();

        if (success) {
            meterRegistry.summary("worksite.distance.lat").record(lat1);
            meterRegistry.summary("worksite.distance.lon").record(lon1);
        }
    }

    /**
     * Запись ошибок.
     */
    public void recordError(String operation, String errorType, Exception e) {
        meterRegistry.counter("worksite.errors",
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
        sample.stop(meterRegistry.timer("worksite.operation.duration",
                "operation", operation
        ));
    }
}
