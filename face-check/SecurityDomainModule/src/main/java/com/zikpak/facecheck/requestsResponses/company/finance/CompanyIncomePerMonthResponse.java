package com.zikpak.facecheck.requestsResponses.company.finance;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Data
@Builder
public class CompanyIncomePerMonthResponse {

    private Integer companyId;

    private BigDecimal companyIncomePerMonth;

}
