package com.zikpak.facecheck.controllers;

import com.zikpak.facecheck.requestsResponses.UserCompanyNameInformation;
import com.zikpak.facecheck.requestsResponses.WorkerCompanyIdByAuthenticationResponse;
import com.zikpak.facecheck.requestsResponses.WorkerPersonalInformationResponse;
import com.zikpak.facecheck.requestsResponses.worker.*;
import com.zikpak.facecheck.services.userService.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.time.YearMonth;

@RestController
@RequiredArgsConstructor
@RequestMapping("user")
public class UserServiceController {

    private final UserService userService;

    // User Information Update Operations
    @PutMapping("/email/{email}")
    public ResponseEntity<Void> updateEmail(@PathVariable String email, Authentication authentication){
        userService.updateEmail(email, authentication);
        return ResponseEntity.ok().build();
    }

    //working
    @PutMapping("/password/{password}")
    public ResponseEntity<Void> updatePassword(@PathVariable String password, Authentication authentication) {
        userService.updatePassword(password, authentication);
        return ResponseEntity.ok().build();
    }

    //working
    @PutMapping("/phone/{phone}")
    public ResponseEntity<Void> updatePhone(@PathVariable String phone, Authentication authentication) {
        userService.updatePhone(phone, authentication);
        return ResponseEntity.ok().build();
    }

    //working
    @PutMapping("/address/{homeAddress}")
    public ResponseEntity<Void> updateHomeAddress(@PathVariable String homeAddress, Authentication authentication) {
        userService.updateHomeAddress(homeAddress, authentication);
        return ResponseEntity.ok().build();
    }



    // User Information Retrieval
    //working
    @GetMapping("/full-name")
    public ResponseEntity<UserFullNameResponse> findWorkerFullName(Authentication authentication) {
        return ResponseEntity.ok(userService.findWorkerFullName(authentication));
    }
    //working
    @GetMapping("/email")
    public ResponseEntity<UserEmailResponse> findWorkerEmail(Authentication authentication) {
        return ResponseEntity.ok(userService.findWorkerEmail(authentication));
    }
    //working
    @GetMapping("/phone")
    public ResponseEntity<UserPhoneNumberResponse> findWorkerPhoneNumber(Authentication authentication) {
        return ResponseEntity.ok(userService.findWorkerPhoneNumber(authentication));
    }
    //working
    @GetMapping("/address")
    public ResponseEntity<UserHomeAddressResponse> findWorkerHomeAddress(Authentication authentication) {
        return ResponseEntity.ok(userService.findWorkerHomeAddress(authentication));
    }
    //working
    @GetMapping("/contact-info")
    public ResponseEntity<UserFullContactInformation> findWorkerFullContactInformation(Authentication authentication) {
        return ResponseEntity.ok(userService.findWorkerFullContactInformation(authentication));
    }

     // Salary Information - Base Rate
    @GetMapping("/base-rate")
    public ResponseEntity<BigDecimal> findWorkerBaseHourRate(Authentication authentication) {
        return ResponseEntity.ok(userService.findWorkerBaseHourRate(authentication));
    }

    @GetMapping("/total-hours-perWeek")
    public ResponseEntity<Double> findTotalWorkedHoursPerWeek(Authentication authentication) {
        return ResponseEntity.ok(userService.findWorkerWorkedHoursTotalPerWeek(authentication));
    }

    // Gross Salary Endpoints
    @GetMapping("/salary/week/gross")
    public ResponseEntity<BigDecimal> findWorkerSalaryPerWeekGross(Authentication authentication) {
        return ResponseEntity.ok(userService.findWorkerSalaryPerWeekGross(authentication));
    }

    @GetMapping("/salary/week/gross/special")
    public ResponseEntity<BigDecimal> findWorkerSalaryForSpecialWeekGross(
            Authentication authentication,
            @RequestParam int weeksAgo) {
        return ResponseEntity.ok(userService.findWorkerSalaryForSpecialWeekGross(authentication, weeksAgo));
    }

    @GetMapping("/salary/month/gross")
    public ResponseEntity<BigDecimal> findWorkerSalaryPerMonthGross(Authentication authentication) {
        return ResponseEntity.ok(userService.findWorkerSalaryPerMonthGross(authentication));
    }

    @GetMapping("/salary/month/gross/special")
    public ResponseEntity<BigDecimal> findWorkerSalaryForSpecialMonthGross(
            Authentication authentication,
            @RequestParam YearMonth yearMonth) {
        return ResponseEntity.ok(userService.findWorkerSalaryForSpecialMonthGross(authentication, yearMonth));
    }

    @GetMapping("/salary/year/gross")
    public ResponseEntity<BigDecimal> findWorkerSalaryPerYearGross(Authentication authentication) {
        return ResponseEntity.ok(userService.findWorkerSalaryPerYearGross(authentication));
    }

