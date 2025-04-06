package com.zikpak.facecheck.domain;

import com.zikpak.facecheck.requestsResponses.PageResponse;
import com.zikpak.facecheck.requestsResponses.admin.WorksiteWorkerResponse;
import org.springframework.security.core.Authentication;

public interface AdminAndForemanFunctionality {


    PageResponse<WorksiteWorkerResponse> findAllWorkersInWorkSite(int page, int size, Integer workSiteId, Authentication authentication);


}
