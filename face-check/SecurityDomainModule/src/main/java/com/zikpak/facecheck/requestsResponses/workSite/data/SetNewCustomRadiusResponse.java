package com.zikpak.facecheck.requestsResponses.workSite.data;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SetNewCustomRadiusResponse {

    private Integer workSiteId;

    private Double customRadius;

}
