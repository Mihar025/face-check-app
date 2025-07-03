package com.zikpak.facecheck.entity;

import com.zikpak.facecheck.entity.employee.WorkSite;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "_company")
@Entity
public class Company {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String companyName;

    @Column(nullable = false)
    private String companyAddress;

    @Column(nullable = false)
    private String companyPhone;
    @Column(nullable = false)
    private String companyEmail;


  //  @Column(nullable = false)
    private String companyCity;

  //  @Column(nullable = false)
    private String companyState;

   // @Column(nullable = false)
    private String companyZipCode;

  //  @Column(nullable = false, unique = true)
    private String employerEIN;



    private BigDecimal companyIncomePerMonth;

    private BigDecimal costsForEmployeeSalariesPerMonth;


    private BigDecimal regularPayPerMonthTotal;
    private BigDecimal overtimePayPerMonthTotal;
    private BigDecimal grossPayPerMonthTotal;
    private BigDecimal totalSpendMoneyPerYear;


    private BigDecimal companyInsurance;
    private BigDecimal socialSecurityTaxForCompany;
    private BigDecimal federalWithholdingForCompany;
    private BigDecimal nyStateWithholdingForCompany;
    private BigDecimal nyLocalWithholdingForCompany;

    @Enumerated(EnumType.STRING)
    private CompanyPaymentPosition companyPaymentPosition;

    private BigDecimal irsDepositAmount;

    private LocalDateTime whenDepositAmountWasMade;

    private Integer workersQuantity;

    private LocalDate firstBiweeklyDate;


    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL)
    private List<User> employees = new ArrayList<>();



    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL)
    private Set<WorkSite> workSites = new HashSet<>();

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "owner_id")
    private User companyOwner;

    /** EMR (Experience Modifier Rate), напр. 1.25 */
    @Column(precision = 4, scale = 2, nullable = true)
    private BigDecimal emr;

    @Column(name = "wc_policy_number", length = 50, nullable = true)
    private String wcPolicyNumber;

    @Column(name = "wc_insurance_carrier", length = 100, nullable = true)
    private String wcInsuranceCarrier;

    public void addWorkSite(WorkSite workSite) {
        workSites.add(workSite);
        workSite.setCompany(this);
    }


    public void removeWorkSite(WorkSite workSite) {
        workSites.remove(workSite);
        workSite.setCompany(null);
    }

}

