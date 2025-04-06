package com.zikpak.facecheck.requestsResponses.workSite.data;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class IsWithinRadiusResponse {

    private Integer worksiteId;

    private double providedLatitude;

    private double providedLongitude;

    private double actualLatitude;
    private double actualLongitude;
    private double allowedRadius;

    private boolean isWithinRadius;


}
