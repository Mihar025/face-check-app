package com.zikpak.facecheck.taxesServices.customReportsForCompanys.futaCustomTaxReport;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class EmployeeFutaDTO {
    private String employeeName;
    private BigDecimal grossWages;
    private BigDecimal futaWageBase;
    private BigDecimal futaTax;
    private Boolean exceededLimit; // превысил $7,000
    private LocalDate dateLimitReached;
}