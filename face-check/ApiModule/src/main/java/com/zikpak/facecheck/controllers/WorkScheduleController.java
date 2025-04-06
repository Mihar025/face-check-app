package com.zikpak.facecheck.controllers;

import com.zikpak.facecheck.requestsResponses.schedule.WeeklyScheduleResponse;
import com.zikpak.facecheck.requestsResponses.schedule.WorkerHourResponse;
import com.zikpak.facecheck.requestsResponses.schedule.WorkerSetScheduleRequest;
import com.zikpak.facecheck.requestsResponses.workScheduler.WorkSchedulerResponse;
import com.zikpak.facecheck.services.workSchedule.WorkerScheduleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/schedule")
@RequiredArgsConstructor
public class WorkScheduleController {
    private final WorkerScheduleService workerScheduleService;
    //working
    @GetMapping("/week")
    public ResponseEntity<WeeklyScheduleResponse> getWeekSchedule(
            Authentication authentication,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        return ResponseEntity.ok(
                workerScheduleService.findAllDaysAndHoursForCurrentWeekOrSpecialWeek(authentication, date)
        );
    }

    //working
    @PostMapping("/worker/{workerId}")
    public ResponseEntity<WorkSchedulerResponse> setWorkerSchedule(
        //    Authentication authentication,
            @PathVariable Integer workerId,
            @Valid @RequestBody WorkerSetScheduleRequest request
    ) {

        return ResponseEntity.ok( workerScheduleService.setScheduleForWorker(workerId, request));
    }
    //working
    @GetMapping("/hours/current-week")
    public ResponseEntity<WorkerHourResponse> getCurrentWeekHours(Authentication authentication) {
        return ResponseEntity.ok(
                workerScheduleService.findTotalHoursPerWeek(authentication)
        );
    }
    //Working
    @GetMapping("/hours/special-week")
    public ResponseEntity<WorkerHourResponse> getSpecialWeekHours(
            Authentication authentication,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        return ResponseEntity.ok(
                workerScheduleService.findTotalHoursPerSpecialWeek(authentication, date)
        );
    }
}