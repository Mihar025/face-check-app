package com.zikpak.facecheck.taxesServices.customReportsForCompanys.hoursReport;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class EmployeeHoursDTO {

    private String employeeName;
    private BigDecimal regularHours;
    private BigDecimal overtimeHours;
    private BigDecimal totalHours;
    private BigDecimal dailyAverage;
    private BigDecimal overtimeRate; // percentage
    private BigDecimal hourlyRate;
    private BigDecimal totalEarnings;

}
