package com.zikpak.facecheck.requestsResponses.finance;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PayStubResponse {

    private BigDecimal grossPay;

    private BigDecimal socialSecurity;
    //PERSONAL PAID INSURANCE WHICH EMPLOYER OFFER!
    private BigDecimal healthDeduction;
    private BigDecimal medicare;
    private BigDecimal federalTax;
    private BigDecimal stateTax;
    private BigDecimal nycTax;
    private BigDecimal pfl;
    private BigDecimal disability;

    private BigDecimal totalDeductions;
    private BigDecimal netPay;
}