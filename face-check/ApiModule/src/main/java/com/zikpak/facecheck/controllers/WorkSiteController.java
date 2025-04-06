package com.zikpak.facecheck.controllers;

import com.zikpak.facecheck.requestsResponses.PageResponse;
import com.zikpak.facecheck.requestsResponses.workSite.WorkSiteClosedDaysResponse;
import com.zikpak.facecheck.requestsResponses.workSite.WorkSiteRequest;
import com.zikpak.facecheck.requestsResponses.workSite.WorkSiteResponse;
import com.zikpak.facecheck.requestsResponses.workSite.WorkerCurrentlyWorkingInWorkSite;
import com.zikpak.facecheck.requestsResponses.workSite.data.*;
import com.zikpak.facecheck.requestsResponses.workSite.selectWorkSite.SelectWorkSiteResponse;
import com.zikpak.facecheck.requestsResponses.workSite.updates.*;
import com.zikpak.facecheck.services.workSiteService.WorkSiteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("workSite")
@RequiredArgsConstructor
@Tag(name = "Work Site Controller", description = "API for working with Work Sites")
public class WorkSiteController {
    private final WorkSiteService workSiteService;

    //working
    @GetMapping("/{id}")
    @Operation(summary = "Find work site by ID")
    public ResponseEntity<WorkSiteResponse> findWorkSiteById(@PathVariable Integer id) {
        return ResponseEntity.ok(workSiteService.findWorkSiteById(id));
    }

    @GetMapping
    @Operation(summary = "Find all work sites with pagination")
    public ResponseEntity<PageResponse<WorkSiteResponse>> findAllWorkSites(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(workSiteService.findAllWorkSites(page, size));
    }
    //working
    @PostMapping("/select/{workSiteId}")
    @Operation(summary = "Select work site")
    public ResponseEntity<SelectWorkSiteResponse> selectWorkSite(
            @PathVariable Integer workSiteId,
            Authentication authentication) {
        workSiteService.selectWorkSite(workSiteId, authentication);
        return ResponseEntity.ok().build();
    }
    // Working
    @PostMapping
    @Operation(summary = "Create new work site")
    public ResponseEntity<WorkSiteResponse> createWorkSite(
            Authentication authentication,
            @RequestBody @Valid WorkSiteRequest request) {
        return ResponseEntity.ok(workSiteService.createWorkSite(authentication, request));
    }

    //Working
    @PostMapping("/{workSiteId}/custom-radius")
    @Operation(summary = "Set special radius ")
    public ResponseEntity<SetNewCustomRadiusResponse> setCustomRadius(
            Authentication authentication,
            @PathVariable Integer workSiteId,
            @RequestBody @Valid SetNewCustomRadiusRequest customRadius) {
        workSiteService.setCustomRadiusForWorkSite(
                authentication, workSiteId,customRadius);
        return ResponseEntity.ok().build();
    }

    //Working
    @PatchMapping("/{workSiteId}/name")
    @Operation(summary = "Update name for work site")
    public ResponseEntity<WorkSiteUpdateNameResponse> updateName(
            Authentication authentication,
            @PathVariable Integer workSiteId,
            @RequestBody UpdateNameRequest request) {
        return ResponseEntity.ok(workSiteService.updateWorkSiteName(authentication, workSiteId, request));
    }


    //Working
    @PatchMapping("/{workSiteId}/address")
    @Operation(summary = "Update address for work site")
    public ResponseEntity<WorkSiteUpdateAddressResponse> updateAddress(
            Authentication authentication,
            @PathVariable Integer workSiteId,
            @RequestBody UpdateWorkSiteAddress updateWorkSiteAddress) {
        return ResponseEntity.ok(workSiteService.updateWorkSiteAddress(authentication, workSiteId, updateWorkSiteAddress));
    }

    //Working
    @PatchMapping("/{workSiteId}/working-hours")
    @Operation(summary = "Update working hours work site")
    public ResponseEntity<WorkSiteUpdateWorkingHoursResponse> updateWorkingHours(
            Authentication authentication,
            @PathVariable Integer workSiteId,
            @RequestBody @Valid WorkSiteUpdateWorkingHoursRequest request) {
        return ResponseEntity.ok(workSiteService.updateWorkingHours(authentication, workSiteId, request));
    }

    //Working
    @PatchMapping("/{workSiteId}/location")
    @Operation(summary = "Update location of work site")
    public ResponseEntity<WorkSiteUpdateLocationResponse> updateLocation(
            Authentication authentication,
            @PathVariable Integer workSiteId,
            @RequestBody @Valid WorkSiteUpdateLocationRequest request) {
        return ResponseEntity.ok(workSiteService.updateWorkSiteLocation(authentication, workSiteId, request));
    }


