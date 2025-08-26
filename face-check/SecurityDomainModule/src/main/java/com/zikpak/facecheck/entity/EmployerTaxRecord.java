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

    @Column(name = "gross_pay")
    private BigDecimal grossPay  = BigDecimal.ZERO;
    @Column(name = "social_security_tax")
    private BigDecimal socialSecurityTax  = BigDecimal.ZERO;

    @Column(name = "medicare_tax")
    private BigDecimal medicareTax  = BigDecimal.ZERO;

    @Column(name = "futa_tax")
    private BigDecimal futaTax  = BigDecimal.ZERO;

    @Column(name = "suta_tax")
    private BigDecimal sutaTax  = BigDecimal.ZERO;

    @Column(name = "federal_withholding")
    private BigDecimal federalWithholding  = BigDecimal.ZERO;

    @Column(name = "total_employer_tax")
    private BigDecimal totalEmployerTax  = BigDecimal.ZERO;

    @Column(name = "period_start")
    private LocalDate periodStart;

    @Column(name = "period_end")
    private LocalDate periodEnd;

    @CreatedDate
    @Column(updatable = false, name = "created_at")
    private LocalDate createdAt;

    @Column(name = "social_security_taxable_wages")
    private BigDecimal socialSecurityTaxableWages  = BigDecimal.ZERO;

    @Column(name = "social_security_tips")
    private BigDecimal socialSecurityTips  = BigDecimal.ZERO;

    @Column(name = "medicare_taxable_wages")
    private BigDecimal medicareTaxableWages  = BigDecimal.ZERO;

    @Column(name = "additional_medicare_wages")
    private BigDecimal additionalMedicareWages  = BigDecimal.ZERO;

    @Column(name = "payment_date")
    private LocalDate paymentDate;

    @Column(name = "futa_taxable_wages")
    private BigDecimal futaTaxableWages  = BigDecimal.ZERO;

    @Column(name = "suta_taxable_wages")
    private BigDecimal sutaTaxableWages = BigDecimal.ZERO;
}
