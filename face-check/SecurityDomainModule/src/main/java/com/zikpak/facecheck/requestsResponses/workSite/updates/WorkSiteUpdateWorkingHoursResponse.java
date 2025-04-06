package com.zikpak.facecheck.requestsResponses.workSite.updates;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WorkSiteUpdateWorkingHoursResponse {

   private  Integer workSiteId;
    private LocalTime newStart;
    private LocalTime newEnd;

}
