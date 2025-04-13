package com.zikpak.facecheck.requestsResponses;

import lombok.Builder;
import lombok.Data;

import java.time.LocalTime;

@Data
@Builder
public class WorkSiteAllInformationResponse {
    private Integer workSiteId;

    private String siteName;
    private String address;
    private Double latitude;
    private Double longitude;
    private Double allowedRadius;

    private LocalTime workDayStart;
    private LocalTime workDayEnd;
    private Boolean isActive;


}
