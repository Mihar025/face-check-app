package com.zikpak.facecheck.requestsResponses;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;

@Data
@Builder
public class OvertimeResponse {
    private Integer attendanceId;
    private Integer workerId;
    private String workerName;
    private LocalDate date;
    private Double regularHours;
    private Double overtimeHours;
    private String reason;
    private Integer approvedBy;
    private String message;
    private Boolean isSuccessful;
}