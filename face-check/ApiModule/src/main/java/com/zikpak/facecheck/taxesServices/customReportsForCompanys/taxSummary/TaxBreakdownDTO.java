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
public class TaxBreakdownDTO {
    private String taxType;
    private BigDecimal employerPortion;
    private BigDecimal employeePortion;
    private BigDecimal totalAmount;
    private String description;
}