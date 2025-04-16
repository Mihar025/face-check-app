package com.zikpak.facecheck.requestsResponses.company.finance;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class CompanyTaxCalculationResponse {

    private Integer companyId;
    private String companyName;
    private BigDecimal monthlyIncome;
    private BigDecimal totalTaxes;

    private BigDecimal socialSecurityTax;
    private BigDecimal medicareTax;
    private BigDecimal federalUnemploymentTax;
    private BigDecimal nyUnemploymentTax;
    private BigDecimal nyDisabilityInsurance;
    private BigDecimal workersCompensation;

    private Integer employeeCount;
    private BigDecimal totalPayroll;
    private LocalDate calculationDate;

}
