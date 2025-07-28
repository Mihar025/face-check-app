package com.zikpak.facecheck.requestsResponses.company.finance;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CompanyIncomePerMonthRequest {
    @NotNull(message = "Compaany income per month cannot be null")
    private BigDecimal companyIncomePerMonth;


}
