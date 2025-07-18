package com.zikpak.facecheck.taxesServices.customReportsForCompanys.futaCustomTaxReport;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class FutaCalculationResult {
    private BigDecimal totalGrossWages;
    private BigDecimal totalFutaWageBase;
    private BigDecimal totalFutaTax;
    private Integer totalEmployees;
    private Integer employeesSubjectToFuta;
    private List<EmployeeFutaCalculation> employeeCalculations;
}