package com.zikpak.facecheck.taxesServices.customReportsForCompanys.futaCustomTaxReport;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class FutaReportDTO {

    // Company Information
    private Integer companyId;
    private String companyName;
    private String companyAddress;
    private String companyCity;
    private String companyState;
    private String companyZipCode;
    private String companyPhone;
    private String employerEIN;

    // Report Period
    private LocalDate periodStart;
    private LocalDate periodEnd;
    private String reportType; // "Quarterly" or "Annual"
    private Integer taxYear;
    private Integer quarter; // null for annual

    // FUTA Summary
    private BigDecimal totalGrossWages;
    private BigDecimal totalFutaWageBase; // до $7,000 на каждого
    private BigDecimal totalFutaTaxOwed;
    private BigDecimal futaRate; // 0.6%
    private BigDecimal nyCreditReduction; // +0.3% для NY
    private BigDecimal effectiveFutaRate; // 0.9% для NY

    // Payment Info
    private BigDecimal totalFutaTaxPaid;
    private BigDecimal remainingFutaLiability;
    private Boolean needsPayment;

    // Employee Info
    private Integer totalEmployees;
    private Integer employeesSubjectToFuta;
    private List<EmployeeFutaDTO> employeeDetails;

    // Quarterly Breakdown (for annual reports)
    private List<QuarterlyFutaDTO> quarterlyBreakdown;

    // Compliance
    private Boolean complianceStatus;
    private LocalDate nextPaymentDue;
    private List<String> notes;
}
