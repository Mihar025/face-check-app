package com.zikpak.facecheck.entity;

import com.zikpak.facecheck.entity.employee.WorkerPayroll;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

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


    private BigDecimal grossPay  = BigDecimal.ZERO;

    private BigDecimal socialSecurityTax  = BigDecimal.ZERO;
    private BigDecimal medicareTax  = BigDecimal.ZERO;
    private BigDecimal futaTax  = BigDecimal.ZERO;
    private BigDecimal sutaTax  = BigDecimal.ZERO;
    private BigDecimal federalWithholding  = BigDecimal.ZERO;


    private BigDecimal totalEmployerTax  = BigDecimal.ZERO;

    private LocalDate periodStart;
    private LocalDate periodEnd;

    @CreatedDate
    @Column(updatable = false)
    private LocalDate createdAt;


    private BigDecimal socialSecurityTaxableWages  = BigDecimal.ZERO;

    private BigDecimal socialSecurityTips  = BigDecimal.ZERO;

    private BigDecimal medicareTaxableWages  = BigDecimal.ZERO;

    private BigDecimal additionalMedicareWages  = BigDecimal.ZERO;

    private LocalDate paymentDate;

    private BigDecimal futaTaxableWages  = BigDecimal.ZERO;

    private BigDecimal sutaTaxableWages = BigDecimal.ZERO;
}
