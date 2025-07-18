package com.zikpak.facecheck.TestDataForGeneratingData;


import com.zikpak.facecheck.entity.*;
import com.zikpak.facecheck.entity.W4.EmploymentType;
import com.zikpak.facecheck.entity.W4.FilingStatus;
import com.zikpak.facecheck.entity.W4.PayFrequency;
import com.zikpak.facecheck.repository.CompanyRepository;
import com.zikpak.facecheck.repository.RoleRepository;
import com.zikpak.facecheck.repository.UserRepository;
import com.zikpak.facecheck.repository.WcRiskClassRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserTestServiceData {
    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final RoleRepository roleRepository;
    private final WorkerScheduleTest workerScheduleTest;
    private final WcRiskClassRepository wcRiskClassRepository;

    public User createAdmin1(){
        List<Role> allRoles = roleRepository.findAll();
        String rolesDesc = allRoles.stream()
                .map(r -> String.format("(%d: %s)", r.getId(), r.getName()))
                .collect(Collectors.joining(", "));
        log.info("Available roles in DB: " + rolesDesc);

        var adminRole = roleRepository.findById(2)
                .orElseThrow();
        User adminForCompany1 = new User();
        adminForCompany1.setFirstName("Test ");
        adminForCompany1.setLastName(" Admin1");
        adminForCompany1.setMiddleInitial("");
        adminForCompany1.setEmail("mishamay583@gmail.com");
        adminForCompany1.setPhoneNumber("+1-347-828-5790");
        adminForCompany1.setDateOfBirth(LocalDate.of(1995, 8, 8));
        adminForCompany1.setHomeAddress("407 Ocean View Ave #1A");
        adminForCompany1.setCity("Brooklyn");
        adminForCompany1.setState("NY");
        adminForCompany1.setZipcode("11235");
        adminForCompany1.setBaseHourlyRate(BigDecimal.valueOf(25));
        adminForCompany1.setAdmin(true);
        adminForCompany1.setForeman(false);
        adminForCompany1.setBusinessOwner(true);
        adminForCompany1.setUser(false);
        adminForCompany1.setPassword("12345678");
        adminForCompany1.setEnabled(true);
        adminForCompany1.setAccountLocked(false);
        adminForCompany1.setSSN_WORKER("123-12-1234");
        adminForCompany1.setGender(Gender.MALE);
        adminForCompany1.setFilingStatus(FilingStatus.SINGLE);
        adminForCompany1.setDependents(0);
        adminForCompany1.setExtraWithHoldings(BigDecimal.valueOf(0));
        adminForCompany1.setLivesInNYC(true);
        adminForCompany1.setPayFrequency(PayFrequency.WEEKLY);
        adminForCompany1.setEmploymentType(EmploymentType.W2);
        adminForCompany1.setCoverageStartDate(null);
        adminForCompany1.setEnrolledInHealthPlan(false);
        adminForCompany1.setMonthlyHealthPremium(null);
        adminForCompany1.setPeriodChargeInsurance(null);
        adminForCompany1.setSickLeavePaid(true);
        adminForCompany1.setHireDate(LocalDate.of(2025, 1,1));
    //    adminForCompany1.setCompany(adminsCompany1);
    //    adminForCompany1.setOwnedCompany(adminsCompany1);
        adminForCompany1.setCreatedDate(LocalDateTime.now());
        adminForCompany1.setLastModifiedDate(LocalDateTime.now());
        adminForCompany1.setRoles(new ArrayList<>(List.of(adminRole)));
      //  WcRiskClass riskClass = wcRiskClassRepository
     //           .findById("5190").                  // например, «Electrical Wiring»
      //  adminForCompany1.setWcRiskClass(riskClass);
         userRepository.save(adminForCompany1);
        workerScheduleTest.generateScheduleForWorkers(adminForCompany1.getId());
        return adminForCompany1;

    }


    public User createAdmin2(){
        var adminRole = roleRepository.findById(2)
                .orElseThrow();
        User adminForCompany1 = new User();
        adminForCompany1.setFirstName("Test ");
        adminForCompany1.setLastName(" Admin2");
        adminForCompany1.setMiddleInitial("");
        adminForCompany1.setEmail("mishamaykinghsbr1@gmail.com");
        adminForCompany1.setPhoneNumber("+1-347-828-5790");
        adminForCompany1.setDateOfBirth(LocalDate.of(1995, 8, 8));
        adminForCompany1.setHomeAddress("407 Ocean View Ave #2A");
        adminForCompany1.setCity("Brooklyn");
        adminForCompany1.setState("NY");
        adminForCompany1.setZipcode("11236");
        adminForCompany1.setBaseHourlyRate(BigDecimal.valueOf(25));
        adminForCompany1.setAdmin(true);
        adminForCompany1.setForeman(false);
        adminForCompany1.setBusinessOwner(true);
        adminForCompany1.setUser(false);
        adminForCompany1.setPassword("12345678");
        adminForCompany1.setEnabled(true);
        adminForCompany1.setAccountLocked(false);
        adminForCompany1.setSSN_WORKER("123-12-1234");
        adminForCompany1.setGender(Gender.MALE);
        adminForCompany1.setFilingStatus(FilingStatus.SINGLE);
        adminForCompany1.setDependents(0);
        adminForCompany1.setExtraWithHoldings(BigDecimal.valueOf(0));
        adminForCompany1.setLivesInNYC(true);
        adminForCompany1.setPayFrequency(PayFrequency.BIWEEKLY);
        adminForCompany1.setEmploymentType(EmploymentType.W2);
        adminForCompany1.setCoverageStartDate(null);
        adminForCompany1.setEnrolledInHealthPlan(false);
        adminForCompany1.setMonthlyHealthPremium(null);
        adminForCompany1.setPeriodChargeInsurance(null);
        adminForCompany1.setSickLeavePaid(true);
        adminForCompany1.setHireDate(LocalDate.of(2025, 1,1));
    //    adminForCompany1.setCompany(adminsCompany1);
    //    adminForCompany1.setOwnedCompany(adminsCompany1);
        adminForCompany1.setCreatedDate(LocalDateTime.now());
        adminForCompany1.setLastModifiedDate(LocalDateTime.now());
        adminForCompany1.setRoles(new ArrayList<>(List.of(adminRole)));

    //    WcRiskClass riskClass = wcRiskClassRepository
    //            .findById("5190")                   // например, «Electrical Wiring»
    //            .orElseThrow();
     //   adminForCompany1.setWcRiskClass(riskClass);

        userRepository.save(adminForCompany1);
        workerScheduleTest.generateScheduleForWorkers(adminForCompany1.getId());
        return adminForCompany1;
    }



    public void createWorker1ForCompany1(Integer companyId) {
        Company company = companyRepository.findById(companyId).orElseThrow();
        Role userRole = roleRepository.findById(1).orElseThrow();

        User u = new User();
        u.setFirstName("Worker");
        u.setLastName("1");
        u.setEmail("worker1@example.com");
        u.setPhoneNumber("+1-347-000-0001");
        u.setDateOfBirth(LocalDate.of(1990, 1, 1));
        u.setHomeAddress("100 Main St");
        u.setCity("Brooklyn");
        u.setState("NY");
        u.setZipcode("11201");
        u.setBaseHourlyRate(BigDecimal.valueOf(20));
        u.setOvertimeRate(BigDecimal.valueOf(30));
        u.setAdmin(false);
        u.setForeman(false);
        u.setBusinessOwner(false);
        u.setUser(true);
        u.setPassword("password1");
        u.setEnabled(true);
        u.setAccountLocked(false);
        u.setSSN_WORKER("111-11-1111");
        u.setGender(Gender.MALE);

        // W-4 Step 1
        u.setFilingStatus(FilingStatus.SINGLE);
        u.setDependents(0);
        u.setExtraWithHoldings(BigDecimal.ZERO);

        // W-4 Step 2 (нет multiple jobs)
        u.setMultipleJobsOrSpouseWorks(false);
        u.setTwoJobsCheckBox(false);
        u.setMultipleJobsAdditionalWithholding(BigDecimal.ZERO);

        // W-4 Step 3 (нет детей)
        u.setDependentsUnder17(0);
        u.setOtherDependents(0);
        u.setTotalDependentsCredit(BigDecimal.ZERO);

        // W-4 Step 4
        u.setOtherIncome(BigDecimal.ZERO);
        u.setDeductions(BigDecimal.ZERO);

        // W-4 Step 5
        u.setExemptFromWithholding(false);

        // Остальные поля
        u.setLivesInNYC(true);
        u.setPayFrequency(PayFrequency.WEEKLY);
        u.setEmploymentType(EmploymentType.W2);
        u.setCoverageStartDate(null);
        u.setEnrolledInHealthPlan(false);
        u.setMonthlyHealthPremium(null);

        u.setCompany(company);
       u.setRoles(new ArrayList<>(List.of(userRole)));
    //    WcRiskClass riskClass = wcRiskClassRepository
  //              .findById("5190")                   // например, «Electrical Wiring»
  //              .orElseThrow();
 //       u.setWcRiskClass(riskClass);
        userRepository.save(u);
        workerScheduleTest.generateScheduleForWorkers(u.getId());
    }

    public void createWorker2ForCompany1(Integer companyId) {
        Company company = companyRepository.findById(companyId).orElseThrow();
        Role userRole = roleRepository.findById(1).orElseThrow();

        User u = new User();
        u.setFirstName("Worker");
        u.setLastName("2");
        u.setEmail("worker2@example.com");
        u.setPhoneNumber("+1-347-000-0002");
        u.setDateOfBirth(LocalDate.of(1988, 2, 2));
        u.setHomeAddress("200 Main St");
        u.setCity("Brooklyn");
        u.setState("NY");
        u.setZipcode("11202");
        u.setBaseHourlyRate(BigDecimal.valueOf(22));
        u.setOvertimeRate(BigDecimal.valueOf(33));
        u.setAdmin(false);
        u.setForeman(false);
        u.setBusinessOwner(false);
        u.setUser(true);
        u.setPassword("password2");
        u.setEnabled(true);
        u.setAccountLocked(false);
        u.setSSN_WORKER("222-22-2222");
        u.setGender(Gender.FEMALE);

        // W-4 Step 1
        u.setFilingStatus(FilingStatus.MARRIED_FILLING_JOINTLY);
        u.setDependents(2);
        u.setExtraWithHoldings(BigDecimal.ZERO);

        // W-4 Step 2 (способ считает spouse работает)
        u.setMultipleJobsOrSpouseWorks(true);
        u.setTwoJobsCheckBox(true);
        u.setMultipleJobsAdditionalWithholding(BigDecimal.valueOf(50)); // example worksheet result

        // W-4 Step 3 (2 qualifying children)
        u.setDependentsUnder17(2);
        u.setOtherDependents(0);
        u.setTotalDependentsCredit(BigDecimal.valueOf(2 * 2000));

        // W-4 Step 4
        u.setOtherIncome(BigDecimal.ZERO);
        u.setDeductions(BigDecimal.ZERO);

        // W-4 Step 5
        u.setExemptFromWithholding(false);

        // Остальные
        u.setLivesInNYC(true);
        u.setPayFrequency(PayFrequency.WEEKLY);
        u.setEmploymentType(EmploymentType.W2);
        u.setCoverageStartDate(null);
        u.setEnrolledInHealthPlan(false);
        u.setMonthlyHealthPremium(null);

        u.setCompany(company);
        u.setRoles(new ArrayList<>(List.of(userRole)));
    //    WcRiskClass riskClass = wcRiskClassRepository
   //             .orElseThrow();
    //    u.setWcRiskClass(riskClass);
        userRepository.save(u);
        workerScheduleTest.generateScheduleForWorkers(u.getId());
    }

    public void createWorker3ForCompany1(Integer companyId) {
        Company company = companyRepository.findById(companyId).orElseThrow();
        Role userRole = roleRepository.findById(1).orElseThrow();

        User u = new User();
        u.setFirstName("Worker");
        u.setLastName("3");
        u.setEmail("worker3@example.com");
        u.setPhoneNumber("+1-347-000-0003");
        u.setDateOfBirth(LocalDate.of(1992, 3, 3));
        u.setHomeAddress("300 Main St");
        u.setCity("Brooklyn");
        u.setState("NY");
        u.setZipcode("11203");
        u.setBaseHourlyRate(BigDecimal.valueOf(24));
        u.setOvertimeRate(BigDecimal.valueOf(36));
        u.setAdmin(false);
        u.setForeman(false);
        u.setBusinessOwner(false);
        u.setUser(true);
        u.setPassword("password3");
        u.setEnabled(true);
        u.setAccountLocked(false);
        u.setSSN_WORKER("333-33-3333");
        u.setGender(Gender.FEMALE);

        // W-4 Step 1
        u.setFilingStatus(FilingStatus.HEAD_OF_HOUSEHOLD);
        u.setDependents(1);
        u.setExtraWithHoldings(BigDecimal.ZERO);

        // W-4 Step 2
        u.setMultipleJobsOrSpouseWorks(false);
        u.setTwoJobsCheckBox(false);
        u.setMultipleJobsAdditionalWithholding(BigDecimal.ZERO);

        // W-4 Step 3 (1 child)
        u.setDependentsUnder17(1);
        u.setOtherDependents(0);
        u.setTotalDependentsCredit(BigDecimal.valueOf(2000));

        // W-4 Step 4
        u.setOtherIncome(BigDecimal.ZERO);
        u.setDeductions(BigDecimal.ZERO);

        // W-4 Step 5
        u.setExemptFromWithholding(false);

        // Остальные
        u.setLivesInNYC(true);
        u.setPayFrequency(PayFrequency.WEEKLY);
        u.setEmploymentType(EmploymentType.W2);
        u.setCoverageStartDate(null);
        u.setEnrolledInHealthPlan(false);
        u.setMonthlyHealthPremium(null);

        u.setCompany(company);
        u.setRoles(new ArrayList<>(List.of(userRole)));
    //    WcRiskClass riskClass = wcRiskClassRepository
   //             .findById("5190")                   // например, «Electrical Wiring»
   //             .orElseThrow();
 //       u.setWcRiskClass(riskClass);
        userRepository.save(u);
        workerScheduleTest.generateScheduleForWorkers(u.getId());
    }

    public void createWorker4ForCompany1(Integer companyId) {
        Company company = companyRepository.findById(companyId).orElseThrow();
        Role userRole = roleRepository.findById(1).orElseThrow();

        User u = new User();
        u.setFirstName("Worker");
        u.setLastName("4");
        u.setEmail("worker4@example.com");
        u.setPhoneNumber("+1-347-000-0004");
        u.setDateOfBirth(LocalDate.of(1994, 4, 4));
        u.setHomeAddress("400 Main St");
        u.setCity("Brooklyn");
        u.setState("NY");
        u.setZipcode("11204");
        u.setBaseHourlyRate(BigDecimal.valueOf(26));
        u.setOvertimeRate(BigDecimal.valueOf(39));
        u.setAdmin(false);
        u.setForeman(false);
        u.setBusinessOwner(false);
        u.setUser(true);
        u.setPassword("password4");
        u.setEnabled(true);
        u.setAccountLocked(false);
        u.setSSN_WORKER("444-44-4444");
        u.setGender(Gender.MALE);

        // W-4 Step 1
        u.setFilingStatus(FilingStatus.MARRIED_FILLING_SEPARATELY);
        u.setDependents(0);
        u.setExtraWithHoldings(BigDecimal.ZERO);

        // W-4 Step 2
        u.setMultipleJobsOrSpouseWorks(false);
        u.setTwoJobsCheckBox(false);
        u.setMultipleJobsAdditionalWithholding(BigDecimal.ZERO);

        // W-4 Step 3
        u.setDependentsUnder17(0);
        u.setOtherDependents(0);
        u.setTotalDependentsCredit(BigDecimal.ZERO);

        // W-4 Step 4
        u.setOtherIncome(BigDecimal.ZERO);
        u.setDeductions(BigDecimal.ZERO);

        // W-4 Step 5
        u.setExemptFromWithholding(false);

        // Остальные
        u.setLivesInNYC(true);
        u.setPayFrequency(PayFrequency.WEEKLY);
        u.setEmploymentType(EmploymentType.W2);
        u.setCoverageStartDate(null);
        u.setEnrolledInHealthPlan(false);
        u.setMonthlyHealthPremium(null);

        u.setCompany(company);
        u.setRoles(new ArrayList<>(List.of(userRole)));
    //    WcRiskClass riskClass = wcRiskClassRepository
    //            .findById("5190")                   // например, «Electrical Wiring»
   //             .orElseThrow();
    //    u.setWcRiskClass(riskClass);
       userRepository.save(u);
        workerScheduleTest.generateScheduleForWorkers(u.getId());
    }

    public void createWorker5ForCompany1(Integer companyId) {
        Company company = companyRepository.findById(companyId).orElseThrow();
        Role userRole = roleRepository.findById(1).orElseThrow();

        User u = new User();
        u.setFirstName("Worker");
        u.setLastName("5");
        u.setEmail("worker5@example.com");
        u.setPhoneNumber("+1-347-000-0005");
        u.setDateOfBirth(LocalDate.of(1996, 5, 5));
        u.setHomeAddress("500 Main St");
        u.setCity("Brooklyn");
        u.setState("NY");
        u.setZipcode("11205");
        u.setBaseHourlyRate(BigDecimal.valueOf(28));
        u.setOvertimeRate(BigDecimal.valueOf(42));
        u.setAdmin(false);
        u.setForeman(false);
        u.setBusinessOwner(false);
        u.setUser(true);
        u.setPassword("password5");
        u.setEnabled(true);
        u.setAccountLocked(false);
        u.setSSN_WORKER("555-55-5555");
        u.setGender(Gender.FEMALE);

        // W-4 Step 1
        u.setFilingStatus(FilingStatus.SINGLE);
        u.setDependents(0);
        u.setExtraWithHoldings(BigDecimal.ZERO);

        // W-4 Step 2 (multiple jobs self)
        u.setMultipleJobsOrSpouseWorks(true);
        u.setTwoJobsCheckBox(true);
        u.setMultipleJobsAdditionalWithholding(BigDecimal.valueOf(75));

        // W-4 Step 3
        u.setDependentsUnder17(0);
        u.setOtherDependents(0);
        u.setTotalDependentsCredit(BigDecimal.ZERO);

        // W-4 Step 4
        u.setOtherIncome(BigDecimal.ZERO);
        u.setDeductions(BigDecimal.ZERO);

        // W-4 Step 5
        u.setExemptFromWithholding(false);

        // Остальные
        u.setLivesInNYC(true);
        u.setPayFrequency(PayFrequency.WEEKLY);
        u.setEmploymentType(EmploymentType.W2);
        u.setCoverageStartDate(null);
        u.setEnrolledInHealthPlan(false);
        u.setMonthlyHealthPremium(null);

        u.setCompany(company);
        u.setRoles(new ArrayList<>(List.of(userRole)));
    //    WcRiskClass riskClass = wcRiskClassRepository
     //           .findById("5190")                   // например, «Electrical Wiring»
     //           .orElseThrow();
    //    u.setWcRiskClass(riskClass);
        userRepository.save(u);
        workerScheduleTest.generateScheduleForWorkers(u.getId());
    }


    //------------------------------------------------------------------------------

    public void createWorker1ForCompany2(Integer companyId){
        var adminsCompany1 = companyRepository.findById(companyId)
                .orElseThrow();
        var adminRole = roleRepository.findById(1)
                .orElseThrow();
        User adminForCompany1 = new User();
        adminForCompany1.setFirstName("Worker ");
        adminForCompany1.setLastName(" 1");
        adminForCompany1.setMiddleInitial("");
        adminForCompany1.setEmail("mishamaykinghsbr67@gmail.com");
        adminForCompany1.setPhoneNumber("+1-347-828-5790");
        adminForCompany1.setDateOfBirth(LocalDate.of(1995, 8, 8));
        adminForCompany1.setHomeAddress("407 Ocean View Ave #2A");
        adminForCompany1.setCity("Brooklyn");
        adminForCompany1.setState("NY");
        adminForCompany1.setZipcode("11236");
        adminForCompany1.setBaseHourlyRate(BigDecimal.valueOf(25));
        adminForCompany1.setAdmin(false);
        adminForCompany1.setForeman(false);
        adminForCompany1.setBusinessOwner(false);
        adminForCompany1.setUser(true);
        adminForCompany1.setPassword("12345678");
        adminForCompany1.setEnabled(true);
        adminForCompany1.setAccountLocked(false);
        adminForCompany1.setSSN_WORKER("123-12-1234");
        adminForCompany1.setGender(Gender.MALE);
        adminForCompany1.setFilingStatus(FilingStatus.SINGLE);
        adminForCompany1.setDependents(0);
        adminForCompany1.setExtraWithHoldings(BigDecimal.valueOf(0));
        adminForCompany1.setLivesInNYC(true);
        adminForCompany1.setPayFrequency(PayFrequency.BIWEEKLY);
        adminForCompany1.setEmploymentType(EmploymentType.W2);
        adminForCompany1.setCoverageStartDate(null);
        adminForCompany1.setEnrolledInHealthPlan(false);
        adminForCompany1.setMonthlyHealthPremium(null);
        adminForCompany1.setPeriodChargeInsurance(null);
        adminForCompany1.setSickLeavePaid(true);
        adminForCompany1.setHireDate(LocalDate.of(2025, 1,1));
        adminForCompany1.setCompany(adminsCompany1);
        adminForCompany1.setOwnedCompany(null);
        adminForCompany1.setCreatedDate(LocalDateTime.now());
        adminForCompany1.setLastModifiedDate(LocalDateTime.now());
        adminForCompany1.setRoles(new ArrayList<>(List.of(adminRole)));
     //   WcRiskClass riskClass = wcRiskClassRepository
     //           .findById("5190")                   // например, «Electrical Wiring»
     //           .orElseThrow();
    //   adminForCompany1.setWcRiskClass(riskClass);
        userRepository.save(adminForCompany1);
        workerScheduleTest.generateScheduleForWorkers(adminForCompany1.getId());

    }

    public void createWorke2ForCompany2(Integer companyId){
        var adminsCompany1 = companyRepository.findById(companyId)
                .orElseThrow();
        var adminRole = roleRepository.findById(1)
                .orElseThrow();
        User adminForCompany1 = new User();
        adminForCompany1.setFirstName("Worker  ");
        adminForCompany1.setLastName(" 2");
        adminForCompany1.setMiddleInitial("");
        adminForCompany1.setEmail("mishamay712@gmail.com");
        adminForCompany1.setPhoneNumber("+1-347-828-5791");
        adminForCompany1.setDateOfBirth(LocalDate.of(1995, 8, 8));
        adminForCompany1.setHomeAddress("407 Ocean View Ave #4A");
        adminForCompany1.setCity("Brooklyn");
        adminForCompany1.setState("NY");
        adminForCompany1.setZipcode("11237");
        adminForCompany1.setBaseHourlyRate(BigDecimal.valueOf(25));
        adminForCompany1.setAdmin(false);
        adminForCompany1.setForeman(false);
        adminForCompany1.setBusinessOwner(false);
        adminForCompany1.setUser(true);
        adminForCompany1.setPassword("12345678");
        adminForCompany1.setEnabled(true);
        adminForCompany1.setAccountLocked(false);
        adminForCompany1.setSSN_WORKER("123-12-1234");
        adminForCompany1.setGender(Gender.MALE);
        adminForCompany1.setFilingStatus(FilingStatus.MARRIED_FILLING_SEPARATELY);
        adminForCompany1.setDependents(2);
        adminForCompany1.setExtraWithHoldings(BigDecimal.valueOf(0));
        adminForCompany1.setLivesInNYC(true);
        adminForCompany1.setPayFrequency(PayFrequency.BIWEEKLY);
        adminForCompany1.setEmploymentType(EmploymentType.W2);
        adminForCompany1.setCoverageStartDate(null);
        adminForCompany1.setEnrolledInHealthPlan(false);
        adminForCompany1.setMonthlyHealthPremium(null);
        adminForCompany1.setPeriodChargeInsurance(null);
        adminForCompany1.setSickLeavePaid(true);
        adminForCompany1.setHireDate(LocalDate.of(2025, 1,1));
        adminForCompany1.setCompany(adminsCompany1);
        adminForCompany1.setOwnedCompany(null);
        adminForCompany1.setCreatedDate(LocalDateTime.now());
        adminForCompany1.setLastModifiedDate(LocalDateTime.now());
        adminForCompany1.setRoles(new ArrayList<>(List.of(adminRole)));
    //    WcRiskClass riskClass = wcRiskClassRepository
     //           .findById("5190")                   // например, «Electrical Wiring»
    //            .orElseThrow();
    //    adminForCompany1.setWcRiskClass(riskClass);
        userRepository.save(adminForCompany1);
        workerScheduleTest.generateScheduleForWorkers(adminForCompany1.getId());

    }

    public void createWorker3ForCompany2(Integer companyId){
        var adminsCompany1 = companyRepository.findById(companyId)
                .orElseThrow();
        var adminRole = roleRepository.findById(1)
                .orElseThrow();
        User adminForCompany1 = new User();
        adminForCompany1.setFirstName("Worker ");
        adminForCompany1.setLastName(" 3");
        adminForCompany1.setMiddleInitial("");
        adminForCompany1.setEmail("worker18@gmail.com");
        adminForCompany1.setPhoneNumber("+1-347-828-5794");
        adminForCompany1.setDateOfBirth(LocalDate.of(1995, 8, 8));
        adminForCompany1.setHomeAddress("407 Ocean View Ave #6A");
        adminForCompany1.setCity("Brooklyn");
        adminForCompany1.setState("NY");
        adminForCompany1.setZipcode("11237");
        adminForCompany1.setBaseHourlyRate(BigDecimal.valueOf(25));
        adminForCompany1.setAdmin(false);
        adminForCompany1.setForeman(false);
        adminForCompany1.setBusinessOwner(false);
        adminForCompany1.setUser(true);
        adminForCompany1.setPassword("12345678");
        adminForCompany1.setEnabled(true);
        adminForCompany1.setAccountLocked(false);
        adminForCompany1.setSSN_WORKER("123-12-1234");
        adminForCompany1.setGender(Gender.MALE);
        adminForCompany1.setFilingStatus(FilingStatus.HEAD_OF_HOUSEHOLD);
        adminForCompany1.setDependents(1);
        adminForCompany1.setExtraWithHoldings(BigDecimal.valueOf(0));
        adminForCompany1.setLivesInNYC(true);
        adminForCompany1.setPayFrequency(PayFrequency.BIWEEKLY);
        adminForCompany1.setEmploymentType(EmploymentType.W2);
        adminForCompany1.setCoverageStartDate(null);
        adminForCompany1.setEnrolledInHealthPlan(false);
        adminForCompany1.setMonthlyHealthPremium(null);
        adminForCompany1.setPeriodChargeInsurance(null);
        adminForCompany1.setSickLeavePaid(true);
        adminForCompany1.setHireDate(LocalDate.of(2025, 1,1));
        adminForCompany1.setCompany(adminsCompany1);
        adminForCompany1.setOwnedCompany(adminsCompany1);
        adminForCompany1.setCreatedDate(LocalDateTime.now());
        adminForCompany1.setLastModifiedDate(LocalDateTime.now());
        adminForCompany1.setRoles(new ArrayList<>(List.of(adminRole)));
    //    WcRiskClass riskClass = wcRiskClassRepository
    //            .findById("5190")                   // например, «Electrical Wiring»
     //           .orElseThrow();
    //    adminForCompany1.setWcRiskClass(riskClass);
        userRepository.save(adminForCompany1);
        workerScheduleTest.generateScheduleForWorkers(adminForCompany1.getId());

    }

    public void createWorker4ForCompany2(Integer companyId){
        var adminsCompany1 = companyRepository.findById(companyId)
                .orElseThrow();
        var adminRole = roleRepository.findById(1)
                .orElseThrow();
        User adminForCompany1 = new User();
        adminForCompany1.setFirstName("Worker ");
        adminForCompany1.setLastName(" 4");
        adminForCompany1.setMiddleInitial("");
        adminForCompany1.setEmail("worker954@gmail.com");
        adminForCompany1.setPhoneNumber("+1-347-828-5734");
        adminForCompany1.setDateOfBirth(LocalDate.of(1995, 8, 8));
        adminForCompany1.setHomeAddress("407 Ocean View Ave #7A");
        adminForCompany1.setCity("Brooklyn");
        adminForCompany1.setState("NY");
        adminForCompany1.setZipcode("11237");
        adminForCompany1.setBaseHourlyRate(BigDecimal.valueOf(25));
        adminForCompany1.setAdmin(false);
        adminForCompany1.setForeman(false);
        adminForCompany1.setBusinessOwner(false);
        adminForCompany1.setUser(true);
        adminForCompany1.setPassword("12345678");
        adminForCompany1.setEnabled(true);
        adminForCompany1.setAccountLocked(false);
        adminForCompany1.setSSN_WORKER("123-12-1234");
        adminForCompany1.setGender(Gender.MALE);
        adminForCompany1.setFilingStatus(FilingStatus.SINGLE);
        adminForCompany1.setDependents(0);
        adminForCompany1.setExtraWithHoldings(BigDecimal.valueOf(0));
        adminForCompany1.setLivesInNYC(true);
        adminForCompany1.setPayFrequency(PayFrequency.BIWEEKLY);
        adminForCompany1.setEmploymentType(EmploymentType.W2);
        adminForCompany1.setCoverageStartDate(null);
        adminForCompany1.setEnrolledInHealthPlan(false);
        adminForCompany1.setMonthlyHealthPremium(null);
        adminForCompany1.setPeriodChargeInsurance(null);
        adminForCompany1.setSickLeavePaid(true);
        adminForCompany1.setHireDate(LocalDate.of(2025, 1,1));
        adminForCompany1.setCompany(adminsCompany1);
        adminForCompany1.setOwnedCompany(adminsCompany1);
        adminForCompany1.setCreatedDate(LocalDateTime.now());
        adminForCompany1.setLastModifiedDate(LocalDateTime.now());
        adminForCompany1.setRoles(new ArrayList<>(List.of(adminRole)));
   //     WcRiskClass riskClass = wcRiskClassRepository
   //             .findById("5190")                   // например, «Electrical Wiring»
   //             .orElseThrow();
   //     adminForCompany1.setWcRiskClass(riskClass);
        userRepository.save(adminForCompany1);
        workerScheduleTest.generateScheduleForWorkers(adminForCompany1.getId());

    }

    public void createWorker5ForCompany2(Integer companyId){
        var adminsCompany1 = companyRepository.findById(companyId)
                .orElseThrow();
        var adminRole = roleRepository.findById(1)
                .orElseThrow();
        User adminForCompany1 = new User();
        adminForCompany1.setFirstName("Worker ");
        adminForCompany1.setLastName(" 5");
        adminForCompany1.setMiddleInitial("");
        adminForCompany1.setEmail("worker1014@gmail.com");
        adminForCompany1.setPhoneNumber("+1-347-828-5734");
        adminForCompany1.setDateOfBirth(LocalDate.of(1995, 8, 8));
        adminForCompany1.setHomeAddress("407 Ocean View Ave #7A");
        adminForCompany1.setCity("Brooklyn");
        adminForCompany1.setState("NY");
        adminForCompany1.setZipcode("11237");
        adminForCompany1.setBaseHourlyRate(BigDecimal.valueOf(25));
        adminForCompany1.setAdmin(false);
        adminForCompany1.setForeman(false);
        adminForCompany1.setBusinessOwner(false);
        adminForCompany1.setUser(true);
        adminForCompany1.setPassword("12345678");
        adminForCompany1.setEnabled(true);
        adminForCompany1.setAccountLocked(false);
        adminForCompany1.setSSN_WORKER("123-12-1234");
        adminForCompany1.setGender(Gender.MALE);
        adminForCompany1.setFilingStatus(FilingStatus.SINGLE);
        adminForCompany1.setDependents(0);
        adminForCompany1.setExtraWithHoldings(BigDecimal.valueOf(0));
        adminForCompany1.setLivesInNYC(true);
        adminForCompany1.setPayFrequency(PayFrequency.BIWEEKLY);
        adminForCompany1.setEmploymentType(EmploymentType.W2);
        adminForCompany1.setCoverageStartDate(null);
        adminForCompany1.setEnrolledInHealthPlan(false);
        adminForCompany1.setMonthlyHealthPremium(null);
        adminForCompany1.setPeriodChargeInsurance(null);
        adminForCompany1.setSickLeavePaid(true);
        adminForCompany1.setHireDate(LocalDate.of(2025, 1,1));
        adminForCompany1.setCompany(adminsCompany1);
        adminForCompany1.setOwnedCompany(adminsCompany1);
        adminForCompany1.setCreatedDate(LocalDateTime.now());
        adminForCompany1.setLastModifiedDate(LocalDateTime.now());
        adminForCompany1.setRoles(new ArrayList<>(List.of(adminRole)));
 //       WcRiskClass riskClass = wcRiskClassRepository
//                .findById("5190")                   // например, «Electrical Wiring»
 //               .orElseThrow();
 //       adminForCompany1.setWcRiskClass(riskClass);
         userRepository.save(adminForCompany1);
        workerScheduleTest.generateScheduleForWorkers(adminForCompany1.getId());

    }


}
