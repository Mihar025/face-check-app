package com.zikpak.facecheck.taxesServices.customReportsForCompanys.payrollReport;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class PayrollTotals {
    private BigDecimal totalGross;
    private BigDecimal totalNet;
    private BigDecimal totalTaxes;
    private BigDecimal totalHours;
    private Integer totalEmployees;
    private BigDecimal averageRate;
}