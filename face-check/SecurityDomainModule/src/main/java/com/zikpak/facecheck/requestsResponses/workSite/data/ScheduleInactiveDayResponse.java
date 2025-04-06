package com.zikpak.facecheck.requestsResponses.workSite.data;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class ScheduleInactiveDayResponse {


    private Integer workSiteId;

    private LocalDate inactiveDate;




}
