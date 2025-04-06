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

    // Налоги компании
    private BigDecimal socialSecurityTax;     // 6.2% от зарплаты до $160,200 (2023)
    private BigDecimal medicareTax;           // 1.45% от всей зарплаты
    private BigDecimal federalUnemploymentTax; // 6% на первые $7,000
    private BigDecimal nyUnemploymentTax;     // ~4.1% на первые $12,000
    private BigDecimal nyDisabilityInsurance; // ~$0.14 за $100 зарплаты
    private BigDecimal workersCompensation;   // Зависит от типа работы

    // Дополнительная информация
    private Integer employeeCount;
    private BigDecimal totalPayroll;
    private LocalDate calculationDate;

}
