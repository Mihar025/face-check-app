package com.zikpak.facecheck.requestsResponses.workSite;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WorkSiteClosedDaysResponse {

    private Integer workSiteId;
    private String siteName;
    private String siteAddress;
    private List<LocalDate> closedDays;

}
