package com.zikpak.facecheck.authRequests;

import com.zikpak.facecheck.entity.Gender;
import com.zikpak.facecheck.entity.W4.EmploymentType;
import com.zikpak.facecheck.entity.W4.FilingStatus;
import com.zikpak.facecheck.entity.W4.PayFrequency;
import com.zikpak.facecheck.requestsResponses.DependentsRequest;
import jakarta.persistence.Column;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationAdminRequest {
    @NotBlank(message = "Firstname cannot be empty or blank")
    private String firstName;
    @NotBlank(message = "Lastname cannot be empty or blank")
    private String lastName;

    private String middleInitial;

    @NotBlank(message = "Home Address cannot be empty or blank")
    private String homeAddress;

    private LocalDate dateOfBirth;

    @NotBlank(message = "Apartment cannot be empty or blank")
    private String apt;

    @NotNull(message = "Gender is required")
    private Gender gender;

    @NotBlank(message = "Phonenumber is required!")
    private String phoneNumber;

    private String SSN_WORKER;

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
    @NotNull(message = "Number of Child is required")
    private Integer dependents;

    @NotNull
    @Size(max = 10)
    private List<@Valid DependentsRequest> dependentsList;
    @NotNull(message = "City status is required")
    private String city;
    @NotNull(message = "State status is required")
    private String state;
    @NotNull(message = "ZipCode status is required")
    private String zipcode;

    @NotNull(message = "Extra withholding is required")
    private BigDecimal extraWithHoldings;

    @NotNull(message = " is person live in NYC required")
    private Boolean livesInNYC;

    @NotNull(message = " Pay Frequency is required")
    private PayFrequency payFrequency;

    @NotNull(message = "Employment type is required")
    private EmploymentType employmentType;


    // === W-4 Step 2: Multiple Jobs or Spouse Works ===
    @NotNull(message = "Укажите, есть ли у вас несколько работ или работает ли супруг(а)")
    private Boolean multipleJobsOrSpouseWorks;

    @NotNull(message = "Укажите, отмечен ли чекбокс 'две работы' (Step 2(c))")
    private Boolean twoJobsCheckBox;

    @DecimalMin(value = "0.00", message = "Дополнительное удержание должно быть неотрицательным")
    private BigDecimal multipleJobsAdditionalWithholding;

    // === W-4 Step 3: Dependents Breakdown ===
    @Min(value = 0, message = "Количество детей до 17 лет не может быть отрицательным")
    private Integer dependentsUnder17;

    @Min(value = 0, message = "Количество прочих dependents не может быть отрицательным")
    private Integer otherDependents;

    @DecimalMin(value = "0.00", message = "Сумма кредитов не может быть отрицательной")
    private BigDecimal totalDependentsCredit;

    // === W-4 Step 4: Other Adjustments ===
    @DecimalMin(value = "0.00", message = "Other income не может быть отрицательным")
    private BigDecimal otherIncome;

    @DecimalMin(value = "0.00", message = "Deductions должно быть неотрицательным")
    private BigDecimal deductions;

    // === W-4 Step 5: Exemption ===
    @NotNull(message = "Укажите, освобождены ли вы от удержания (Step 5)")
    private Boolean exemptFromWithholding;

    private BigDecimal multipleJobsWorksheetLine2a;
    private BigDecimal multipleJobsWorksheetLine2b;
    private BigDecimal estimatedItemizedDeductions;
    private BigDecimal adjustmentsSchedule1;
    private String wcRiskClassCode;

    private Boolean isCitizen;
    private Boolean isNonCitizenNationalOfTheUS;
    private Boolean isPermanentResident;
    private Boolean isANonCitizen;

    private Boolean isRehired;
    private LocalDate dateWhenRehired;
    private LocalDate workAuthorizationExpiryDate;
    private String uscisNumber;
    private String formI94AdmissionNumber;
    private String passportNumber;
    private String passportCountryOfIssuance;

    @Valid
    @Size(max = 3, message = "Можно загрузить до 3 документов")
    private List<I9DocumentRequest> i9Documents;



}
