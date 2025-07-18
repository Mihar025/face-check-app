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

    @Column(nullable = false)
    private String firstName;
    @Column(nullable = true)
    private String middleInitial;

    @Column(nullable = false)
    private String lastName;


    @Column(unique = true, nullable = false)
    private String email;

    private String phoneNumber;

    private LocalDate dateOfBirth;

    private String homeAddress;
    private String apt;

    private String city;
    private String state;
    private String zipcode;





    //All check box could be only one selected!
    //I-9 form Check box 1
    @Column(nullable = true)
    private Boolean isCitizen = false;
    //I-9 form Check box 2
    @Column(nullable = true)
    private Boolean isNonCitizenNationalOfTheUS = false;
    //I-9 form Check box 3
    @Column(nullable = true)
    private Boolean isPermanentResident = false;
    //I-9 form Check box 4
    @Column(nullable = true)
    private Boolean isANonCitizen = false;
    //For checkbox 4 and three!
    //All of them will be optional!

    @Column(nullable = true)
    private Boolean isRehired = false;

    @Column(nullable = true)
    private LocalDate dateWhenRehired;

    @Column(nullable = true)
    private LocalDate workAuthrizationExpiryDate;

    @Column(nullable = true)
    private String UscisNumber;

    @Column(nullable = true)
    private String FormI94AdmissionNumber;

    @Column(nullable = true)
    private String PassportNumber;

    @Column(nullable = true)
    private String PassportCountryOfIssuance;

    @OneToMany(
            mappedBy = "user",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    private List<DocumentsI9> documentsI9;







    @Column
    private String photoFileName;
    @Column
    private String photoUrl;

    private BigDecimal baseHourlyRate;
    private BigDecimal overtimeRate;

    @Column(nullable = false)
    private boolean isAdmin;
    @Column(nullable = false)
    private boolean isForeman;
    @Column(nullable = false)
    private boolean isUser;
    @Column(nullable = false)
    private boolean isBusinessOwner;

    @Column(nullable = false)
    private String password;

    @Builder.Default
    @Column(nullable = false)
    private boolean enabled = false;

    @Builder.Default
    @Column(nullable = false)
    private boolean accountLocked = false;

    @NotNull
    private String SSN_WORKER;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Enumerated(EnumType.STRING)
    @Column(nullable = true)
    private FilingStatus filingStatus;



    @Column(nullable = true)
    private Integer dependents; // <- Reminder this field for how many childrens has person which working! For W4 Form

    @Column(nullable = true)
    private BigDecimal extraWithHoldings;

    // === W-4 Step 2: Multiple Jobs or Spouse Works ===
    @Column(nullable = true)
    private Boolean multipleJobsOrSpouseWorks;           // отмечено, если у сотрудника или супруга более одной работы

    @Column(nullable = true)
    private Boolean twoJobsCheckBox;                     // если отмечена опция “две работы” (Step 2(c))

    @Column(precision = 10, scale = 2, nullable = true)
    private BigDecimal multipleJobsAdditionalWithholding; // результат Multiple Jobs Worksheet (доп. удержание)

    // === W-4 Step 3: Dependents Breakdown ===
    @Column(nullable = true)
    private Integer dependentsUnder17;                   // численность qualifying children (<17)

    /* у вас уже есть `dependents` — это общее число иждивенцев;
       для точности разбивки детей на under17 и остальных: */
    @Column(nullable = true)
    private Integer otherDependents;                     // прочие dependents (>=17 и др.)

    @Column(precision = 10, scale = 2, nullable = true)
    private BigDecimal totalDependentsCredit;            // итоговый кредит (дети×2000 + прочие×500)

    // === W-4 Step 4: Other Adjustments ===
    @Column(precision = 12, scale = 2, nullable = true)
    private BigDecimal otherIncome;                      // прочие доходы, не из работы (Step 4(a))

    @Column(precision = 12, scale = 2, nullable = true)
    private BigDecimal deductions;                       // дополнительные вычеты (Step 4(b))

    // === W-4 Step 5: Exemption ===
    @Column(nullable = true)
    private Boolean exemptFromWithholding;// если сотрудник отметил “Exempt” (Step 5)


    @Column(nullable = true)
    private BigDecimal multipleJobsWorksheetLine2a;
    @Column(nullable = true)
    private BigDecimal multipleJobsWorksheetLine2b;
    @Column(nullable = true)
    private BigDecimal estimatedItemizedDeductions;
    @Column(nullable = true)
    private BigDecimal adjustmentsSchedule1;




    @Column(nullable = true)
    private Boolean livesInNYC;

    @Enumerated(EnumType.STRING)
    @Column(nullable = true)
    private PayFrequency payFrequency;

    @Enumerated(EnumType.STRING)
    @Column(nullable = true)
    private EmploymentType employmentType;


    private LocalDate coverageStartDate;

    private Boolean enrolledInHealthPlan;

    private BigDecimal monthlyHealthPremium;

    private BigDecimal periodChargeInsurance;


    @Column(nullable = true)
    private BigDecimal sickLeaveAccrued = BigDecimal.ZERO;

    @Column(nullable = true)
    private BigDecimal sickLeaveUsed = BigDecimal.ZERO;

    @Column(nullable = true)
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
    @Column(updatable = false, nullable = false)
    private LocalDateTime createdDate;

    @LastModifiedDate
    @Column(insertable = false)
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