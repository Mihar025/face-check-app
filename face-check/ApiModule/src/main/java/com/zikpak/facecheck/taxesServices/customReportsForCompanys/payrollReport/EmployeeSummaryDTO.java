package com.zikpak.facecheck.taxesServices.customReportsForCompanys.payrollReport;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
@Data
@Builder
public class EmployeeSummaryDTO {

    private String employeeName;
    private BigDecimal grossPay;
    private BigDecimal netPay;
    private BigDecimal hoursWorked;
    private BigDecimal hourlyRate;
    private BigDecimal overtimeHours;
    private BigDecimal regularHours;
}
