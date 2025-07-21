package com.zikpak.facecheck.requestsResponses.workSite.data;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class IsWithinRadiusResponse {

    private Integer worksiteId;

    private double providedLatitude;

    private double providedLongitude;

    private double actualLatitude;
    private double actualLongitude;
    private double allowedRadius;

    private boolean isWithinRadius;


}
