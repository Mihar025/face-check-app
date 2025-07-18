package com.zikpak.facecheck.taxesServices.customReportsForCompanys.sutaCustomTaxReturn;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuarterlySutaDTO {
    private Integer quarter;
    private LocalDate quarterStart;
    private LocalDate quarterEnd;
    private BigDecimal grossWages;
    private BigDecimal sutaWageBase;
    private BigDecimal sutaTaxOwed;
    private BigDecimal sutaTaxPaid;
    private Boolean isLiable;
}