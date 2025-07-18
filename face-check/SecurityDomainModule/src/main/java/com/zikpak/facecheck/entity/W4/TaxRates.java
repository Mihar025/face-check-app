package com.zikpak.facecheck.entity.W4;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Getter
@Setter
public class TaxRates {

    public static final BigDecimal SOCIAL_SECURITY_RATE = new BigDecimal("0.062");
    public static final BigDecimal SOCIAL_SECURITY_WAGE_LIMIT = new BigDecimal("176100"); // 2025

    public static final BigDecimal MEDICARE_RATE = new BigDecimal("0.0145");
    public static final BigDecimal ADDITIONAL_MEDICARE_RATE = new BigDecimal("0.009"); // если доход выше 200,000
    public static final BigDecimal ADDL_MEDICARE_THRESHOLD = new BigDecimal("200000");

    public static final BigDecimal NY_STATE_TAX_APPROX = new BigDecimal("0.06");

    public static final BigDecimal NYC_LOCAL_TAX_APPROX = new BigDecimal("0.035");

    public static final BigDecimal NY_PFL_RATE = new BigDecimal("0.00388");
    public static final BigDecimal NY_PFL_ANNUAL_MAX = new BigDecimal("354.53");

    public static final BigDecimal NY_DISABILITY_WEEKLY_MAX = new BigDecimal("0.60");

    public static final BigDecimal FEDERAL_STANDARD_DEDUCTION_SINGLE = new BigDecimal("15000");
    public static final BigDecimal FEDERAL_STANDARD_DEDUCTION_MARRIED = new BigDecimal("30000");
    public static final BigDecimal FEDERAL_STANDARD_DEDUCTION_HEAD_OF_HOUSEHOLD = new BigDecimal("22500");

    public static final BigDecimal NY_STANDARD_DEDUCTION_SINGLE = new BigDecimal("8000");
    public static final BigDecimal NY_STANDARD_DEDUCTION_MARRIED = new BigDecimal("16050");
    public static final BigDecimal NY_STANDARD_DEDUCTION_HEAD_OF_HOUSEHOLD = new BigDecimal("11200");

    public static final BigDecimal FEDERAL_CHILD_TAX_CREDIT = new BigDecimal("2000");
    public static final BigDecimal FEDERAL_OTHER_DEPENDENT_CREDIT = new BigDecimal("500");





    public static BigDecimal round(BigDecimal value) {
        return value.setScale(2, RoundingMode.HALF_UP);
    }
}

