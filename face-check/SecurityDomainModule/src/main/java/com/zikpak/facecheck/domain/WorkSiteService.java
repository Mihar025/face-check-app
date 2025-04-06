package com.zikpak.facecheck.domain;

import com.zikpak.facecheck.requestsResponses.PageResponse;
import com.zikpak.facecheck.requestsResponses.workSite.WorkSiteClosedDaysResponse;
import com.zikpak.facecheck.requestsResponses.workSite.WorkSiteRequest;
import com.zikpak.facecheck.requestsResponses.workSite.WorkSiteResponse;
import com.zikpak.facecheck.requestsResponses.workSite.WorkerCurrentlyWorkingInWorkSite;
import com.zikpak.facecheck.requestsResponses.workSite.data.*;
import com.zikpak.facecheck.requestsResponses.workSite.selectWorkSite.SelectWorkSiteResponse;
import com.zikpak.facecheck.requestsResponses.workSite.updates.*;
import org.springframework.security.core.Authentication;

public interface WorkSiteService {
    // For default users
    WorkSiteResponse findWorkSiteById(Integer id);  // find special work site
    PageResponse<WorkSiteResponse> findAllWorkSites(int page, int size);      // find all work sites
    SelectWorkSiteResponse selectWorkSite(Integer workSiteId, Authentication authentication);  // select special work site

    SetNewCustomRadiusForWorkerInSpecialWorkSiteResponse setNewCustomRadiusForWorker(Integer workSiteId,
                                                           Integer workerId,
                                                           SetNewCustomRadiusRequest setNewCustomRadiusRequest,
                                                           Authentication authentication);


    // For admins and business workers
    // Create operations
    WorkSiteResponse createWorkSite(Authentication authentication, WorkSiteRequest request);



    SetNewCustomRadiusResponse setCustomRadiusForWorkSite(Authentication authentication,
                                  Integer workSiteId,
                                  SetNewCustomRadiusRequest customRadius);  // для работников которым нужен особый радиус




    // Update operations
    WorkSiteUpdateNameResponse updateWorkSiteName(Authentication authentication, Integer workSiteId, UpdateNameRequest updateNameRequest);
    WorkSiteUpdateAddressResponse updateWorkSiteAddress(Authentication authentication,Integer workSiteId, UpdateWorkSiteAddress newAddress);
    WorkSiteUpdateWorkingHoursResponse updateWorkingHours(Authentication authentication,Integer workSiteId,
                                                          WorkSiteUpdateWorkingHoursRequest request);
    WorkSiteUpdateLocationResponse updateWorkSiteLocation(Authentication authentication,Integer workSiteId,
                                                          WorkSiteUpdateLocationRequest request);

    // Activity management
    void setWorkSiteActiveOrNotActive(Authentication authentication, Integer workSiteId, UpdateStatusWorkSiteRequest isActive);
    ScheduleInactiveDayResponse scheduleInactiveDay(Authentication authentication, Integer workSiteId, ScheduleInactiveDayRequest inactiveDate);  // запланировать неактивный день
    ScheduleInactiveDayResponse removeInactiveDay(Authentication authentication, Integer workSiteId, ScheduleInactiveDayRequest date);  // удалить неактивный день

    // Validation methods
    boolean isWorkSiteActive(Integer workSiteId);  // проверка активна ли сейчас площадка


    IsWithinRadiusResponse isWithinRadius(Integer workSiteId,     // проверка находится ли работник в радиусе
                           IsWithinRadiusRequest request);

    boolean canPunchInOut( Authentication authentication,
                            Integer workSiteId,      // проверка можно ли сейчас делать punch in/out
                          Integer userId,           // (проверяет время и активность)
                          CanPunchOutRequestWorkSite canPunchOutRequest);


    PageResponse<WorkSiteClosedDaysResponse> findWorkSiteClosedDays(Integer workSite, int page, int size, Authentication authentication);

    PageResponse<WorkerCurrentlyWorkingInWorkSite> findAllWorkerInWorkSiteRelated(Integer workSiteId, int page, int size, Authentication authentication);

    //findAll Worksites to which admin related!;
    //

}
