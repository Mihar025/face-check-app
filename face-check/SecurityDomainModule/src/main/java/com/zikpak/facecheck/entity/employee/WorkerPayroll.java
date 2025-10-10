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

    @Column(name = "period_start")
    private LocalDate periodStart;

    @Column(name = "period_end")
    private LocalDate periodEnd;

    @Column(name = "base_hourly_rate")
    private BigDecimal baseHourlyRate;

    @Column(name = "over_time_rate")
    private BigDecimal overtimeRate;

    @Column(name = "regular_hours")
    private Double regularHours;

    @Column(name = "overtime_hours")
    private Double overtimeHours;

    @Column(name = "total_hours")
    private Double totalHours;

    @Column(name = "regular_pay")
    private BigDecimal regularPay;

    @Column(name = "overtime_pay")
    private BigDecimal overtimePay;

    @Column(name = "gross_pay")
    private BigDecimal grossPay;

    @Column(name = "medicare")
    private BigDecimal medicare;

    @Column(name = "social_security_employee")
    private BigDecimal socialSecurityEmployee;

    @Column(name = "federal_withholding")
    private BigDecimal federalWithholding;

    @Column(name = "ny_state_withholding")
    private BigDecimal nyStateWithholding;

    @Column(name = "ny_local_withholding")
    private BigDecimal nyLocalWithholding;

    @Column(name = "ny_disability_withholding")
    private BigDecimal nyDisabilityWithholding;

    @Column(name = "ny_paid_family_leave")
    private BigDecimal nyPaidFamilyLeave;

    @Column(name = "total_deductions")
    private BigDecimal totalDeductions;

    @Column(name = "retirement401k_contribution")
    private BigDecimal retirement401kContribution;

    @Column(name = "health_insurance_cost")
    private BigDecimal healthInsuranceCost;

    @Column(name = "has_retirement_plan")
    private Boolean hasRetirementPlan;

    @Column(nullable = true, precision = 12, scale = 2, name = "ny_unemployment_withholding")
    private BigDecimal nyUnemploymentWithholding;

    @Column(name = "net_pay")
    private BigDecimal netPay;

    @Column(nullable = true, name = "employer_taxes_calculated")
    private boolean employerTaxesCalculated = false;

    @Column(name = "pay_stub_generated", nullable = true)
    private Boolean payStubGenerated = false;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "wc_risk_code", nullable = true)
    private WcRiskClass riskClass;

}

