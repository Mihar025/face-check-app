package com.zikpak.facecheck.taxesServices.customReportsForCompanys.futaCustomTaxReport;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class QuarterlyFutaDTO {
    private Integer quarter;
    private LocalDate quarterStart;
    private LocalDate quarterEnd;
    private BigDecimal grossWages;
    private BigDecimal futaWageBase;
    private BigDecimal futaTaxOwed;
    private BigDecimal futaTaxPaid;
    private Boolean isLiable; // > $500
}