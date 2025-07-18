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
public class UpdatePunchForWorkerResponse {

    private Integer workerId;
    private LocalDate newPunchDate;
    private LocalTime newPunchTime;
    private PunchType punchType;

    private Double hoursWorked;
    private Double overtimeHours;

    private LocalDate periodStart;
    private LocalDate periodEnd;

    private Boolean isSuccessful;
    private String message;

    private String workerName;
    private String workSiteName;
}