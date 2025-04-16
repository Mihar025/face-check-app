package com.zikpak.facecheck.requestsResponses.schedule;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkerScheduleResponse {

    private Integer workerId;
    private String workSiteName;
    private LocalDate scheduleDate;

    private LocalTime expectedStartTime;
    private LocalTime expectedEndTime;
    private String shift;
    private Boolean isOnDuty;
}
