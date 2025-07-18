package com.zikpak.facecheck.taxesServices.customReportsForCompanys.taxSummary;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaxSummaryReportDTO {

    // Company Info
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
    private String reportType; // "Annual", "Quarterly", "Monthly"
    private Integer taxYear;
    private Integer quarter; // if quarterly

    // Summary Totals
    private BigDecimal totalGrossWages;
    private BigDecimal totalTaxableWages;
    private BigDecimal totalFederalTaxWithheld;
    private BigDecimal totalSocialSecurityTax;
    private BigDecimal totalMedicareTax;
    private BigDecimal totalStateTaxWithheld;
    private BigDecimal totalLocalTaxWithheld;
    private BigDecimal totalFUTATax;
    private BigDecimal totalSUTATax;
    private BigDecimal totalEmployerTaxes;
    private BigDecimal totalEmployeeTaxes;
    private BigDecimal totalTaxLiability;

    // Employee Count
    private Integer totalEmployees;
    private Integer activeEmployees;

    // Tax Breakdown by Type
    private List<TaxBreakdownDTO> taxBreakdown;

    // Employee Tax Summary
    private List<EmployeeTaxSummaryDTO> employeeTaxSummary;

    // Payment Status
    private BigDecimal totalTaxesPaid;
    private BigDecimal remainingTaxLiability;
    private Boolean complianceStatus;

    // Forms Required
    private List<String> formsRequired; // ["Form 941", "Form 940", "W-2", etc.]
}
