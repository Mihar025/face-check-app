package com.zikpak.facecheck.entity;

import com.zikpak.facecheck.entity.employee.WorkerPayroll;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "employer_tax_record")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployerTaxRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    private Company company;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id")
    private User employee;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pay_stub_id")
    private WorkerPayroll payStub;

    private BigDecimal grossPay;

    private BigDecimal socialSecurityTax;
    private BigDecimal medicareTax;
    private BigDecimal futaTax;
    private BigDecimal sutaTax;
    private BigDecimal federalWithholding;


    private BigDecimal totalEmployerTax;

    private LocalDate weekStart;
    private LocalDate weekEnd;

    private LocalDate createdAt;


    private BigDecimal socialSecurityTaxableWages;

    private BigDecimal socialSecurityTips;

    private BigDecimal medicareTaxableWages;

    private BigDecimal additionalMedicareWages;

    private LocalDate paymentDate;
}
