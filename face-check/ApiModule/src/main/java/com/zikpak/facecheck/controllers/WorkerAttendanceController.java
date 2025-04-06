package com.zikpak.facecheck.controllers;

import com.zikpak.facecheck.requestsResponses.attendance.*;
import com.zikpak.facecheck.requestsResponses.worker.FinanceInfoForWeekInFinanceScreenResponse;
import com.zikpak.facecheck.services.workAttendanceService.WorkAttendanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("attendance")
@RequiredArgsConstructor
public class WorkerAttendanceController {

    private final WorkAttendanceService workAttendanceService;

    @PostMapping("/punch-in")
    public ResponseEntity<PunchInResponse> punchIn(
            @RequestBody PunchInRequest request,
            Authentication authentication) {
        PunchInResponse response = workAttendanceService.makePunchIn(authentication, request);

        return response.getIsSuccessful()
                ? ResponseEntity.ok(response)
                : ResponseEntity.badRequest().body(response);
    }

    @PostMapping("/punch-out")
    public ResponseEntity<PunchOutResponse> punchOut(
            @RequestBody PunchOutRequest request,
            Authentication authentication) {
        PunchOutResponse response = workAttendanceService.makePunchOut(authentication, request);

        return response.getIsSuccessful()
                ? ResponseEntity.ok(response)
                : ResponseEntity.badRequest().body(response);
    }

    @GetMapping("/last-punch")
    public ResponseEntity<LastPunchTimeDTO> getLastPunchTime(Authentication authentication) {
        LastPunchTimeDTO lastPunchInfo = workAttendanceService.getLastPunchTime(authentication);
        return ResponseEntity.ok(lastPunchInfo);
    }

    @GetMapping("/week")
    public ResponseEntity<List<DailyEarningResponse>> getWeeklyEarnings(Authentication authentication) {
        return ResponseEntity.ok(workAttendanceService.getCurrentWeekEarnings(authentication));
    }

    @GetMapping("/finance-info")
    public ResponseEntity<FinanceInfoForWeekInFinanceScreenResponse> getFinanceInfoForWeek(
            Authentication authentication,
            @RequestParam("weekStart") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate weekStart) {
        return ResponseEntity.ok(
                workAttendanceService.getFinanceInfoForFinanceScreen(authentication, weekStart)
        );
    }



}