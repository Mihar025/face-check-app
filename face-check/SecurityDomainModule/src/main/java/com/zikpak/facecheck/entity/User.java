package com.zikpak.facecheck.entity;

import com.zikpak.facecheck.entity.W4.EmploymentType;
import com.zikpak.facecheck.entity.W4.FilingStatus;
import com.zikpak.facecheck.entity.W4.PayFrequency;
import com.zikpak.facecheck.entity.employee.WorkSite;
import com.zikpak.facecheck.entity.employee.WorkerAttendance;
import com.zikpak.facecheck.entity.employee.WorkerPayroll;
import com.zikpak.facecheck.entity.employee.WorkerSchedule;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "_user")
@EntityListeners(AuditingEntityListener.class)
@Entity
public class User implements UserDetails, Principal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, name = "first_name")
    private String firstName = "";

    @Column(nullable = true, name = "middle_initial")
    private String middleInitial = "";

    @Column(nullable = false, name = "last_name")
    private String lastName = "";


    @Column(unique = true, nullable = false, name = "email")
    private String email = "";

    @Column(name = "phone_number")
    private String phoneNumber = "";

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name = "home_address")
    private String homeAddress = "";

    @Column(name = "apt")
    private String apt= "";

    @Column(name = "city")
    private String city = "";

    @Column(name = "state")
    private String state = "";

    @Column(name = "zipcode")
    private String zipcode = "";

    @Column(name = "fcm_token", length = 512)
    private String fcmToken;

    @Column(name = "actual_budget")
    private BigDecimal actualBudget = BigDecimal.ZERO;

    @Column(name = "expenses")
    private BigDecimal expenses = BigDecimal.ZERO;

    @Column(name = "cost_of_salaries")
    private BigDecimal costOfSalaries = BigDecimal.ZERO;

    @Column(name = "profit")
    private BigDecimal profit = BigDecimal.ZERO;

///////////////////////////////////////// October 11
    @Column(name = "ssn_ciphertext")
    private byte[] ssnCiphertext;

    @Column(name = "ssn_iv")
    private byte[] ssnIv;

    @Column(name = "ssn_key_version")
    private Integer ssnKeyVersion;

    @Column(name = "ssn_h")
    private byte[] ssnH;

    @Column(name = "ssn_last4")
    private String ssnLast4;

/////////////////////////////////////////

    @Column(name = "is_remote_worker")
    private Boolean isRemoteWorker = Boolean.FALSE;
