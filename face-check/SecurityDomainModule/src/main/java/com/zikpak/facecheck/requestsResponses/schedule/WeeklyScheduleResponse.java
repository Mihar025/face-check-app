package com.zikpak.facecheck.requestsResponses.schedule;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WeeklyScheduleResponse {

    private List<DailyScheduleResponse> dailySchedules;
    private Double totalWeekHours;
    private Double regularHours;
    private Double overtimeHours;
}
