package com.zikpak.facecheck.mapper;

import com.zikpak.facecheck.authRequests.RegistrationAdminRequest;
import com.zikpak.facecheck.authRequests.RegistrationRequest;
import com.zikpak.facecheck.entity.*;
import com.zikpak.facecheck.entity.employee.WorkSite;
import com.zikpak.facecheck.entity.employee.WorkerAttendance;
import com.zikpak.facecheck.repository.WcRiskClassRepository;
import com.zikpak.facecheck.requestsResponses.*;
import com.zikpak.facecheck.requestsResponses.admin.WorksiteWorkerResponse;
import com.zikpak.facecheck.requestsResponses.worker.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserMapper {
    private final PasswordEncoder passwordEncoder;
    private final WcRiskClassRepository wcRiskClassRepository;

    public UserFullNameResponse toUserFullNameResponse(String savedFullName) {
        return UserFullNameResponse.builder()
                .fullName(savedFullName)
                .build();
    }

    public UserEmailResponse toUserEmailResponse(String savedEmail) {
        return UserEmailResponse.builder()
                .email(savedEmail)
                .build();
    }

    public UserPhoneNumberResponse toUserPhoneNumberResponse(String phoneNumber) {
        return UserPhoneNumberResponse.builder()
                .phoneNumber(phoneNumber)
                .build();
    }

    public UserHomeAddressResponse toUserHomeAddressResponse(String homeAddress) {
        return UserHomeAddressResponse.builder()
                .homeAddress(homeAddress)
                .build();
    }

    public UserFullContactInformation toFullUserInfoResponse(User foundedUser) {
        return UserFullContactInformation.builder()
                .userId(foundedUser.getId())
                .fullName(foundedUser.fullName())
                .email(foundedUser.getEmail())
                .phoneNumber(foundedUser.getPhoneNumber())
                .address(foundedUser.getHomeAddress())
                .photoUrl(foundedUser.getPhotoUrl())
                .photoFileName(foundedUser.getPhotoFileName())
                .build();
    }


    public User toWorker(RegistrationRequest request){
        if (request.getWcRiskClassCode() == null) {
            throw new IllegalArgumentException("WC Risk Class Code is required");
        }

        WcRiskClass wcRiskClass = wcRiskClassRepository.findById(request.getWcRiskClassCode())
                .orElseThrow(() -> new IllegalArgumentException("Invalid WC Risk Class Code"));
        User user = User.builder()
                // === Основные поля ===
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .middleInitial(request.getMiddleInitial())
                .homeAddress(request.getHomeAddress())
                .city(request.getCity())
                .state(request.getState())
                .zipcode(request.getZipcode())
                .dateOfBirth(request.getDateOfBirth())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .SSN_WORKER(passwordEncoder.encode(request.getSSN_WORKER()))
                .gender(request.getGender())

                .accountLocked(false)
                .enabled(false)
                .isAdmin(false)
                .isForeman(false)
                .isUser(true)

                .phoneNumber(request.getPhoneNumber())

                .filingStatus(request.getFilingStatus())
                .dependents(request.getDependents())
                .extraWithHoldings(request.getExtraWithHoldings())
                .livesInNYC(request.getLivesInNYC())
                .payFrequency(request.getPayFrequency())
                .employmentType(request.getEmploymentType())

                .coverageStartDate(request.getCoverageStartDate())
                .enrolledInHealthPlan(request.getEnrolledInHealthPlan())
                .monthlyHealthPremium(request.getMonthlyHealthPremium())
                .apt(request.getApt())

                .multipleJobsOrSpouseWorks(request.getMultipleJobsOrSpouseWorks())
                .twoJobsCheckBox(request.getTwoJobsCheckBox())
                .multipleJobsAdditionalWithholding(request.getMultipleJobsAdditionalWithholding())

                .dependentsUnder17(request.getDependentsUnder17())
                .otherDependents(request.getOtherDependents())
                .totalDependentsCredit(request.getTotalDependentsCredit())
                .otherIncome(request.getOtherIncome())
                .deductions(request.getDeductions())
                .exemptFromWithholding(request.getExemptFromWithholding())
                .multipleJobsWorksheetLine2a(request.getMultipleJobsWorksheetLine2a())
                .multipleJobsWorksheetLine2b(request.getMultipleJobsWorksheetLine2b())
                .estimatedItemizedDeductions(request.getEstimatedItemizedDeductions())
                .adjustmentsSchedule1(request.getAdjustmentsSchedule1())
                .wcRiskClass(wcRiskClass)
                .build();

        if (request.getDependentsList() != null && !request.getDependentsList().isEmpty()) {
            List<Dependents> deps = request.getDependentsList().stream()
                    .map(dto -> {
                        Dependents d = new Dependents();
                        d.setFirstName(dto.getFirstName());
                        d.setLastName(dto.getLastName());
                        d.setBirthDate(dto.getBirthDate());
                        d.setUser(user);
                        return d;
                    })
                    .toList();
            user.setDependent(deps);
        } else {
            user.setDependent(new ArrayList<>()); // или Collections.emptyList()
        }

        if (request.getI9Documents() != null) {
            List<DocumentsI9> docs = request.getI9Documents().stream()
                    .map(d -> DocumentsI9.builder()
                            .documentTitle(d.getDocumentTitle())
                            .issuingAuthority(d.getIssuingAuthority())
                            .documentNumber(d.getDocumentNumber())
                            .expirationDate(d.getExpirationDate())
                            .user(user)
                            .build())
                    .toList();
            user.setDocumentsI9(docs);
        }

        return user;
    }


    public User toForeman(RegistrationRequest request){
        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .middleInitial(request.getMiddleInitial())
                .homeAddress(request.getHomeAddress())
                .dateOfBirth(request.getDateOfBirth())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .SSN_WORKER((request.getSSN_WORKER()))
                .gender(request.getGender())
                .accountLocked(false)
                .enabled(false)
                .isAdmin(false)
                .isForeman(true)
                .isUser(false)
                .phoneNumber(request.getPhoneNumber())
                .dependents(request.getDependentsList().size())
                .extraWithHoldings(request.getExtraWithHoldings())
                .livesInNYC(request.getLivesInNYC())
                .payFrequency(request.getPayFrequency())
                .employmentType(request.getEmploymentType())
                .coverageStartDate(request.getCoverageStartDate())
                .enrolledInHealthPlan(request.getEnrolledInHealthPlan())
                .monthlyHealthPremium(request.getMonthlyHealthPremium())
                .build();

        List<Dependents> deps = request.getDependentsList().stream()
                .map(dto -> {
                    Dependents d = new Dependents();
                    d.setFirstName(dto.getFirstName());
                    d.setLastName(dto.getLastName());
                    d.setBirthDate(dto.getBirthDate());
                    d.setUser(user);
                    return d;
                })
                .toList();
        user.setDependent(deps);
        return user;
    }

    public User toAdmin(RegistrationAdminRequest request) {
        if (request.getWcRiskClassCode() == null) {
            throw new IllegalArgumentException("WC Risk Class Code is required");
        }

        WcRiskClass wcRiskClass = wcRiskClassRepository.findById(request.getWcRiskClassCode())
                .orElseThrow(() -> new IllegalArgumentException("Invalid WC Risk Class Code"));
        User user = User.builder()
                // === Основные поля ===
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .middleInitial(request.getMiddleInitial())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .SSN_WORKER(passwordEncoder.encode(request.getSSN_WORKER()))
                .dateOfBirth(request.getDateOfBirth())
                .gender(request.getGender())
                .phoneNumber(request.getPhoneNumber())

                // === Адресные поля ===
                .homeAddress(request.getHomeAddress())
                .apt(request.getApt())
                .city(request.getCity())
                .state(request.getState())
                .zipcode(request.getZipcode())

                // === Роли и флаги доступа ===
                .accountLocked(false)
                .enabled(false)
                .isAdmin(true)
                .isForeman(false)
                .isUser(false)

                // === Здоровье ===
                .coverageStartDate(request.getCoverageStartDate())
                .enrolledInHealthPlan(request.getEnrolledInHealthPlan())
                .monthlyHealthPremium(request.getMonthlyHealthPremium())

                // === W-4 Step 1: Filing & Dependents ===
                .filingStatus(request.getFilingStatus())
                .dependents(request.getDependents())               // общее число иждивенцев
                .dependentsUnder17(request.getDependentsUnder17())
                .otherDependents(request.getOtherDependents())
                .totalDependentsCredit(request.getTotalDependentsCredit())
                .extraWithHoldings(request.getExtraWithHoldings())

                // === W-4 Step 2: Multiple Jobs or Spouse Works ===
                .multipleJobsOrSpouseWorks(request.getMultipleJobsOrSpouseWorks())
                .twoJobsCheckBox(request.getTwoJobsCheckBox())
                .multipleJobsAdditionalWithholding(request.getMultipleJobsAdditionalWithholding())

                // === W-4 Step 4: Other Adjustments ===
                .otherIncome(request.getOtherIncome())
                .deductions(request.getDeductions())

                // === W-4 Step 5: Exemption ===
                .exemptFromWithholding(request.getExemptFromWithholding())

                // === Payroll Settings ===
                .livesInNYC(request.getLivesInNYC())
                .payFrequency(request.getPayFrequency())
                .employmentType(request.getEmploymentType())
                .multipleJobsWorksheetLine2a(request.getMultipleJobsWorksheetLine2a())
                .multipleJobsWorksheetLine2b(request.getMultipleJobsWorksheetLine2b())
                .estimatedItemizedDeductions(request.getEstimatedItemizedDeductions())
                .adjustmentsSchedule1(request.getAdjustmentsSchedule1())
                .wcRiskClass(wcRiskClass)
                .build();

        if (request.getDependentsList() != null && !request.getDependentsList().isEmpty()) {
            List<Dependents> deps = request.getDependentsList().stream()
                    .map(dto -> {
                        Dependents d = new Dependents();
                        d.setFirstName(dto.getFirstName());
                        d.setLastName(dto.getLastName());
                        d.setBirthDate(dto.getBirthDate());
                        d.setUser(user);
                        return d;
                    })
                    .toList();
            user.setDependent(deps);
        } else{
            user.setDependent(new ArrayList<>());
        }
        if (request.getI9Documents() != null) {
            List<DocumentsI9> docs = request.getI9Documents().stream()
                    .map(d -> DocumentsI9.builder()
                            .documentTitle(d.getDocumentTitle())
                            .issuingAuthority(d.getIssuingAuthority())
                            .documentNumber(d.getDocumentNumber())
                            .expirationDate(d.getExpirationDate())
                            .user(user)
                            .build())
                    .toList();
            user.setDocumentsI9(docs);
        }

        return user;
    }


    public WorksiteWorkerResponse toUserWorkSiteResponse(User user) {
        return WorksiteWorkerResponse.builder()
                .workerId(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .phoneNumber(user.getPhoneNumber())
                .workSiteAddress(user.getWorkSites().stream()
                        .findFirst()
                        .map(WorkSite::getAddress)
                        .orElse(null))
                .punchIn(user.getAttendances().stream()
                        .max(Comparator.comparing(WorkerAttendance::getCheckInTime))
                        .map(WorkerAttendance::getCheckInTime)
                        .orElseThrow(() -> new EntityNotFoundException("No check-in time found for user: " + user.getId())))
                .build();
    }




    public UserCompanyNameInformation toUserCompanyNameResponse(String savedCompanyName) {
        return UserCompanyNameInformation.builder()
                .companyName(savedCompanyName)
                .build();
    }

    public WorkerCompanyIdByAuthenticationResponse toWorkerCompanyIdByAuthenticationResponse(Integer foundedCompanyId) {
        return  WorkerCompanyIdByAuthenticationResponse.builder()
                .companyId(foundedCompanyId)
                .build();
    }


    public WorkerPersonalInformationResponse toWorkerPersonalInformationResponse(User foundedUser) {
        return WorkerPersonalInformationResponse.builder()
                .workerId(foundedUser.getId())
                .companyId(foundedUser.getCompany().getId())
                .firstName(foundedUser.getFirstName())
                .lastName(foundedUser.getLastName())
                .email(foundedUser.getEmail())
                .companyName(foundedUser.getCompany().getCompanyName())
                .phoneNumber(foundedUser.getPhoneNumber())
                .address(foundedUser.getHomeAddress())
                .baseHourlyRate(foundedUser.getBaseHourlyRate())
                .role(foundedUser.getRoles().stream()
                                .map(Role::getName)
                                .findFirst()
                                .orElse("USER"))
                .build();
    }

    public UserCompanyAddressResponse toUserCompanyAddressResponse(String savedCompanyName) {
        return UserCompanyAddressResponse.builder()
                .companyAddress(savedCompanyName)
                .build();
    }

    public UserCompanyPhoneNumberResponse toUserCompanyPhoneNumberResponse(String savedCompanyPhone) {
        return UserCompanyPhoneNumberResponse.builder()
                .phoneNumber(savedCompanyPhone)
                .build();
    }

    public UserCompanyEmailResponse toUserCompanyEmailResponse(String savedCompanyPhone) {
        return UserCompanyEmailResponse.builder()
                .email(savedCompanyPhone)
                .build();
    }

    public RelatedUserInCompanyResponse toRelatedUserInCompanyResponse(User user) {
        return RelatedUserInCompanyResponse.builder()
                .workerId(user.getId())
                .companyId(user.getCompany().getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .baseHourlyRate(user.getBaseHourlyRate())
                .enabled(user.isEnabled())
                .build();
    }
}
