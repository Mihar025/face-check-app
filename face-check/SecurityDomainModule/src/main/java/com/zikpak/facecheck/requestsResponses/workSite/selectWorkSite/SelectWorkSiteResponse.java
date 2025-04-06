package com.zikpak.facecheck.requestsResponses.workSite.selectWorkSite;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SelectWorkSiteResponse {

    private String selectedWorkSiteName;
    private Integer selectedWorkSiteId;
    private Integer workerId;


}
