package com.zikpak.facecheck.taxesServices.customReportsForCompanys.payrollReport;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class PayrollSummaryReportDTO {

    private Integer companyId;
    private String companyName;
    private String companyAddress;
    private String companyCity;
    private String companyState;
    private String companyZipCode;
    private String companyPhone;

    // Report Period
    private LocalDate periodStart;
    private LocalDate periodEnd;
    private String reportType; // "Weekly", "Monthly", "Custom"

    // Summary Totals
    private BigDecimal totalGrossPay;
    private BigDecimal totalNetPay;
    private BigDecimal totalTaxesWithheld;
    private BigDecimal totalHoursWorked;
    private Integer totalEmployees;
    private BigDecimal averageHourlyRate;

    // Employee Breakdown
    private List<EmployeeSummaryDTO> employeeBreakdown;

}
