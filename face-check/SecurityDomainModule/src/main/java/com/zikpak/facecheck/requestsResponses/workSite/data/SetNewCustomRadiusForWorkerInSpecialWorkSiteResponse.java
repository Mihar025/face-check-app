package com.zikpak.facecheck.requestsResponses.workSite.data;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SetNewCustomRadiusForWorkerInSpecialWorkSiteResponse {


    private Integer workSiteId;
    private Integer workerId;
    private String firstName;
    private String lastName;
    private String companyName;
    private Double newRadius;

}
