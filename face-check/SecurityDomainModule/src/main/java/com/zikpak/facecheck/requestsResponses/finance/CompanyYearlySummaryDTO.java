package com.zikpak.facecheck.requestsResponses.finance;


import jdk.jfr.DataAmount;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class CompanyYearlySummaryDTO {

    private BigDecimal totalWages;
    private BigDecimal totalFederalIncomeTax;
    private BigDecimal totalSocialSecurityWages;
    private BigDecimal totalSocialSecurityTax;
    private BigDecimal totalMedicareWages;
    private BigDecimal totalMedicareTax;
    private Integer totalEmployees;
    private Integer year;
    private BigDecimal totalEmployerTaxes;

}
