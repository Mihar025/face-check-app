package com.zikpak.facecheck.requestsResponses.finance;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class PayStubResponse {

    private BigDecimal grossPay;

    private BigDecimal socialSecurity;
    private BigDecimal medicare;
    private BigDecimal federalTax;
    private BigDecimal stateTax;
    private BigDecimal nycTax;
    private BigDecimal pfl;
    private BigDecimal disability;

    private BigDecimal totalDeductions;
    private BigDecimal netPay;
}