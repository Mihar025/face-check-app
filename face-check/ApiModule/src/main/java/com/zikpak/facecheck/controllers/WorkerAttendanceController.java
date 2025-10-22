package com.zikpak.facecheck.controllers;

import com.zikpak.facecheck.entity.User;
import com.zikpak.facecheck.requestsResponses.*;
import com.zikpak.facecheck.requestsResponses.attendance.*;
import com.zikpak.facecheck.requestsResponses.worker.FinanceInfoForWeekInFinanceScreenResponse;
import com.zikpak.facecheck.services.workAttendanceService.WorkAttendanceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("attendance")
@RequiredArgsConstructor
public class WorkerAttendanceController {

    private final WorkAttendanceService workAttendanceService;

    @PostMapping("/punch-in")
    public ResponseEntity<PunchInResponse> punchIn(
            @Valid @RequestBody PunchInRequest request,
            Authentication authentication) {
        long start = System.currentTimeMillis();
        PunchInResponse response = workAttendanceService.makePunchIn(authentication, request);
        long end = System.currentTimeMillis();
        System.out.println("Время выполнения запроса: " + (end - start) + " мс");
        return response.getIsSuccessful()
                ? ResponseEntity.ok(response)
                : ResponseEntity.badRequest().body(response);
    }

    @PostMapping("/punch-out")
    public ResponseEntity<PunchOutResponse> punchOut(
            @Valid @RequestBody PunchOutRequest request,
            Authentication authentication) {
        long start = System.currentTimeMillis();
        PunchOutResponse response = workAttendanceService.makePunchOut(authentication, request);
        long end = System.currentTimeMillis();
        System.out.println("Время выполнения запроса: " + (end - start) + " мс");
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


    @PostMapping("/overtime/add")
    public ResponseEntity<OvertimeResponse> addManualOvertime(
            Authentication authentication,
            @Valid @RequestBody AddOvertimeRequest request
    ) {
        User admin = (User) authentication.getPrincipal();

        OvertimeResponse response = workAttendanceService.addManualOvertime(
                request.getAttendanceId(),
                request.getOvertimeHours(),
                request.getReason(),
                admin.getId()
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/find-all/attendance/app-owner")
    public ResponseEntity<PageResponse<AttendanceResponse>> getAllAttendanceForAppOwner(
            Authentication authentication,
            @RequestParam(name = "page", defaultValue = "0", required = false) int page,
            @RequestParam(name = "size", defaultValue = "10", required = false) int size
    ) {
        return ResponseEntity.ok(
                workAttendanceService.findAllAttendanceAppOwner(authentication, page, size)
        );
    }

    @GetMapping("/find-all/attendance/admin")
    public ResponseEntity<PageResponse<AttendanceResponse>> getAllAttendanceForAdmin(
            Authentication authentication,
            @RequestParam(name = "page", defaultValue = "0", required = false) int page,
            @RequestParam(name = "size", defaultValue = "10", required = false) int size
    ) {
        return ResponseEntity.ok(
                workAttendanceService.findAllAttendanceAdmin(authentication, page, size)
        );
    }

    @GetMapping("/photos/{workerId}")
    public ResponseEntity<WorkerPhotosResponse> getWorkerPhotosByDate(
            @PathVariable Integer workerId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        WorkerPhotosResponse response = workAttendanceService
                .getPhotosForWorkerByDate(workerId, date);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/photos/attendance/{attendanceId}")
    public ResponseEntity<WorkerPhotosResponse> getPhotosByAttendanceId(
            @PathVariable Integer attendanceId) {

        WorkerPhotosResponse response = workAttendanceService
                .getPhotosByAttendanceId(attendanceId);

        return ResponseEntity.ok(response);
    }


    @GetMapping("/has-punch-in/{workerId}")
    public ResponseEntity<Boolean> hasPunchIn(@PathVariable(name = "workerId") Integer workerId,  Authentication authentication) {
        return ResponseEntity.ok(workAttendanceService.isWorkerHasPunchInToday(workerId, authentication));
    }

}