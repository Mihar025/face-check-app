package com.zikpak.facecheck.taxesServices.customReportsForCompanys.taxSummary;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public  class TaxSummaryTotals {
    private BigDecimal totalGrossWages;
    private BigDecimal totalTaxableWages;
    private BigDecimal totalFederalTax;
    private BigDecimal totalSocialSecurityTax;
    private BigDecimal totalMedicareTax;
    private BigDecimal totalStateTax;
    private BigDecimal totalLocalTax;
    private BigDecimal totalFUTATax;
    private BigDecimal totalSUTATax;
    private BigDecimal totalEmployerTaxes;
    private BigDecimal totalEmployeeTaxes;
    private BigDecimal totalTaxLiability;
    private Integer totalEmployees;
    private Integer activeEmployees;
}
