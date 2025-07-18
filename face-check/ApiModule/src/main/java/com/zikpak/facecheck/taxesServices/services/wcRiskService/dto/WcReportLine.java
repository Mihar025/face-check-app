package com.zikpak.facecheck.taxesServices.services.wcRiskService.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

@AllArgsConstructor
@RequiredArgsConstructor
@Data
@Builder
public class WcReportLine {
    private final String code;
    private final String description;
    private final BigDecimal totalWages;
    private final BigDecimal rate;
    private final BigDecimal emr;
    private BigDecimal basePremium;
    private final BigDecimal totalContribution;
}
