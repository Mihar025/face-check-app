package com.zikpak.facecheck.taxesServices.customReportsForCompanys.futaCustomTaxReport;

import com.zikpak.facecheck.entity.User;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class EmployeeFutaCalculation {
    private User employee;
    private BigDecimal grossWages;
    private BigDecimal futaWageBase;
    private BigDecimal futaTax;
    private BigDecimal yearToDateWages;
    private Boolean exceededLimit;
}
