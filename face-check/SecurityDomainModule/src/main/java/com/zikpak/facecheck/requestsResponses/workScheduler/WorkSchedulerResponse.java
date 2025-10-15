package com.zikpak.facecheck.requestsResponses.workScheduler;


import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Data
@Builder
public class WorkSchedulerResponse {

    private Integer workerId;
    private String workerName;
    private List<ScheduleDto> schedules;
    private String message;

}
