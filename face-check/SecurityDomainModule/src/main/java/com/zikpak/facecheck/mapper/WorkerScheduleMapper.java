package com.zikpak.facecheck.mapper;

import com.zikpak.facecheck.entity.employee.WorkerSchedule;
import com.zikpak.facecheck.requestsResponses.schedule.WorkerScheduleResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WorkerScheduleMapper {

    public WorkerScheduleResponse toWorkerScheduleResponse(WorkerSchedule schedule) {
        return WorkerScheduleResponse.builder()
                .workerId(schedule.getWorker().getId())
                .workSiteName(schedule.getWorkSite().getSiteName())
                .expectedStartTime(schedule.getExpectedStartTime())
                .expectedEndTime(schedule.getExpectedEndTime())
                .shift(schedule.getShift())
                .isOnDuty(schedule.getIsOnDuty())
                .build();
    }

    public WorkerSchedule toWorkerSchedule(WorkerScheduleResponse workerScheduleResponse) {
        return WorkerSchedule.builder()
                .id(workerScheduleResponse.getWorkerId())
                .expectedStartTime(workerScheduleResponse.getExpectedStartTime())
                .expectedEndTime(workerScheduleResponse.getExpectedEndTime())
                .shift(workerScheduleResponse.getShift())
                .isOnDuty(workerScheduleResponse.getIsOnDuty())
                .build();
    }


}
