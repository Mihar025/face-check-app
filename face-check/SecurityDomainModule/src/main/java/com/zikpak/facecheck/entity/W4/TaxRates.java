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

    // === FEDERAL ===
    public static final BigDecimal SOCIAL_SECURITY_RATE = new BigDecimal("0.062");
    public static final BigDecimal SOCIAL_SECURITY_WAGE_LIMIT = new BigDecimal("176100"); // 2025

    public static final BigDecimal MEDICARE_RATE = new BigDecimal("0.0145");
    public static final BigDecimal ADDITIONAL_MEDICARE_RATE = new BigDecimal("0.009"); // если доход выше 200,000

    // === NEW YORK STATE TAX (упрощённо) ===
    public static final BigDecimal NY_STATE_TAX_APPROX = new BigDecimal("0.06");

    // === NEW YORK CITY LOCAL TAX (упрощённо) ===
    public static final BigDecimal NYC_LOCAL_TAX_APPROX = new BigDecimal("0.035");

    // === NY PAID FAMILY LEAVE (PFL) ===
    public static final BigDecimal NY_PFL_RATE = new BigDecimal("0.00455");
    public static final BigDecimal NY_PFL_ANNUAL_MAX = new BigDecimal("399.43");

    // === NY DISABILITY INSURANCE ===
    public static final BigDecimal NY_DISABILITY_WEEKLY_MAX = new BigDecimal("0.60");

    // === FEDERAL STANDARD DEDUCTION (для простого моделирования) ===
    public static final BigDecimal FEDERAL_STANDARD_DEDUCTION_SINGLE = new BigDecimal("14600");
    public static final BigDecimal FEDERAL_STANDARD_DEDUCTION_MARRIED = new BigDecimal("29200");
    public static final BigDecimal FEDERAL_CHILD_TAX_CREDIT = new BigDecimal("2000");

    // Округление для точных расчётов
    public static BigDecimal round(BigDecimal value) {
        return value.setScale(2, RoundingMode.HALF_UP);
    }
}

