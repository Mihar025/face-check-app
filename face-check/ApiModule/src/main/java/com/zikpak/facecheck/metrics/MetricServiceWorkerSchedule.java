package com.zikpak.facecheck.metrics;


import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class MetricServiceWorkerSchedule {

    private final MeterRegistry meterRegistry;

    public void recordWorkersDaysHours(String workerName,
                                        double totalHours,
                                        double regularHours,
                                        double overtimeHours,
                                        boolean success){
        meterRegistry.counter("schedule.workers_days_hours",
                "worker", workerName,
                "success", success ? "success" : "fail").increment();
        if(success) {
            meterRegistry.summary("schedule.totalHours").record(totalHours);
            meterRegistry.summary("schedule.regularHours").record(regularHours);
            meterRegistry.summary("schedule.overtimeHours").record(overtimeHours);
        }
        }

    public void recordNewWorkerSchedule(boolean success){
        meterRegistry.counter("schedule.new_worker","success", success ? "success" : "fail").increment();
    }



    public void recordScheduleError(String operation, String errorType, Exception e){
        meterRegistry.counter("schedule.errors",
                "operation", operation,
                "errorType", errorType,
                "exception", e.getClass().getSimpleName()).increment();
        log.error("Error in {}: {} - {}", operation, errorType, e);
    }


    public Timer.Sample startTimer(){
        return Timer.start(meterRegistry);
    }


    public void recordOperationTime(Timer.Sample sample, String operation) {
        sample.stop(meterRegistry.timer("schedule.operation_time", "operation", operation));
    }

}
