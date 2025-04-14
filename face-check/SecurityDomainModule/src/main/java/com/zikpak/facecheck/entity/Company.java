package com.zikpak.facecheck.entity;

import com.zikpak.facecheck.entity.employee.WorkSite;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
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






    private Integer workersQuantity;

    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL)
    private List<User> employees = new ArrayList<>();

    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL)
    private Set<WorkSite> workSites = new HashSet<>();


    public void addWorkSite(WorkSite workSite) {
        workSites.add(workSite);
        workSite.setCompany(this);
    }


    public void removeWorkSite(WorkSite workSite) {
        workSites.remove(workSite);
        workSite.setCompany(null);
    }

}

