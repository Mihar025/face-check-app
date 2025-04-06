package com.zikpak.facecheck.controllers;



import com.zikpak.facecheck.requestsResponses.PageResponse;
import com.zikpak.facecheck.requestsResponses.admin.*;
import com.zikpak.facecheck.services.ForemanAndAdminFunctional.ForemanAndAdminService;
import com.zikpak.facecheck.requestsResponses.worker.UpdatePunchInForWorkerResponse;
import com.zikpak.facecheck.services.adminService.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;
    private final ForemanAndAdminService foremanAndAdminService;



    @PostMapping("/worker/{workerId}/punch-in")
    public ResponseEntity<ChangePunchInForWorkerResponse> changePunchInForWorker(
            @PathVariable Integer workerId,
            @RequestBody ChangePunchInRequest request,
            Authentication authentication) {

        return ResponseEntity.ok(
                adminService.ChangingPunchInForWorkerIfDoesntExist(
                        workerId,
                        request,
                        authentication
                )
        );
    }

    @PostMapping("/worker/{workerId}/punch-out")
    public ResponseEntity<ChangePunchOutForWorkerResponse> changePunchOutForWorker(
            @PathVariable Integer workerId,
            @RequestBody ChangePunchOutRequest request,
            Authentication authentication) {

        return ResponseEntity.ok(
                adminService.ChangingPunchOutForWorkerIfDoesntExist(
                        workerId,
                        request,
                        authentication
                )
        );
    }
    //working
    @GetMapping("/employee")
    @Operation(summary = "Get all workers in worksite", description = "Retrieves paginated list of workers for a specific worksite")
    public ResponseEntity<PageResponse<WorksiteWorkerResponse>> getWorkersInWorksite(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam Integer worksiteId,
            Authentication authentication
    ) {
        return ResponseEntity.ok(
                foremanAndAdminService.findAllWorkersInWorkSite(page, size, worksiteId, authentication)
        );
    }
    //working
    @DeleteMapping("/workers/{workerId}/punch-in")
    public ResponseEntity<Void> deleteWorkerPunchIn(
            @PathVariable Integer workerId,
            Authentication authentication
    ) {
        foremanAndAdminService.deleteWorkerPunchIn(authentication, workerId);
        return ResponseEntity.noContent().build();
    }
    //working
    @PutMapping("/worker/{workerId}/punch-in")
    public ResponseEntity<UpdatePunchInForWorkerResponse> updatePunchInTime(
            Authentication authentication,
            @PathVariable Integer workerId,
            @RequestBody PunchInUpdateRequest request) {

        UpdatePunchInForWorkerResponse response = foremanAndAdminService.updateLatestPunchInForWorkerResponse(
                authentication,
                workerId,
                request.getNewCheckInTIme()
        );

        return ResponseEntity.ok(response);
    }


}