/// /////////////////////////////////////////////////////
    //All check box could be only one selected!
    //I-9 form Check box 1
    @Column(nullable = true, name = "is_citizen")
    private Boolean isCitizen = false;
    //I-9 form Check box 2
    @Column(nullable = true, name = "is_non_citizen_national_of_the_us")
    private Boolean isNonCitizenNationalOfTheUS = false;
    //I-9 form Check box 3
    @Column(nullable = true , name = "is_permanent_resident")
    private Boolean isPermanentResident = false;
    //I-9 form Check box 4
    @Column(nullable = true, name = "is_non_active" )
    private Boolean isANonCitizen = false;
    //For checkbox 4 and three!
    //All of them will be optional!

    @Column(nullable = true  , name = "is_rehired")
    private Boolean isRehired = false;

    @Column(nullable = true, name = "date_when_rehired")
    private LocalDate dateWhenRehired;

    @Column(nullable = true, name = "work_authrization_expiry_date")
    private LocalDate workAuthrizationExpiryDate;

    @Column(nullable = true, name = "uscis_number")
    private String UscisNumber = "";

    @Column(nullable = true, name ="form_i94_admission_number" )
    private String FormI94AdmissionNumber = "";

    @Column(nullable = true, name = "passport_number")
    private String PassportNumber = "";

    @Column(nullable = true, name = "passport_country_of_issuance")
    private String PassportCountryOfIssuance = "";

    @OneToMany(
            mappedBy = "user",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    private List<DocumentsI9> documentsI9;







    @Column(name = "photo_file_name")
    private String photoFileName = "";

    @Column(name = "photo_url")
    private String photoUrl = "";

    @Column(name = "base_hourly_rate")
    private BigDecimal baseHourlyRate =BigDecimal.ZERO;

    @Column(name = "overtime_rate")
    private BigDecimal overtimeRate = BigDecimal.ZERO;

    @Column(nullable = false, name = "is_admin")
    private boolean isAdmin = false;

    @Column(nullable = false, name = "is_foreman")
    private boolean isForeman = false;

    @Column(nullable = false, name = "is_user")
    private boolean isUser = false;

    @Column(nullable = false, name = "is_business_owner")
    private boolean isBusinessOwner = false;

    @Column(nullable = false, name = "password")
    private String password;

    @Builder.Default
    @Column(nullable = false, name = "enabled")
    private boolean enabled = false;

    @Builder.Default
    @Column(nullable = false, name = "account_locked")
    private boolean accountLocked = false;

    @NotNull
    @Column(name = "ssn_worker")
    private String SSN_WORKER = "";

    @Enumerated(EnumType.STRING)
    @Column(name = "gender")
    private Gender gender;

    @Enumerated(EnumType.STRING)
    @Column(nullable = true, name = "filing_status")
    private FilingStatus filingStatus;



    @Column(nullable = true, name = "dependents")
    private Integer dependents = 0; // <- Reminder this field for how many childrens has person which working! For W4 Form

    @Column(nullable = true, name = "extra_with_holdings")
    private BigDecimal extraWithHoldings = BigDecimal.ZERO;

    // === W-4 Step 2: Multiple Jobs or Spouse Works ===
    @Column(nullable = true, name = "multiple_jobs_or_spouse_works")
    private Boolean multipleJobsOrSpouseWorks = false;           // отмечено, если у сотрудника или супруга более одной работы

    @Column(nullable = true, name = "two_jobs_check_box")
    private Boolean twoJobsCheckBox = false;                     // если отмечена опция “две работы” (Step 2(c))

    @Column(precision = 10, scale = 2, nullable = true, name = "multiple_jobs_additional_withholding")
    private BigDecimal multipleJobsAdditionalWithholding = BigDecimal.ZERO; // результат Multiple Jobs Worksheet (доп. удержание)

    // === W-4 Step 3: Dependents Breakdown ===
    @Column(nullable = true, name = "dependents_under_17" )
    private Integer dependentsUnder17 = 0;                   // численность qualifying children (<17)

    /* у вас уже есть `dependents` — это общее число иждивенцев;
       для точности разбивки детей на under17 и остальных: */
    @Column(nullable = true, name = "other_dependents")
    private Integer otherDependents = 0;                     // прочие dependents (>=17 и др.)

    @Column(precision = 10, scale = 2, nullable = true, name = "total_dependents_credit")
    private BigDecimal totalDependentsCredit = BigDecimal.ZERO;;            // итоговый кредит (дети×2000 + прочие×500)

    // === W-4 Step 4: Other Adjustments ===
    @Column(precision = 12, scale = 2, nullable = true, name = "other_income")
    private BigDecimal otherIncome = BigDecimal.ZERO;;                      // прочие доходы, не из работы (Step 4(a))

    @Column(precision = 12, scale = 2, nullable = true, name = "deductions")
    private BigDecimal deductions = BigDecimal.ZERO;;                       // дополнительные вычеты (Step 4(b))

    // === W-4 Step 5: Exemption ===
    @Column(nullable = true, name = "exempt_from_withholding")
    private Boolean exemptFromWithholding = Boolean.FALSE;// если сотрудник отметил “Exempt” (Step 5)


    @Column(name = "multiple_jobs_worksheet_line_2a", nullable = true)
    private BigDecimal multipleJobsWorksheetLine2a = BigDecimal.ZERO;

    @Column(name = "multiple_jobs_worksheet_line_2b", nullable = true)
    private BigDecimal multipleJobsWorksheetLine2b = BigDecimal.ZERO;

    @Column(nullable = true, name = "estimated_itemized_deductions")
    private BigDecimal estimatedItemizedDeductions = BigDecimal.ZERO;;
    @Column(nullable = true, name = "adjustments_schedule1")
    private BigDecimal adjustmentsSchedule1 = BigDecimal.ZERO;;




    @Column(nullable = true, name = "live_in_nyc")
    private Boolean livesInNYC = false;

    @Enumerated(EnumType.STRING)
    @Column(nullable = true, name = "pay_frequency")
    private PayFrequency payFrequency;

    @Enumerated(EnumType.STRING)
    @Column(nullable = true, name = "employment_type")
    private EmploymentType employmentType;


    @Column(name = "coverage_start_date")
    private LocalDate coverageStartDate;

    @Column(name = "enrolled_in_health_plan")
    private Boolean enrolledInHealthPlan = false;

    @Column(name = "monthly_health_premium")
    private BigDecimal monthlyHealthPremium = BigDecimal.ZERO;;

    @Column(name = "period_charge_insurance")
    private BigDecimal periodChargeInsurance = BigDecimal.ZERO;;


    @Column(nullable = true, name = "sick_leave_accrued")
    private BigDecimal sickLeaveAccrued = BigDecimal.ZERO;

    @Column(nullable = true, name = "sick_leave_used")
    private BigDecimal sickLeaveUsed = BigDecimal.ZERO;

    @Column(nullable = true, name = "hours_worked_year_to_date")
    private BigDecimal hoursWorkedYearToDate = BigDecimal.ZERO;

    @Column(name = "sick_leave_accrued_this_year", nullable = true)
    private BigDecimal sickLeaveAccruedThisYear = BigDecimal.ZERO;

    /**
     * Флаг, указывающий, является ли sick leave оплачиваемым
     * Зависит от размера компании:
     * - false для компаний с 1-4 сотрудниками (неоплачиваемый)
     * - true для компаний с 5+ сотрудниками (оплачиваемый)
     */
    @Column(name = "sick_leave_paid", nullable = true)
    private Boolean sickLeavePaid = true;

    /**
     * Дата найма сотрудника
     * Необходима для расчета 120-дневного периода ожидания
     * перед возможностью использования sick leave
     */
    @Column(name = "hire_date", nullable = true)
    private LocalDate hireDate;

    /**
     * Дата последнего переноса sick leave на новый год
     * Помогает отслеживать, когда был последний carryover
     */
    @Column(name = "last_sick_leave_carryover_date", nullable = true)
    private LocalDate lastSickLeaveCarryoverDate;

    /**
     * Количество часов sick leave, перенесенных с прошлого года
     * Максимум 40 часов может быть перенесено
     */
    @Column(name = "sick_leave_carried_over", nullable = true)
    private BigDecimal sickLeaveCarriedOver = BigDecimal.ZERO;





    @OneToMany(mappedBy = "worker", cascade = CascadeType.ALL)
    private List<WorkerAttendance> attendances;

    @OneToMany(mappedBy = "worker", cascade = CascadeType.ALL)
    private List<RandomAttendanceVerification> randomAttendanceVerifications;

    @OneToMany(mappedBy = "worker", cascade = CascadeType.ALL)
    private List<WorkerPayroll> payrolls;



    @OneToMany(mappedBy = "worker", cascade = CascadeType.ALL)
    private List<WorkerSchedule> schedules;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Dependents> dependent = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    private Company company;

    @OneToOne(mappedBy = "companyOwner", fetch = FetchType.LAZY)
    private Company ownedCompany;

    @ManyToMany(mappedBy = "users")
    private Set<WorkSite> workSites = new HashSet<>();

    @OneToMany(
            mappedBy = "user",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    private List<LocationRecord> locationRecords = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wc_risk_class_code")
    private WcRiskClass wcRiskClass;



    @ManyToOne
    private WorkSite currentWorkSite;

    @CreatedDate
    @Column(updatable = false, nullable = false, name = "created_date")
    private LocalDateTime createdDate;

    @LastModifiedDate
    @Column(insertable = false, name = "last_modified_date")
    private LocalDateTime lastModifiedDate;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private List<Role> roles = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Token> tokens;

    @Override
    public String getName() {
        return email;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .toList();
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !accountLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    public String fullName() {
        return firstName + " " + lastName;
    }
}