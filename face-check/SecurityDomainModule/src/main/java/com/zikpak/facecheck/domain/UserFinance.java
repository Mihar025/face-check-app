package com.zikpak.facecheck.domain;


import org.springframework.security.core.Authentication;

import java.math.BigDecimal;
import java.time.YearMonth;

public interface UserFinance {

    BigDecimal findWorkerBaseHourRate(Authentication authentication);
    double findWorkerWorkedHoursTotalPerWeek(Authentication authentication);

    //Gross it's all salary without included taxes
    BigDecimal findWorkerSalaryPerWeekGross(Authentication authentication);
    BigDecimal findWorkerSalaryForSpecialWeekGross(Authentication authentication, int weeksAgo);
    BigDecimal findWorkerSalaryPerMonthGross(Authentication authentication);
    BigDecimal findWorkerSalaryForSpecialMonthGross(Authentication authentication, YearMonth yearMonth);
    BigDecimal findWorkerSalaryPerYearGross(Authentication authentication);
    BigDecimal findWorkerSalaryForSpecialYearGross(Authentication authentication, int year);



    // Net its salary with counted taxes
    BigDecimal findWorkerSalaryPerWeekNet(Authentication authentication);
    BigDecimal findWorkerSalaryPerMonthNet(Authentication authentication);
    BigDecimal findWorkerSalaryPerYearNet(Authentication authentication);

    BigDecimal findWorkerSalaryForSpecialWeekNet(Authentication authentication, int weekAgo );
    BigDecimal findWorkerSalaryForSpecialMonthNet(Authentication authentication, YearMonth yearMonth);
    BigDecimal findWorkerSalaryForSpecialYearNet(Authentication authentication, int years);


    // Just paid taxes!
    BigDecimal findWorkerTotalPayedTaxesAmountForMonth(Authentication authentication);
    BigDecimal findWorkerTotalPayedTaxesAmountForYear(Authentication authentication);
    BigDecimal findWorkerTotalPayedTaxesAmountForWeek(Authentication authentication);


    BigDecimal findWorkerTotalPayedTaxesAmountForSpecialWeek(Authentication authentication, int weekAgo);
    BigDecimal findWorkerTotalPayedTaxesAmountForSpecialMonth(Authentication authentication, YearMonth yearMonth);
    BigDecimal findWorkerTotalPayedTaxesAmountForSpecialYear(Authentication authentication, int years);




}
