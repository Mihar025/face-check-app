package com.zikpak.facecheck.requestsResponses;


import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AddOvertimeRequest {

    @NotNull(message = "Attendance ID is required")
    private Integer attendanceId;

    @NotNull(message = "Overtime hours is required")
    @Min(value = 0, message = "Overtime hours must be positive")
    @Max(value = 8, message = "Overtime hours cannot exceed 8 hours")
    private Double overtimeHours;

    @NotNull(message = "Reason is required")
    private String reason;
}