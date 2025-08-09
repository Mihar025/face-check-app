package com.zikpak.facecheck.authRequests;

import com.zikpak.facecheck.entity.Dependents;
import com.zikpak.facecheck.entity.Gender;
import com.zikpak.facecheck.entity.W4.EmploymentType;
import com.zikpak.facecheck.entity.W4.FilingStatus;
import com.zikpak.facecheck.entity.W4.PayFrequency;
import com.zikpak.facecheck.requestsResponses.DependentsRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegistrationRequest {

    @NotBlank(message = "Firstname cannot be empty or blank")
    private String firstName;
    @NotBlank(message = "Lastname cannot be empty or blank")
    private String lastName;

    private String middleInitial;

    @NotBlank(message = "Home Address cannot be empty or blank")
    private String homeAddress;

    @NotBlank(message = "Apartment cannot be empty or blank")
    private String apt;

    @NotNull(message = "Date of birth is required")
    private LocalDate dateOfBirth;

    @NotBlank(message = "Company name cannot be empty or blank")
    private String companyName;

    @NotBlank(message = "Company address cannot be empty or blank")
    private String companyAddress;

    @NotNull(message = "Gender is required")
    private Gender gender;

    @NotNull(message = "SSN is required")
    private String SSN_WORKER;

    @NotBlank(message = "Phonenumber is required!")
    private String phoneNumber;


    @Email(regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$", message = "Email is not formatted well!")
    @NotBlank(message = "Email is required!")
    @Pattern(regexp = "^[^;'\"]*$", message = "Email contains invalid characters")
    private String email;

    private LocalDate coverageStartDate;
    private Boolean enrolledInHealthPlan;        // true, если сотрудник выбрал страховой план
    private BigDecimal monthlyHealthPremium;

    @NotBlank(message = "Password is mandatory!")
    @Size(min = 6, message = "Password should be minimum 4 characters")
    private String password;


    @NotNull(message = "Filling status is required")
    private FilingStatus filingStatus;

    private Integer dependents;

    @Size(max = 10)
    private List<@Valid DependentsRequest> dependentsList;

    private BigDecimal extraWithHoldings;

    @NotNull(message = " is person live in NYC required")
    private Boolean livesInNYC;

    @NotNull(message = " Pay Frequency is required")
    private PayFrequency payFrequency;

    @NotNull(message = "Employment type is required")
    private EmploymentType employmentType;

    @NotNull(message = "This field maximal required!!!")
    private String city;

    @NotNull(message = "This field maximal required!!!")
    private String state;

    @NotNull(message = "This field maximal required!!!")
    private String zipcode;

    // === W-4 Step 2: Multiple Jobs or Spouse Works ===
    @NotNull(message = "Do you have few jobs? Or your spouse is working?")
    private Boolean multipleJobsOrSpouseWorks;

    @NotNull(message = "Two jobs? (Step 2(c))")
    private Boolean twoJobsCheckBox;

    @DecimalMin(value = "0.00", message = "Additional withholding cannot be negative")
    private BigDecimal multipleJobsAdditionalWithholding;

    // === W-4 Step 3: Dependents & Other Credits ===
    @Min(value = 0, message = "Quantity of children before  17 years cannot be negative")
    private Integer dependentsUnder17;

    @Min(value = 0, message = "Quantity of other dependents cannot be negative")
    private Integer otherDependents;

    @DecimalMin(value = "0.00", message = "Total Dependents credit cannot be negative")
    private BigDecimal totalDependentsCredit;

    // === W-4 Step 4: Other Adjustments ===
    @DecimalMin(value = "0.00", message = "Other income cannot be negative")
    private BigDecimal otherIncome;

    @DecimalMin(value = "0.00", message = "Deductions cannot be negative")
    private BigDecimal deductions;


    // === W-4 Step 5: Exemption ===
    @NotNull(message = "Are you free from withholding? (Step 5)")
    private Boolean exemptFromWithholding;

    private BigDecimal multipleJobsWorksheetLine2a;
    private BigDecimal multipleJobsWorksheetLine2b;
    private BigDecimal estimatedItemizedDeductions;
    private BigDecimal adjustmentsSchedule1;

    @NotNull(message = "This field is required!")
    private String wcRiskClassCode;

    @NotNull(message = "Are you citizen of the USA")
    private Boolean isCitizen;
    @NotNull(message = "Are non-citizen national")
    private Boolean isNonCitizenNationalOfTheUS;
    @NotNull(message = "Are permanent resident")
    private Boolean isPermanentResident;
    @NotNull(message = "Are, non-citizen you")
    private Boolean isANonCitizen;

    private Boolean isRehired;

    private LocalDate dateWhenRehired;

    private LocalDate workAuthorizationExpiryDate;

    private String uscisNumber;

    private String formI94AdmissionNumber;

    private String passportNumber;

    private String passportCountryOfIssuance;

    @Valid
    @Size(max = 3, message = "Could Load 3 Documents")
    private List<I9DocumentRequest> i9Documents;




}
