package com.zikpak.facecheck.taxesServices.customReportsForCompanys.sutaCustomTaxReturn;

import lombok.AllArgsConstructor;


import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SutaReportDTO {
    // Company info
    private Integer companyId;
    private String companyName;
    private String companyAddress;
    private String companyCity;
    private String companyState;
    private String companyZipCode;
    private String companyPhone;
    private String employerEIN;
    private String sutaAccountNumber; // NY State account number

    // Period info
    private LocalDate periodStart;
    private LocalDate periodEnd;
    private String reportType; // "Quarterly" or "Annual"
    private Integer taxYear;
    private Integer quarter;

    // SUTA calculations
    private BigDecimal totalGrossWages;
    private BigDecimal totalSutaWageBase;
    private BigDecimal totalSutaTaxOwed;
    private BigDecimal sutaRate; // Company-specific rate (from DB)
    private BigDecimal standardSutaRate; // NY standard rate 0.6%
    private BigDecimal experienceRate; // Experience modification

    // Payment info
    private BigDecimal totalSutaTaxPaid;
    private BigDecimal remainingSutaLiability;
    private Boolean needsPayment;

    // Employee data
    private Integer totalEmployees;
    private Integer employeesSubjectToSuta;
    private List<EmployeeSutaDTO> employeeDetails;

    // Quarterly breakdown (for annual reports)
    private List<QuarterlySutaDTO> quarterlyBreakdown;

    // Compliance
    private Boolean complianceStatus;
    private LocalDate nextPaymentDue;
    private List<String> notes;
}