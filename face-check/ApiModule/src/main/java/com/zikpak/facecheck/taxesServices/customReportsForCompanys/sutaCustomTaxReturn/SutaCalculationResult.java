package com.zikpak.facecheck.taxesServices.customReportsForCompanys.sutaCustomTaxReturn;
import lombok.*;
import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SutaCalculationResult {
    private BigDecimal totalGrossWages;
    private BigDecimal totalSutaWageBase;
    private BigDecimal totalSutaTax;
    private Integer totalEmployees;
    private Integer employeesSubjectToSuta;
    private List<EmployeeSutaCalculation> employeeCalculations;
}
