package com.zikpak.facecheck.domain;

import org.springframework.security.core.Authentication;

import java.math.BigDecimal;
import java.time.YearMonth;

public interface UserFinanceNetGrossTaxCalculator {
    BigDecimal findWorkerBaseHourRate(Authentication authentication);
    double findWorkerWorkedHoursTotalPerWeek(Authentication authentication);

    BigDecimal countSalaryPerWeekGross(Authentication authentication);
    BigDecimal countSalaryPerMontGross(Authentication authentication);
    BigDecimal countSalaryPerYearGross(Authentication authentication);

    BigDecimal countSalaryPerWeekNet(Authentication authentication);
    BigDecimal countSalaryPerMontNet(Authentication authentication);
    BigDecimal countSalaryPerYearNet(Authentication authentication);


    BigDecimal findWorkerTotalPayedTaxesAmountForMonth(Authentication authentication);

    BigDecimal findWorkerTotalPayedTaxesAmountForYear(Authentication authentication);

    BigDecimal findWorkerTotalPayedTaxesAmountForWeek(Authentication authentication);


    BigDecimal findWorkerSalaryForSpecialWeekGross(Authentication authentication, int weekAgo);
    BigDecimal findWorkerSalaryForSpecialMonthGross(Authentication authentication,  YearMonth yearMonth);
    BigDecimal findWorkerSalaryForSpecialYearGross(Authentication authentication, int years);


    BigDecimal findWorkerSalaryForSpecialWeekNet(Authentication authentication, int weekAgo );
    BigDecimal findWorkerSalaryForSpecialMonthNet(Authentication authentication, YearMonth yearMonth);
    BigDecimal findWorkerSalaryForSpecialYearNet(Authentication authentication, int years);

    BigDecimal findWorkerTotalPayedTaxesAmountForSpecialWeek(Authentication authentication, int weekAgo);
    BigDecimal findWorkerTotalPayedTaxesAmountForSpecialMonth(Authentication authentication, YearMonth yearMonth);
    BigDecimal findWorkerTotalPayedTaxesAmountForSpecialYear(Authentication authentication, int years);
}
