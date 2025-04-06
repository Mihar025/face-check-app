package com.zikpak.facecheck.services.workSchedule;

import com.zikpak.facecheck.helperServices.WorkerScheduleServiceImpl;
import com.zikpak.facecheck.requestsResponses.schedule.WeeklyScheduleResponse;
import com.zikpak.facecheck.requestsResponses.schedule.WorkerHourResponse;
import com.zikpak.facecheck.requestsResponses.schedule.WorkerSetScheduleRequest;
import com.zikpak.facecheck.requestsResponses.workScheduler.WorkSchedulerResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class WorkerScheduleService {

    private final WorkerScheduleServiceImpl workerScheduleService;

    public WeeklyScheduleResponse findAllDaysAndHoursForCurrentWeekOrSpecialWeek(Authentication authentication, LocalDate date) {
        return workerScheduleService.findWorkerAllDaysAndAllHours(authentication, date);
    }

    public WorkSchedulerResponse setScheduleForWorker( Integer workerId, WorkerSetScheduleRequest workerSetScheduleRequest) {
       return workerScheduleService.setScheduleForWorkerScenario(workerId, workerSetScheduleRequest);
    }

    public WorkerHourResponse findTotalHoursPerWeek(Authentication authentication){
        return workerScheduleService.calculateWorkerTotalHoursPerWeek(authentication);
    }

    public WorkerHourResponse findTotalHoursPerSpecialWeek(Authentication authentication, LocalDate date){
        return workerScheduleService.calculateWorkerTotalHoursForSpecialWeek(authentication, date);
    }





}
