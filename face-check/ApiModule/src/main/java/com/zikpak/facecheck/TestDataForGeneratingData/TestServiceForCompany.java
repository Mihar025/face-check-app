package com.zikpak.facecheck.TestDataForGeneratingData;


import com.zikpak.facecheck.entity.Company;
import com.zikpak.facecheck.entity.CompanyPaymentPosition;
import com.zikpak.facecheck.entity.User;
import com.zikpak.facecheck.repository.CompanyRepository;
import com.zikpak.facecheck.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class TestServiceForCompany {
        private final CompanyRepository companyRepository;

    public Company createCompany1(){
        Company testCompany1 = new Company();
        testCompany1.setId(1);
        testCompany1.setCompanyName("TEST COMPANY 1");
        testCompany1.setCompanyPhone("+1-347-828-5790");
        testCompany1.setCompanyAddress("407 Ocena View1");
        testCompany1.setCompanyEmail("mishamay583@gmail.com");
        testCompany1.setCompanyCity("Brooklyn");
        testCompany1.setCompanyState("NY");
        testCompany1.setCompanyZipCode("11235");
        testCompany1.setEmployerEIN("12-3456789");
        // testCompany1.setCompanyOwner(admin);
        testCompany1.setSocialSecurityTaxForCompany(BigDecimal.valueOf(4.1));
        testCompany1.setCompanyPaymentPosition(CompanyPaymentPosition.WEEKLY);
        testCompany1.setEmr(BigDecimal.valueOf(1.25));                  // EMR — Experience Modifier Rate
        testCompany1.setWcPolicyNumber("WC-2025-12345");                // номер полиса
        testCompany1.setWcInsuranceCarrier("ACME Workers’ Comp Ins.");  // страховая компания
        testCompany1.setCompanyStateIdNumber("208105407");
        testCompany1.setSpecialTwoCharConditionCodeForMTA305("F2");


        return companyRepository.save(testCompany1);

    }

    public Company createCompany2(){

        Company testCompany2 = new Company();
        testCompany2.setId(2);
        testCompany2.setCompanyName("TEST COMPANY 2");
        testCompany2.setCompanyPhone("+1-347-828-5799");
        testCompany2.setCompanyAddress("407 Ocen View 1");
        testCompany2.setCompanyEmail("mishamaykinghsbr1@gmail.com");
        testCompany2.setCompanyCity("Brooklyn");
        testCompany2.setCompanyState("NY");
        testCompany2.setCompanyZipCode("11235");
        testCompany2.setEmployerEIN("12-3456789");
        testCompany2.setSocialSecurityTaxForCompany(BigDecimal.valueOf(4.1));
        testCompany2.setCompanyPaymentPosition(CompanyPaymentPosition.BIWEEKLY);
        testCompany2.setFirstBiweeklyDate(LocalDate.now());
        testCompany2.setEmr(BigDecimal.valueOf(1.25));                  // EMR — Experience Modifier Rate
        testCompany2.setWcPolicyNumber("WC-2025-12345");                // номер полиса
        testCompany2.setWcInsuranceCarrier("ACME Workers’ Comp Ins.");  // страховая компания
        testCompany2.setCompanyStateIdNumber("208105407");
        testCompany2.setSpecialTwoCharConditionCodeForMTA305("F2");
        return companyRepository.save(testCompany2);
    }


}
