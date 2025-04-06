package com.zikpak.facecheck.requestsResponses.company.finance;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CompanyIncomePerMonthRequest {

    private BigDecimal companyIncomePerMonth;


}
