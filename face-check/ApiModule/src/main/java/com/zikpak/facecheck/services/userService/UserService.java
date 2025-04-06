package com.zikpak.facecheck.services.userService;


import com.zikpak.facecheck.domain.UserFinance;
import com.zikpak.facecheck.helperServices.UserServiceImpl;
import com.zikpak.facecheck.helperServices.WorkerPayRollService;
import com.zikpak.facecheck.requestsResponses.worker.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.YearMonth;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService implements UserFinance {

    private final UserServiceImpl userService;
    private final WorkerPayRollService workerPayRollService;


    public void updateEmail( String email, Authentication authentication) {
        userService.updateEmail(email, authentication);
    }


    public void updatePassword( String password, Authentication authentication) {
        userService.updatePassword(password, authentication);
    }



    public void updatePhone( String phone, Authentication authentication) {
        userService.updatePhone(phone, authentication);
    }



    public void updateHomeAddress( String homeAddress, Authentication authentication) {
        userService.updateHomeAddress(homeAddress, authentication);
    }


    public UserFullNameResponse findWorkerFullName(Authentication authentication) {
       return  userService.findWorkerFullName(authentication);
    }


    public UserEmailResponse findWorkerEmail(Authentication authentication) {
       return  userService.findWorkerEmail(authentication);
    }

    public UserPhoneNumberResponse findWorkerPhoneNumber(Authentication authentication) {
           return  userService.findWorkerPhoneNumber(authentication);
    }


    public UserHomeAddressResponse findWorkerHomeAddress(Authentication authentication) {
      return  userService.findWorkerHomeAddress(authentication);
    }

    public UserFullContactInformation findWorkerFullContactInformation(Authentication authentication) {
        return userService.findWorkerFullContactInformation(authentication);
    }


    @Override
    public BigDecimal findWorkerBaseHourRate(Authentication authentication) {
        return workerPayRollService.findWorkerBaseHourRate(authentication);
    }

    @Override
    public double findWorkerWorkedHoursTotalPerWeek(Authentication authentication) {
        return workerPayRollService.findWorkerWorkedHoursTotalPerWeek(authentication);
    }


    @Override
    public BigDecimal findWorkerSalaryPerWeekGross(Authentication authentication) {
        return workerPayRollService.countSalaryPerWeekGross(authentication);
    }

    @Override
    public BigDecimal findWorkerSalaryForSpecialWeekGross(Authentication authentication, int weeksAgo) {
        return workerPayRollService.findWorkerSalaryForSpecialWeekGross(authentication, weeksAgo);
    }

    @Override
    public BigDecimal findWorkerSalaryPerMonthGross(Authentication authentication) {
        return workerPayRollService.countSalaryPerMontGross(authentication);
    }

    @Override
    public BigDecimal findWorkerSalaryForSpecialMonthGross(Authentication authentication, YearMonth yearMonth) {
        return workerPayRollService.findWorkerSalaryForSpecialMonthGross(authentication, yearMonth);
    }

    @Override
    public BigDecimal findWorkerSalaryPerYearGross(Authentication authentication) {
        return workerPayRollService.countSalaryPerYearGross(authentication);
    }

    @Override
    public BigDecimal findWorkerSalaryForSpecialYearGross(Authentication authentication, int year) {
        return workerPayRollService.findWorkerSalaryForSpecialYearGross(authentication, year);
    }

    @Override
    public BigDecimal findWorkerSalaryPerWeekNet(Authentication authentication) {
        return workerPayRollService.countSalaryPerWeekNet(authentication);
    }

    @Override
    public BigDecimal findWorkerSalaryPerMonthNet(Authentication authentication) {
        return workerPayRollService.countSalaryPerMontNet(authentication);
    }

    @Override
    public BigDecimal findWorkerSalaryPerYearNet(Authentication authentication) {
        return workerPayRollService.countSalaryPerYearNet(authentication);
    }

    @Override
    public BigDecimal findWorkerSalaryForSpecialWeekNet(Authentication authentication, int weekAgo) {
        return workerPayRollService.findWorkerSalaryForSpecialWeekNet(authentication, weekAgo);
    }

    @Override
    public BigDecimal findWorkerSalaryForSpecialMonthNet(Authentication authentication, YearMonth yearMonth) {
        return workerPayRollService.findWorkerSalaryForSpecialMonthNet(authentication, yearMonth);
    }

    @Override
    public BigDecimal findWorkerSalaryForSpecialYearNet(Authentication authentication, int years) {
        return workerPayRollService.findWorkerSalaryForSpecialYearNet(authentication, years);
    }

    @Override
    public BigDecimal findWorkerTotalPayedTaxesAmountForMonth(Authentication authentication) {
            return workerPayRollService.findWorkerTotalPayedTaxesAmountForMonth(authentication);
    }

    @Override
    public BigDecimal findWorkerTotalPayedTaxesAmountForYear(Authentication authentication) {
            return workerPayRollService.findWorkerTotalPayedTaxesAmountForYear(authentication);
    }

    @Override
    public BigDecimal findWorkerTotalPayedTaxesAmountForWeek(Authentication authentication) {
        return workerPayRollService.findWorkerTotalPayedTaxesAmountForWeek(authentication);
    }

    @Override
    public BigDecimal findWorkerTotalPayedTaxesAmountForSpecialWeek(Authentication authentication, int weekAgo) {
        return workerPayRollService.findWorkerTotalPayedTaxesAmountForSpecialWeek(authentication, weekAgo);
    }

    @Override
    public BigDecimal findWorkerTotalPayedTaxesAmountForSpecialMonth(Authentication authentication, YearMonth yearMonth) {
        return workerPayRollService.findWorkerTotalPayedTaxesAmountForSpecialMonth(authentication, yearMonth);
    }

    @Override
    public BigDecimal findWorkerTotalPayedTaxesAmountForSpecialYear(Authentication authentication, int years) {
        return workerPayRollService.findWorkerTotalPayedTaxesAmountForSpecialYear(authentication, years);
    }



}
