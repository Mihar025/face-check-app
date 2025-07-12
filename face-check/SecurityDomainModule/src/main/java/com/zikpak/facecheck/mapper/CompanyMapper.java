package com.zikpak.facecheck.mapper;

import com.zikpak.facecheck.authRequests.CompanyRegistrationRequest;
import com.zikpak.facecheck.entity.Company;
import com.zikpak.facecheck.entity.User;
import com.zikpak.facecheck.entity.employee.WorkerPayroll;
import com.zikpak.facecheck.requestsResponses.CompanyUpdatingResponse;
import com.zikpak.facecheck.requestsResponses.company.finance.CompanyIncomePerMonthResponse;
import com.zikpak.facecheck.requestsResponses.company.finance.CompanyTaxCalculationResponse;
import com.zikpak.facecheck.requestsResponses.company.finance.EmployeeSalaryResponse;
import com.zikpak.facecheck.requestsResponses.worker.RelatedUserInCompanyResponse;
import com.zikpak.facecheck.requestsResponses.worker.WorkerPayrollResponse;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class CompanyMapper {


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

    public EmployeeSalaryResponse toEmployeeBaseHourRate(WorkerPayroll workerPayroll) {
        return EmployeeSalaryResponse.builder()
                .employeeId(workerPayroll.getWorker().getId())
                .firstName(workerPayroll.getWorker().getFirstName())
                .lastName(workerPayroll.getWorker().getLastName())
                .email(workerPayroll.getWorker().getEmail())
                .baseHourlyRate(workerPayroll.getBaseHourlyRate())
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

    public WorkerPayrollResponse toWorkerPayrollResponse(WorkerPayroll payroll) {
        return WorkerPayrollResponse.builder()
                .workerId(payroll.getWorker().getId())
                .workerFullName(payroll.getWorker().getFirstName() + " " + payroll.getWorker().getLastName())
                .periodStart(payroll.getPeriodStart())
                .periodEnd(payroll.getPeriodEnd())
                .regularHours(payroll.getRegularHours())
                .overtimeHours(payroll.getOvertimeHours())
                .totalHours(payroll.getTotalHours())
                .regularPay(payroll.getRegularPay())
                .overtimePay(payroll.getOvertimePay())
                .grossPay(payroll.getGrossPay())
                .medicare(payroll.getMedicare())
                .socialSecurity(payroll.getSocialSecurityEmployee())
                .federalWithholding(payroll.getFederalWithholding())
                .stateWithholding(payroll.getNyStateWithholding())
                .localWithholding(payroll.getNyLocalWithholding())
                .disability(payroll.getNyDisabilityWithholding())
                .paidFamilyLeave(payroll.getNyPaidFamilyLeave())
                .totalDeductions(payroll.getTotalDeductions())
                .netPay(payroll.getNetPay())
                .build();
    }
}