    //Working
    @PatchMapping("/{workSiteId}/active")
    @Operation(summary = "Change status of work site activity")
    public ResponseEntity<Void> setActive(
            Authentication authentication,
            @PathVariable Integer workSiteId,
            @RequestBody UpdateStatusWorkSiteRequest updateStatusWorkSiteRequest) {
        workSiteService.setWorkSiteActiveOrNotActive(authentication, workSiteId, updateStatusWorkSiteRequest);
        return ResponseEntity.ok().build();
    }



    //Working
    @PostMapping("/{workSiteId}/inactive-day")
    @Operation(summary = "Schedule vacation day")
    public ResponseEntity<ScheduleInactiveDayResponse> scheduleInactiveDay(
            Authentication authentication,
            @PathVariable Integer workSiteId,
            @RequestBody @Valid ScheduleInactiveDayRequest request) {
        return ResponseEntity.ok(workSiteService.scheduleInactiveDay(authentication, workSiteId, request));
    }


    //Working
    @DeleteMapping("/{workSiteId}/inactive-day")
    @Operation(summary = "Remove inactive day")
    public ResponseEntity<ScheduleInactiveDayResponse> removeInactiveDay(
            Authentication authentication,
            @PathVariable Integer workSiteId,
            @RequestBody @Valid ScheduleInactiveDayRequest request) {
        workSiteService.removeInactiveDay(authentication, workSiteId, request);
        return ResponseEntity.ok().build();
    }

    //Working
    @GetMapping("/{workSiteId}/active")
    @Operation(summary = "Check is work site active")
    public ResponseEntity<Boolean> isActive(@PathVariable Integer workSiteId) {
        return ResponseEntity.ok(workSiteService.isWorkSiteActive(workSiteId));
    }

    //Working
    @PostMapping("/{workSiteId}/within-radius")
    @Operation(summary = "Check is user in correct work site radius")
    public ResponseEntity<IsWithinRadiusResponse> isWithinRadius(
            @PathVariable Integer workSiteId,
            @RequestBody @Valid IsWithinRadiusRequest request) {
        return ResponseEntity.ok(workSiteService.isWithinRadius(workSiteId, request));
    }


    //Working
    @PostMapping("/{workSiteId}/can-punch/{userId}")
    @Operation(summary = "Check is it correct time to punch in/out")
    public ResponseEntity<Boolean> canPunchInOut(
            Authentication authentication,
            @PathVariable Integer workSiteId,
            @PathVariable Integer userId,
            @RequestBody CanPunchOutRequestWorkSite canPunchOutRequestWorkSite) {
        return ResponseEntity.ok(workSiteService.canPunchInOut(authentication, workSiteId, userId, canPunchOutRequestWorkSite));
    }

    //Working
    @GetMapping("/{workSiteId}/closed-days")
    @Operation(summary = "Get all closed days in special work site")
    public ResponseEntity<PageResponse<WorkSiteClosedDaysResponse>> getWorkSiteClosedDays(
            @PathVariable Integer workSiteId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication authentication) {
        return ResponseEntity.ok(
                workSiteService.findWorkSiteClosedDays(workSiteId, page, size, authentication)
        );
    }

    @PostMapping("/{workSiteId}/custom-radius/{workerId}")
    @Operation(summary = "Set custom radius for specific worker on work site")
    public ResponseEntity<SetNewCustomRadiusForWorkerInSpecialWorkSiteResponse> setCustomRadiusForWorkerInSpecialWorkSite(
            @PathVariable Integer workSiteId,
            @PathVariable Integer workerId,
            @Valid @RequestBody SetNewCustomRadiusRequest request,
            Authentication authentication
    ) {
        return ResponseEntity.ok(
                workSiteService.setCustomRadiusForWorkerInSpecialWorkSite(workSiteId, workerId, request, authentication)
        );
    }



    //todo tomorrow figure out what wrong with start day time!
    @GetMapping("/{workSiteId}/active-workers")
    @Operation(summary = "Get list of workers whose working right now in worksite and related to special company")
    public ResponseEntity<PageResponse<WorkerCurrentlyWorkingInWorkSite>> getActiveWorkers(
            @PathVariable Integer workSiteId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication authentication) {
        return ResponseEntity.ok(
                workSiteService.findAllWorkerInWorkSiteRelated(workSiteId, page, size, authentication)
        );
    }

}