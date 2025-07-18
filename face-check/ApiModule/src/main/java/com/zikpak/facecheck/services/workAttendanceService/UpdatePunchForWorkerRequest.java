package com.zikpak.facecheck.services.workAttendanceService;

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
public class UpdatePunchForWorkerRequest {

    private LocalDate newPunchDate;
    private LocalTime newPunchTime;
    private PunchType punchType;

    private Double workedHours;

    private Integer workSiteId;
    private String notes;
    private Boolean skipOvertimeCalculation;

}