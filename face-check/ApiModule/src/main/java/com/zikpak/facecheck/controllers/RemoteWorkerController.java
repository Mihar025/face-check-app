package com.zikpak.facecheck.controllers;


import com.zikpak.facecheck.requestsResponses.PageResponse;
import com.zikpak.facecheck.requestsResponses.attendance.PunchInRequest;
import com.zikpak.facecheck.requestsResponses.attendance.PunchInResponse;
import com.zikpak.facecheck.services.remoteWorkerService.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("remote-worker")
@RequiredArgsConstructor
public class RemoteWorkerController {

    private final RemoteWorkerService remoteWorkerService;



    @PatchMapping("/set-worker-remote/{workerId}")
    public ResponseEntity<String> setWorkerRemote(Authentication authentication,
                                                @PathVariable(name = "workerId") Integer workerId)
                                                throws Exception {

         remoteWorkerService.setWorkerAsRemoteWorker(authentication, workerId);

         String responseBody = "Successfully set worker as remote worker";
         return new ResponseEntity<>(responseBody, HttpStatus.OK);
    }


    @PatchMapping("/set-worker-on-person/{workerId}")
    public ResponseEntity<String> setWorkerOnPerson(Authentication authentication,
                                                  @PathVariable(name = "workerId") Integer workerId)
            throws Exception {

        remoteWorkerService.setWorkerAsNotRemoteWorker(authentication, workerId);

        String responseBody = "Successfully set worker as not remote worker";
        return new ResponseEntity<>(responseBody, HttpStatus.OK);
    }

    @PostMapping("/random-attendance-verification")
    public ResponseEntity<RandonAttendanceVerificationResponse> completeRandomAttendanceVerification(
            Authentication authentication,
            @Valid @RequestBody RandonAttendanceVerificationRequest request) {

        RandonAttendanceVerificationResponse response =
                remoteWorkerService.completeRandomAttendanceVerification(authentication, request);
        return response.getIsSuccessful()
                ? ResponseEntity.ok(response)
                : ResponseEntity.badRequest().body(response);
    }

    @GetMapping("/pending-verification")
    public ResponseEntity<RandonAttendanceVerificationResponse> getPendingVerification(
            Authentication authentication) {
        return ResponseEntity.ok(
                remoteWorkerService.getPendingVerification(authentication));
    }


    @GetMapping("/admin/verifications")
    public ResponseEntity<PageResponse<RemoteVerificationAdminResponse>> getAllVerifications(
            Authentication authentication,
            @RequestParam(required = false) Integer companyId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String dateFrom,
            @RequestParam(required = false) String dateTo,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        PageResponse<RemoteVerificationAdminResponse> response =
                remoteWorkerService.getAllVerificationsForAdmin(
                        authentication, companyId, status, dateFrom, dateTo, page, size);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/admin/verifications/stats")
    public ResponseEntity<RemoteVerificationStatsResponse> getVerificationStats(
            Authentication authentication,
            @RequestParam(required = false) Integer companyId) {

        RemoteVerificationStatsResponse stats =
                remoteWorkerService.getVerificationStats(authentication, companyId);
        return ResponseEntity.ok(stats);
    }



}
