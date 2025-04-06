package com.zikpak.facecheck.services.company;


import com.zikpak.facecheck.repository.CompanyRepository;
import com.zikpak.facecheck.repository.WorkerPayrollRepository;
import com.zikpak.facecheck.requestsResponses.company.finance.CompanyTaxCalculationRequest;
import com.zikpak.facecheck.requestsResponses.company.finance.CompanyTaxCalculationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class CompanyTaxCalculator {


    private final CompanyRepository companyRepository;
    private final WorkerPayrollRepository workerPayrollRepository;


    private BigDecimal SOCIAL_SECURITY_RATE = new BigDecimal("0.062");

    private BigDecimal MEDICARE_RATE = new BigDecimal("0.0145");

    private BigDecimal FUTA_RATE = new BigDecimal("0.06");

    private BigDecimal NY_UNEMPLOYMENT_RATE = new BigDecimal("0.041");


    public CompanyTaxCalculationResponse calculateTax(BigDecimal monthIncome, CompanyTaxCalculationRequest request, Integer companyId ) {

        var foundedCompany = companyRepository.findById(companyId)
                .orElseThrow(() -> new RuntimeException("Company not found"));

        BigDecimal totalPayRoll = workerPayrollRepository.getTotalPayrollForMonth(
                foundedCompany.getId(),
                request.getYear(),
                request.getMonth()
        );

        BigDecimal socialSecurityTax = calculateSocialSecurityTax(totalPayRoll);

        BigDecimal medicareTax = totalPayRoll.multiply(MEDICARE_RATE);

        BigDecimal futaTax =calculateFUTATax(foundedCompany.getEmployees().size());

        BigDecimal nyUiTax = calculateNYUnemploymentTax(foundedCompany.getEmployees().size());

        BigDecimal nyDisabilityTax = calculateNYDisabilityInsurance(totalPayRoll);

        BigDecimal workerComp = calculateWorkerCompensation(totalPayRoll);

        BigDecimal totalTaxes = socialSecurityTax
                .add(medicareTax)
                .add(futaTax)
                .add(nyUiTax)
                .add(nyDisabilityTax)
                .add(workerComp);

        return CompanyTaxCalculationResponse.builder()
                .companyId(foundedCompany.getId())
                .companyName(foundedCompany.getCompanyName())
                .monthlyIncome(foundedCompany.getCompanyIncomePerMonth())
                .totalTaxes(totalTaxes)
                .socialSecurityTax(socialSecurityTax)
                .medicareTax(medicareTax)
                .federalUnemploymentTax(futaTax)
                .nyUnemploymentTax(nyUiTax)
                .nyDisabilityInsurance(nyDisabilityTax)
                .workersCompensation(workerComp)
                .employeeCount(foundedCompany.getEmployees().size())
                .totalPayroll(totalPayRoll)
                .calculationDate(LocalDate.now())
                .build();

    }

    private BigDecimal calculateSocialSecurityTax(BigDecimal totalPayRoll) {
        BigDecimal socialSecurityWageCap = new BigDecimal("160200");
        return totalPayRoll.min(socialSecurityWageCap).multiply(SOCIAL_SECURITY_RATE);
    }

    private BigDecimal calculateFUTATax(int employeeCount){
        BigDecimal perEmployeeCap = new BigDecimal("7000");
        return perEmployeeCap.multiply(FUTA_RATE)
                .multiply(BigDecimal.valueOf(employeeCount));
    }

    private BigDecimal calculateNYUnemploymentTax(int employeeCount) {
        BigDecimal perEmployeeCap = new BigDecimal("12000");
        return perEmployeeCap.multiply(NY_UNEMPLOYMENT_RATE)
                .multiply(BigDecimal.valueOf(employeeCount));
    }

    private BigDecimal calculateNYDisabilityInsurance(BigDecimal totalPayRoll) {
        BigDecimal rate = new BigDecimal("0.0014");
        return totalPayRoll.multiply(rate);
    }

    private BigDecimal calculateWorkerCompensation(BigDecimal totalPayRoll) {
        BigDecimal rate = new BigDecimal("0.15");
        return totalPayRoll.multiply(rate);
    }





}
