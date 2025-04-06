package com.zikpak.facecheck.domain;

import com.zikpak.facecheck.requestsResponses.schedule.WeeklyScheduleResponse;
import com.zikpak.facecheck.requestsResponses.schedule.WorkerHourResponse;
import com.zikpak.facecheck.requestsResponses.schedule.WorkerSetScheduleRequest;
import com.zikpak.facecheck.requestsResponses.workScheduler.WorkSchedulerResponse;
import org.springframework.security.core.Authentication;

import java.time.LocalDate;

public interface WorkerScheduleI {
    WeeklyScheduleResponse findWorkerAllDaysAndAllHours(Authentication authentication, LocalDate weekDate);
    WorkSchedulerResponse setScheduleForWorkerScenario(Integer workerId, WorkerSetScheduleRequest workerSetScheduleRequest);
    WorkerHourResponse calculateWorkerTotalHoursPerWeek(Authentication authentication);
    WorkerHourResponse calculateWorkerTotalHoursForSpecialWeek(Authentication authentication, LocalDate weekDate );


}

