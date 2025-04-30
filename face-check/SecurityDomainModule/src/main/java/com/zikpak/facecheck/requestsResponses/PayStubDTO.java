package com.zikpak.facecheck.requestsResponses;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Map;

@Data
@Builder
public class PayStubDTO {

    private String employeeName;
    private String employeeSsn;
    private String employeeAddress;

    private String employerName;
    private String employerAddress;

    private LocalDate periodStart;
    private LocalDate periodEnd;

    private Map<DayOfWeek, BigDecimal> hoursWorkedPerDay;
    private Map<DayOfWeek, BigDecimal> grossPayPerDay;

    private BigDecimal totalHours;
    private BigDecimal totalGrossPay;

    private BigDecimal federalTax;
    private BigDecimal socialSecurityTax;
    private BigDecimal medicareTax;
    private BigDecimal stateTax;
    private BigDecimal localTax;

    private BigDecimal netPay;
}
