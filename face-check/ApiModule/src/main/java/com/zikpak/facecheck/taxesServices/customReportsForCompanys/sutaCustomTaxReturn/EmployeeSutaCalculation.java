package com.zikpak.facecheck.taxesServices.customReportsForCompanys.sutaCustomTaxReturn;

import com.zikpak.facecheck.entity.User;
import lombok.*;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeSutaCalculation {
    private User employee;
    private BigDecimal grossWages;
    private BigDecimal sutaWageBase;
    private BigDecimal sutaTax;
    private BigDecimal yearToDateWages;
    private Boolean exceededLimit;
}