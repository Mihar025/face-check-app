package com.zikpak.facecheck.entity.employee;


import com.zikpak.facecheck.entity.Company;
import com.zikpak.facecheck.entity.User;
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
    private User worker;

    @ManyToOne
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


    private BigDecimal netPay;
}

