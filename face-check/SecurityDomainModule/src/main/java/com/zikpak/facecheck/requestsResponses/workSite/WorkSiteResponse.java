package com.zikpak.facecheck.requestsResponses.workSite;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WorkSiteResponse {
    private Integer workSiteId;

    private String workSiteName;

    private String address;

    private Double latitude;

    private Double longitude;

    private Double allowedRadius;

    private LocalTime workDayStart;

    private LocalTime workDayEnd;
}
