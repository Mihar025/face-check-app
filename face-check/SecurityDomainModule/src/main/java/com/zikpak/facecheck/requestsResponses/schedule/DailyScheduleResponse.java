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
public class DailyScheduleResponse {

    private String dayOfWeek;
    private LocalDate date;
    private Double hoursWorked;
    private LocalTime startTime;
    private LocalTime endTime;
    private String workSiteName;
    private Boolean isOnDuty;


}
