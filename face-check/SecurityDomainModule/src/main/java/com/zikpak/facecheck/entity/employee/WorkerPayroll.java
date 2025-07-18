package com.zikpak.facecheck.entity.employee;


import com.zikpak.facecheck.entity.Company;
import com.zikpak.facecheck.entity.User;
import com.zikpak.facecheck.entity.WcRiskClass;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "worker_payroll")
@Data
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkerPayroll {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "worker_id")
    private User worker;

    @ManyToOne
    @JoinColumn(name = "company_id")
    private Company company;

    private LocalDate periodStart;
    private LocalDate periodEnd;

    private BigDecimal baseHourlyRate;
    private BigDecimal overtimeRate;

    private Double regularHours;
    private Double overtimeHours;
    private Double totalHours;

    private BigDecimal regularPay;
    private BigDecimal overtimePay;
    private BigDecimal grossPay;

    private BigDecimal medicare;
    private BigDecimal socialSecurityEmployee;
    private BigDecimal federalWithholding;
    private BigDecimal nyStateWithholding;
    private BigDecimal nyLocalWithholding;
    private BigDecimal nyDisabilityWithholding;
    private BigDecimal nyPaidFamilyLeave;
    private BigDecimal totalDeductions;

    private BigDecimal retirement401kContribution;
    private BigDecimal healthInsuranceCost;
    private Boolean hasRetirementPlan;

    @Column(nullable = true, precision = 12, scale = 2)
    private BigDecimal nyUnemploymentWithholding;

    private BigDecimal netPay;

    @Column(nullable = true)
    private boolean employerTaxesCalculated = false;

    @Column(name = "pay_stub_generated", nullable = true)
    private Boolean payStubGenerated = false;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "wc_risk_code", nullable = true)
    private WcRiskClass riskClass;

}

