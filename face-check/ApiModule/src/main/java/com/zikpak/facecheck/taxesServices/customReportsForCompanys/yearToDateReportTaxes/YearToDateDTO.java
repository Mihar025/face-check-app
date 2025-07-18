package com.zikpak.facecheck.taxesServices.customReportsForCompanys.yearToDateReportTaxes;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class YearToDateDTO {
    private Integer companyId;
    private String companyName;
    private LocalDate periodStart;
    private LocalDate periodEnd;

    private Double totalRegularHours;
    private Double totalOvertimeHours;

    private BigDecimal totalGross;
    private BigDecimal totalRegularPay;
    private BigDecimal totalOvertimePay;

    private BigDecimal totalFederalWithholding;
    private BigDecimal totalSocialSecurity;
    private BigDecimal totalMedicare;
    private BigDecimal totalStateWithHolding;
    private BigDecimal totalLocalWithholding;
    private BigDecimal totalFutaWithholding;
    private BigDecimal totalSutaWithholding;

    private BigDecimal totalDisabilityWithholding;
    private BigDecimal totalPaidFamilyLeave;

    private BigDecimal totalRetirement401kContribution;
    private BigDecimal totalHealthInsuranceCost;

    private BigDecimal totalDeductions;
    private BigDecimal totalNet;
}