    @GetMapping("/salary/year/gross/special")
    public ResponseEntity<BigDecimal> findWorkerSalaryForSpecialYearGross(
            Authentication authentication,
            @RequestParam int year) {
        return ResponseEntity.ok(userService.findWorkerSalaryForSpecialYearGross(authentication, year));
    }

    // Net Salary Endpoints
    @GetMapping("/salary/week/net")
    public ResponseEntity<BigDecimal> findWorkerSalaryPerWeekNet(Authentication authentication) {
        return ResponseEntity.ok(userService.findWorkerSalaryPerWeekNet(authentication));
    }

    @GetMapping("/salary/month/net")
    public ResponseEntity<BigDecimal> findWorkerSalaryPerMonthNet(Authentication authentication) {
        return ResponseEntity.ok(userService.findWorkerSalaryPerMonthNet(authentication));
    }

    @GetMapping("/salary/year/net")
    public ResponseEntity<BigDecimal> findWorkerSalaryPerYearNet(Authentication authentication) {
        return ResponseEntity.ok(userService.findWorkerSalaryPerYearNet(authentication));
    }

    @GetMapping("/salary/week/net/special")
    public ResponseEntity<BigDecimal> findWorkerSalaryForSpecialWeekNet(
            Authentication authentication,
            @RequestParam int weekAgo) {
        return ResponseEntity.ok(userService.findWorkerSalaryForSpecialWeekNet(authentication, weekAgo));
    }

    @GetMapping("/salary/month/net/special")
    public ResponseEntity<BigDecimal> findWorkerSalaryForSpecialMonthNet(
            Authentication authentication,
            @RequestParam YearMonth yearMonth) {
        return ResponseEntity.ok(userService.findWorkerSalaryForSpecialMonthNet(authentication, yearMonth));
    }

    @GetMapping("/salary/year/net/special")
    public ResponseEntity<BigDecimal> findWorkerSalaryForSpecialYearNet(
            Authentication authentication,
            @RequestParam int years) {
        return ResponseEntity.ok(userService.findWorkerSalaryForSpecialYearNet(authentication, years));
    }

    // Tax Information Endpoints
    @GetMapping("/taxes/month")
    public ResponseEntity<BigDecimal> findWorkerTotalPayedTaxesAmountForMonth(Authentication authentication) {
        return ResponseEntity.ok(userService.findWorkerTotalPayedTaxesAmountForMonth(authentication));
    }

    @GetMapping("/taxes/year")
    public ResponseEntity<BigDecimal> findWorkerTotalPayedTaxesAmountForYear(Authentication authentication) {
        return ResponseEntity.ok(userService.findWorkerTotalPayedTaxesAmountForYear(authentication));
    }

    @GetMapping("/taxes/week")
    public ResponseEntity<BigDecimal> findWorkerTotalPayedTaxesAmountForWeek(Authentication authentication) {
        return ResponseEntity.ok(userService.findWorkerTotalPayedTaxesAmountForWeek(authentication));
    }

    @GetMapping("/taxes/week/special")
    public ResponseEntity<BigDecimal> findWorkerTotalPayedTaxesAmountForSpecialWeek(
            Authentication authentication,
            @RequestParam int weekAgo) {
        return ResponseEntity.ok(userService.findWorkerTotalPayedTaxesAmountForSpecialWeek(authentication, weekAgo));
    }

    @GetMapping("/taxes/month/special")
    public ResponseEntity<BigDecimal> findWorkerTotalPayedTaxesAmountForSpecialMonth(
            Authentication authentication,
            @RequestParam YearMonth yearMonth) {
        return ResponseEntity.ok(userService.findWorkerTotalPayedTaxesAmountForSpecialMonth(authentication, yearMonth));
    }

    @GetMapping("/taxes/year/special")
    public ResponseEntity<BigDecimal> findWorkerTotalPayedTaxesAmountForSpecialYear(
            Authentication authentication,
            @RequestParam int years) {
        return ResponseEntity.ok(userService.findWorkerTotalPayedTaxesAmountForSpecialYear(authentication, years));
    }

    @GetMapping("/company")
    public ResponseEntity<UserCompanyNameInformation> findWorkerCompanyName(Authentication authentication) {
        return ResponseEntity.ok(userService.findWorkersCompanyNameInformation(authentication));
    }

    @GetMapping("/company/id")
    public ResponseEntity<WorkerCompanyIdByAuthenticationResponse> findWorkerCompanyIdByAuthentication(Authentication authentication) {
        return ResponseEntity.ok(userService.findWorkersCompanyByAuthentication(authentication));
    }

    @GetMapping("/personal-information/{employeeId}")
    public ResponseEntity<WorkerPersonalInformationResponse> getWorkerPersonalInformation(
            Authentication authentication,
            @PathVariable Integer employeeId) {
        WorkerPersonalInformationResponse response = userService.findWorkerPersonalInformation(authentication, employeeId);
        return ResponseEntity.ok(response);
    }







}
