package com.zikpak.facecheck.requestsResponses.workSite.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SetNewCustomRadiusForWorkerInSpecialWorkSiteResponse {


    private Integer workSiteId;
    private Integer workerId;
    private String firstName;
    private String lastName;
    private String companyName;
    private Double newRadius;

}
