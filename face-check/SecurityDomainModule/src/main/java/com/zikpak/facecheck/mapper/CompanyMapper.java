package com.zikpak.facecheck.mapper;

import com.zikpak.facecheck.authRequests.CompanyRegistrationAppOwnerRequest;
import com.zikpak.facecheck.authRequests.CompanyRegistrationRequest;
import com.zikpak.facecheck.entity.Company;
import com.zikpak.facecheck.entity.User;
import com.zikpak.facecheck.entity.employee.WorkerPayroll;
import com.zikpak.facecheck.repository.UserRepository;
import com.zikpak.facecheck.requestsResponses.CompanyUpdatingResponse;
import com.zikpak.facecheck.requestsResponses.company.CompanyResponse;
import com.zikpak.facecheck.requestsResponses.company.finance.CompanyIncomePerMonthResponse;
import com.zikpak.facecheck.requestsResponses.company.finance.CompanyTaxCalculationResponse;
import com.zikpak.facecheck.requestsResponses.company.finance.EmployeeSalaryResponse;
import com.zikpak.facecheck.requestsResponses.worker.RelatedUserInCompanyResponse;
import com.zikpak.facecheck.requestsResponses.worker.WorkerPayrollResponse;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class CompanyMapper {

    private final UserRepository userRepository;


    public CompanyUpdatingResponse toCompanyUpdateResponse(Company updatedCompany) {
        return CompanyUpdatingResponse.builder()
                .companyId(updatedCompany.getId())
                .companyName(updatedCompany.getCompanyName())
                .companyAddress(updatedCompany.getCompanyAddress())
                .companyPhone(updatedCompany.getCompanyPhone())
                .companyEmail(updatedCompany.getCompanyEmail())
                .workersQuantity(updatedCompany.getWorkersQuantity())
                .build();
    }

    public CompanyIncomePerMonthResponse toCompanyIncomeResponse(Company updatedIncome) {
        return CompanyIncomePerMonthResponse.builder()
                .companyId(updatedIncome.getId())
                .companyIncomePerMonth(updatedIncome.getCompanyIncomePerMonth())
                .build();
    }

    public CompanyTaxCalculationResponse toCountedTaxesResponse(CompanyTaxCalculationResponse countedTaxes) {
        return CompanyTaxCalculationResponse.builder()
                .companyId(countedTaxes.getCompanyId())
                .companyName(countedTaxes.getCompanyName())
                .monthlyIncome(countedTaxes.getMonthlyIncome())
                .totalTaxes(countedTaxes.getTotalTaxes())

                .socialSecurityTax(countedTaxes.getSocialSecurityTax())
                .medicareTax(countedTaxes.getMedicareTax())
                .federalUnemploymentTax(countedTaxes.getFederalUnemploymentTax())
                .nyUnemploymentTax(countedTaxes.getNyUnemploymentTax())
                .nyDisabilityInsurance(countedTaxes.getNyDisabilityInsurance())
                .workersCompensation(countedTaxes.getWorkersCompensation())

                .employeeCount(countedTaxes.getEmployeeCount())
                .totalPayroll(countedTaxes.getTotalPayroll())
                .calculationDate(countedTaxes.getCalculationDate())
                .build();
    }

    public RelatedUserInCompanyResponse toCompanyWorkerResponse(User foundedEmployee) {
        BigDecimal baseHourlyRate = null;
        if (foundedEmployee.getPayrolls() != null && !foundedEmployee.getPayrolls().isEmpty()) {
            baseHourlyRate = foundedEmployee.getPayrolls().getLast().getBaseHourlyRate();
        }

        return RelatedUserInCompanyResponse.builder()
                .workerId(foundedEmployee.getId())
                .companyId(foundedEmployee.getCompany().getId())
                .firstName(foundedEmployee.getFirstName())
                .lastName(foundedEmployee.getLastName())
                .email(foundedEmployee.getEmail())
                .baseHourlyRate(baseHourlyRate)
                .enabled(foundedEmployee.isEnabled())
                .photoUrl(foundedEmployee.getPhotoUrl())
                .companyName(foundedEmployee.getCompany().getCompanyName())
                .role(foundedEmployee.isAdmin() ? "ADMIN" :
                        foundedEmployee.isForeman() ? "FOREMAN" : "USER")
                .build();
    }
    public Company createNewCompany(CompanyRegistrationRequest companyRegistrationRequest) {
        return Company.builder()
                .companyName(companyRegistrationRequest.getCompanyName())
                .companyAddress(companyRegistrationRequest.getCompanyAddress())
                .companyPhone(companyRegistrationRequest.getCompanyPhone())
                .companyEmail(companyRegistrationRequest.getCompanyEmail())
                .companyCity(companyRegistrationRequest.getCompanyCity())
                .companyState(companyRegistrationRequest.getCompanyState())
                .companyZipCode(companyRegistrationRequest.getCompanyZipCode())
                .employerEIN(companyRegistrationRequest.getEmployerEIN())
                .socialSecurityTaxForCompany(companyRegistrationRequest.getSocialSecurityTaxForCompany()) // FUTA
                .companyPaymentPosition(companyRegistrationRequest.getCompanyPaymentPosition())
                .workersQuantity(0)
                .emr(companyRegistrationRequest.getExperienceModRate())
                .wcPolicyNumber(companyRegistrationRequest.getWcPolicyNumber())
                .wcInsuranceCarrier(companyRegistrationRequest.getWcInsuranceCarrier())
                .companyStateIdNumber(companyRegistrationRequest.getCompanyStateIdNumber())
                .specialTwoCharConditionCodeForMTA305(companyRegistrationRequest.getSpecialTwoCharConditionCodeForMTA305())
                .build();
    }


    public CompanyResponse toCompany(Company company) {
        return CompanyResponse.builder()
                .companyId(company.getId())
                .companyName(company.getCompanyName())
                .workersQuantity(company.getWorkersQuantity())
                .companyAddress(company.getCompanyAddress())
                .companyEmail(company.getCompanyEmail())
                .companyPhone(company.getCompanyPhone())
                .build();
    }

    public Company createNewAppOwnerCompany(CompanyRegistrationAppOwnerRequest companyRegistrationRequest) {

        var admin = userRepository.findById(companyRegistrationRequest.getCompanyAdminId()).orElseThrow(
                () -> new EntityNotFoundException("Cannot find user with provided id")
        );

        if(!admin.isAdmin()){
            throw new AccessDeniedException("Permission dined!");
        }

        return Company.builder()
                .companyOwner(admin)
                .companyName(companyRegistrationRequest.getCompanyName())
                .companyAddress(companyRegistrationRequest.getCompanyAddress())
                .companyPhone(companyRegistrationRequest.getCompanyPhone())
                .companyEmail(companyRegistrationRequest.getCompanyEmail())
                .companyCity(companyRegistrationRequest.getCompanyCity())
                .companyState(companyRegistrationRequest.getCompanyState())
                .companyZipCode(companyRegistrationRequest.getCompanyZipCode())
                .employerEIN(companyRegistrationRequest.getEmployerEIN())
                .socialSecurityTaxForCompany(companyRegistrationRequest.getSocialSecurityTaxForCompany()) // FUTA
                .companyPaymentPosition(companyRegistrationRequest.getCompanyPaymentPosition())
                .workersQuantity(0)
                .emr(companyRegistrationRequest.getExperienceModRate())
                .wcPolicyNumber(companyRegistrationRequest.getWcPolicyNumber())
                .wcInsuranceCarrier(companyRegistrationRequest.getWcInsuranceCarrier())
                .companyStateIdNumber(companyRegistrationRequest.getCompanyStateIdNumber())
                .specialTwoCharConditionCodeForMTA305(companyRegistrationRequest.getSpecialTwoCharConditionCodeForMTA305())
                .build();
    }
}
