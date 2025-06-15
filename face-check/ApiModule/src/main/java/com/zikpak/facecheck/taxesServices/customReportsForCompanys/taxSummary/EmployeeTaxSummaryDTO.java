package com.zikpak.facecheck.taxesServices.customReportsForCompanys.taxSummary;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeTaxSummaryDTO {
    private String employeeName;
    private BigDecimal grossWages;
    private BigDecimal federalWithholding;
    private BigDecimal socialSecurityWithholding;
    private BigDecimal medicareWithholding;
    private BigDecimal stateWithholding;
    private BigDecimal totalWithholdings;
    private BigDecimal netPay;
}