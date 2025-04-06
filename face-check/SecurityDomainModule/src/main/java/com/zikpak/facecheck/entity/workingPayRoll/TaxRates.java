package com.zikpak.facecheck.entity.workingPayRoll;

import java.math.BigDecimal;

public enum TaxRates {

    MEDICARE(new BigDecimal("0.0145")) ,
    SOCIAL_SECURITY(new BigDecimal("0.062")),
    FEDERAL_TAX(new BigDecimal("0.22")),
    NY_STATE_TAX(new BigDecimal("0.06")),
    NY_LOCAL_TAX(new BigDecimal("0.035")),
    NY_DISABILITY_TAX(new BigDecimal("0.005")),
    NY_PAID_FAMILY_LEAVE(new BigDecimal("0.00511"))
    ;

    private final BigDecimal taxRate;

    TaxRates(BigDecimal taxRate) {
        this.taxRate = taxRate;
    }

    public BigDecimal getTaxRate() {
        return taxRate;
    }
}
