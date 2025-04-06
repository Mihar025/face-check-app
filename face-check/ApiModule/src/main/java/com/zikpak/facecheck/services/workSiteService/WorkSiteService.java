package com.zikpak.facecheck.services.workSiteService;


import com.zikpak.facecheck.helperServices.WorkSiteServiceImpl;
import com.zikpak.facecheck.requestsResponses.PageResponse;
import com.zikpak.facecheck.requestsResponses.workSite.WorkSiteClosedDaysResponse;
import com.zikpak.facecheck.requestsResponses.workSite.WorkSiteRequest;
import com.zikpak.facecheck.requestsResponses.workSite.WorkSiteResponse;
import com.zikpak.facecheck.requestsResponses.workSite.WorkerCurrentlyWorkingInWorkSite;
import com.zikpak.facecheck.requestsResponses.workSite.data.*;
import com.zikpak.facecheck.requestsResponses.workSite.selectWorkSite.SelectWorkSiteResponse;
import com.zikpak.facecheck.requestsResponses.workSite.updates.*;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalTime;

@Service
@AllArgsConstructor
public class WorkSiteService {

    private final WorkSiteServiceImpl workSiteService;

    public WorkSiteResponse findWorkSiteById(Integer id) {
        return workSiteService.findWorkSiteById(id);
    }

    public PageResponse<WorkSiteResponse> findAllWorkSites(int page, int size) {
        return workSiteService.findAllWorkSites(page, size);
    }

    public SelectWorkSiteResponse selectWorkSite(Integer workSiteId, Authentication authentication) {
        return workSiteService.selectWorkSite(workSiteId, authentication);
    }

    public WorkSiteResponse createWorkSite(Authentication authentication, WorkSiteRequest request) {
        return workSiteService.createWorkSite(authentication, request);
    }

    public SetNewCustomRadiusResponse setCustomRadiusForWorkSite(Authentication authentication,
                                                          Integer workSiteId,
                                                          SetNewCustomRadiusRequest customRadius) {
       return workSiteService.setCustomRadiusForWorkSite(authentication, workSiteId,  customRadius);
    }

    public SetNewCustomRadiusForWorkerInSpecialWorkSiteResponse setCustomRadiusForWorkerInSpecialWorkSite(Integer workSiteId,
                                                                                                          Integer workerId,
                                                                                                          SetNewCustomRadiusRequest setNewCustomRadiusRequest,
                                                                                                          Authentication authentication){
        return workSiteService.setNewCustomRadiusForWorker(workSiteId, workerId, setNewCustomRadiusRequest, authentication);
    }

    public WorkSiteUpdateNameResponse updateWorkSiteName(Authentication authentication,
                                                         Integer workSiteId,
                                                         UpdateNameRequest updateNameRequest) {
        return workSiteService.updateWorkSiteName(authentication, workSiteId, updateNameRequest);
    }

    public WorkSiteUpdateAddressResponse updateWorkSiteAddress(Authentication authentication,
                                                               Integer workSiteId,
                                                               UpdateWorkSiteAddress newAddress) {
        return workSiteService.updateWorkSiteAddress(authentication, workSiteId, newAddress);
    }

    public WorkSiteUpdateWorkingHoursResponse updateWorkingHours(Authentication authentication,
                                                                 Integer workSiteId,
                                                                 WorkSiteUpdateWorkingHoursRequest request) {
        return workSiteService.updateWorkingHours(authentication, workSiteId, request);
    }

    public WorkSiteUpdateLocationResponse updateWorkSiteLocation(Authentication authentication,
                                                                 Integer workSiteId,
                                                                 WorkSiteUpdateLocationRequest request) {
        return workSiteService.updateWorkSiteLocation(authentication, workSiteId, request);
    }

    public void setWorkSiteActiveOrNotActive(Authentication authentication,
                                             Integer workSiteId,
                                             UpdateStatusWorkSiteRequest updateStatusWorkSiteRequest) {
        workSiteService.setWorkSiteActiveOrNotActive(authentication, workSiteId, updateStatusWorkSiteRequest);
    }

    public ScheduleInactiveDayResponse scheduleInactiveDay(Authentication authentication,
                                                           Integer workSiteId,
                                                           ScheduleInactiveDayRequest inactiveDate) {
       return  workSiteService.scheduleInactiveDay(authentication, workSiteId, inactiveDate);
    }

    public void removeInactiveDay(Authentication authentication,
                                  Integer workSiteId,
                                  ScheduleInactiveDayRequest inactiveDate) {
        workSiteService.removeInactiveDay(authentication, workSiteId, inactiveDate);
    }

    public boolean isWorkSiteActive(Integer workSiteId) {
        return workSiteService.isWorkSiteActive(workSiteId);
    }

    public IsWithinRadiusResponse isWithinRadius(Integer workSiteId, IsWithinRadiusRequest request) {
        return workSiteService.isWithinRadius(workSiteId, request);
    }

    public boolean isWithinRadiusForPunchInOut(Integer workSiteId, Double userLatitude, Double userLongitude){
        return workSiteService.isWithinRadiusForPunchInOut(workSiteId, userLatitude, userLongitude);
    }

    public boolean canPunchInOut(Authentication authentication,
                                 Integer workSiteId,
                                 Integer userId,
                                CanPunchOutRequestWorkSite canPunchOutRequestWorkSite) {
        return workSiteService.canPunchInOut(authentication, workSiteId, userId, canPunchOutRequestWorkSite);
    }

    public boolean canPunchInOutForWorkAttendance(Authentication authentication, Integer workSiteId, Integer userId, LocalTime currentTime) {
        return workSiteService.canPunchInOutForWorkAttendance(authentication, workSiteId, userId, currentTime);
    }

    public PageResponse<WorkSiteClosedDaysResponse> findWorkSiteClosedDays(Integer workSiteId,
                                                                           int page,
                                                                           int size,
                                                                           Authentication authentication) {

        return workSiteService.findWorkSiteClosedDays(workSiteId, page, size, authentication);
    }

    public PageResponse<WorkerCurrentlyWorkingInWorkSite> findAllWorkerInWorkSiteRelated(Integer workSiteId,
                                                                                         int page,
                                                                                         int size,
                                                                                         Authentication authentication) {
    return workSiteService.findAllWorkerInWorkSiteRelated(workSiteId, page, size, authentication);
    }

    }
