package com.zikpak.facecheck.requestsResponses.worker;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class WorkerPayrollResponse {
    private Integer workerId;
    private String workerFullName;
    private LocalDate periodStart;
    private LocalDate periodEnd;

    private Double regularHours;
    private Double overtimeHours;
    private Double totalHours;

    private BigDecimal regularPay;
    private BigDecimal overtimePay;
    private BigDecimal grossPay;

    private BigDecimal medicare;
    private BigDecimal socialSecurity;
    private BigDecimal federalWithholding;
    private BigDecimal stateWithholding;
    private BigDecimal localWithholding;
    private BigDecimal disability;
    private BigDecimal paidFamilyLeave;
    private BigDecimal totalDeductions;

    private BigDecimal netPay;
}