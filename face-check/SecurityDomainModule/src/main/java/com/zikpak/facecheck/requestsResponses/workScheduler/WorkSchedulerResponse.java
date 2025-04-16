package com.zikpak.facecheck.requestsResponses.workScheduler;


import lombok.Builder;
import lombok.Data;

import java.time.LocalTime;

@Data
@Builder
public class WorkSchedulerResponse {

    private Integer workerId;

    private LocalTime startTime;

    private LocalTime endTime;


}
