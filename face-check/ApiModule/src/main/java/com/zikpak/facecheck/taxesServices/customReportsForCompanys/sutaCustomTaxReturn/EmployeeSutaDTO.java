package com.zikpak.facecheck.taxesServices.customReportsForCompanys.sutaCustomTaxReturn;
import lombok.*;
import java.math.BigDecimal;



@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeSutaDTO {
    private String employeeName;
    private BigDecimal grossWages;
    private BigDecimal sutaWageBase;
    private BigDecimal sutaTax;
    private Boolean exceededLimit; // Exceeded $13,000 limit
}