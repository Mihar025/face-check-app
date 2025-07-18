package com.zikpak.facecheck.taxesServices.customReportsForCompanys.hoursReport;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Data
@Builder
public class HoursReportDTO {

    // Company Info
    private String companyName;
    private String companyAddress;
    private String companyCity;
    private String companyState;
    private String companyZipCode;
    private String companyPhone;

    // Period Info
    private LocalDate periodStart;
    private LocalDate periodEnd;
    private String reportType;

    // Summary Totals
    private BigDecimal totalRegularHours;
    private BigDecimal totalOvertimeHours;
    private BigDecimal totalHours;
    private BigDecimal averageHoursPerEmployee;
    private BigDecimal overtimePercentage;
    private Integer totalEmployees;

    // Employee Hours Breakdown
    private List<EmployeeHoursDTO> employeeHours;

    // Daily Hours Breakdown
    private Map<LocalDate, DailyHoursDTO> dailyHours;

    // Top performers
    private List<EmployeeHoursDTO> topPerformers;



}
