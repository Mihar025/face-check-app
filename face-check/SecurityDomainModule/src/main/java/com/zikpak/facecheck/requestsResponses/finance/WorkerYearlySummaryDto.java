package com.zikpak.facecheck.requestsResponses.finance;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class WorkerYearlySummaryDto {

    private BigDecimal grossPayTotal;
    private BigDecimal netPayTotal;
    private BigDecimal federalWithholdingTotal;
    private BigDecimal socialSecurityEmployeeTotal;
    private BigDecimal medicareTotal;
    private BigDecimal nyStateWithholdingTotal;
    private BigDecimal nyLocalWithholdingTotal;
    private BigDecimal nyDisabilityWithholdingTotal;
    private BigDecimal nyPaidFamilyLeaveTotal;
    private BigDecimal totalAllTaxes;
    private BigDecimal socialSecurityTips;
    private BigDecimal allocatedTips;
    private BigDecimal dependentCareBenefits;
    private BigDecimal nonqualifiedPlans;
    private BigDecimal retirement401kContribution;
    private BigDecimal healthInsuranceCost;
    private Boolean hasRetirementPlan;

}
