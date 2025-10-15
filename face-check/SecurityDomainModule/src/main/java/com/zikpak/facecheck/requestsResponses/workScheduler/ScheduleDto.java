package com.zikpak.facecheck.requestsResponses.workScheduler;

import lombok.Builder;
import lombok.Data;

import java.time.DayOfWeek;
import java.time.LocalTime;

@Data
@Builder
public class ScheduleDto {
    private Integer scheduleId;
    private DayOfWeek dayOfWeek;
    private LocalTime startTime;
    private LocalTime endTime;
    private LocalTime lunchStart;
    private LocalTime lunchEnd;
    private Boolean isCompanyPayingLunch;
    private Boolean isDayOff;
}