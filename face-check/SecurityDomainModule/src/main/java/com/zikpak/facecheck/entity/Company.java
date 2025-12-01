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

    @Column(nullable = false, name = "company_name")
    private String companyName = "";

    @Column(nullable = false, name = "company_address")
    private String companyAddress = "";

    @Column(nullable = false, name = "company_phone")
    private String companyPhone = "";

    @Column(nullable = false, name = "company_email")
    private String companyEmail = "";

    @Column(name = "company_state_id_number")
    private String companyStateIdNumber = "";


  //  @Column(nullable = false)
    @Column(name = "company_city")
    private String companyCity = "";

  //  @Column(nullable = false)
    @Column(name = "company_state")
    private String companyState = "" ;

   // @Column(nullable = false)
    @Column(name = "company_zip_code")
    private String companyZipCode = "";

    @Column(name = "employer_ein", nullable = false, unique = true)
    private String employerEIN = "";

    @Column(name = "special_two_char_condition_code_for_mta305", nullable = false)
    private String specialTwoCharConditionCodeForMTA305 = "";


    @Column(name = "stripe_customer_id")
    private String stripeCustomerId;

    @Column(name = "stripe_subscription_id")
    private String stripeSubscriptionId;

    @Column(name = "stripe_subscription_item_id")
    private String stripeSubscriptionItemId;

    @Column(name = "subscription_status")
    private String subscriptionStatus;

    @Column(name = "subscription_current_period_end")
    private LocalDateTime subscriptionCurrentPeriodEnd;



    @Column(name = "company_income_per_month")
    private BigDecimal companyIncomePerMonth = BigDecimal.ZERO;

    @Column(name = "costs_for_employee_salaries_per_month")
    private BigDecimal costsForEmployeeSalariesPerMonth = BigDecimal.ZERO;

    @Column(name = "regular_pay_per_month_total")
    private BigDecimal regularPayPerMonthTotal = BigDecimal.ZERO;

    @Column(name = "overtime_pay_per_month_total")
    private BigDecimal overtimePayPerMonthTotal= BigDecimal.ZERO;

    @Column(name = "gross_pay_per_month_total")
    private BigDecimal grossPayPerMonthTotal= BigDecimal.ZERO;

    @Column(name = "total_spend_money_per_year")
    private BigDecimal totalSpendMoneyPerYear= BigDecimal.ZERO;

    @Column(name = "company_insurance")
    private BigDecimal companyInsurance= BigDecimal.ZERO;

    @Column(name = "social_security_tax_for_company")
    private BigDecimal socialSecurityTaxForCompany= BigDecimal.ZERO;

    @Column(name = "federal_withholding_for_company")
    private BigDecimal federalWithholdingForCompany= BigDecimal.ZERO;

    @Column(name = "ny_state_withholding_for_company")
    private BigDecimal nyStateWithholdingForCompany= BigDecimal.ZERO;

    @Column(name = "ny_local_withholding_for_company")
    private BigDecimal nyLocalWithholdingForCompany= BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(name = "company_payment_position")
    private CompanyPaymentPosition companyPaymentPosition;

    @Column(name = "irs_deposited_amount")
    private BigDecimal irsDepositAmount = BigDecimal.ZERO;

    @Column(name = "when_deposit_amount_was_made")
    private LocalDateTime whenDepositAmountWasMade;

    @Column(name = "workers_quantity")
    private Integer workersQuantity = 0;

    @Column(name = "first_biweekly_date")
    private LocalDate firstBiweeklyDate;

    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL)
    private List<User> employees = new ArrayList<>();

    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL)
    private Set<WorkSite> workSites = new HashSet<>();

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "owner_id")
    private User companyOwner;

    /** EMR (Experience Modifier Rate), напр. 1.25 */
    @Column(precision = 4, scale = 2, nullable = true, name = "emr")
    private BigDecimal emr = BigDecimal.ZERO;;

    @Column(name = "wc_policy_number", length = 50, nullable = true)
    private String wcPolicyNumber;

    @Column(name = "wc_insurance_carrier", length = 100, nullable = true)
    private String wcInsuranceCarrier = "";

    @Column(nullable = true, name = "funding_bank_name")
    private String fundingBankName = "";

    @Column(nullable = true, name = "funding_routing_number")
    private String fundingRoutingNumber = "";

    @Column(nullable = true, name = "funding_account_number")
    private String fundingAccountNumber = "";

    @Column(nullable = true, name = "return_mailing_address")
    private String returnMailingAddress = "";

    @Column(nullable = true, name = "default_memo")
    private String defaultMemo = "";

    @Column(nullable = true, name = "signature_name")
    private String signatureName = "";

    @Column(nullable = true, name = "signature_title")
    private String signatureTitle = "";

    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Notification> notifications = new ArrayList<>();


    public void addWorkSite(WorkSite workSite) {
        workSites.add(workSite);
        workSite.setCompany(this);
    }


    public void removeWorkSite(WorkSite workSite) {
        workSites.remove(workSite);
        workSite.setCompany(null);
    }

}

