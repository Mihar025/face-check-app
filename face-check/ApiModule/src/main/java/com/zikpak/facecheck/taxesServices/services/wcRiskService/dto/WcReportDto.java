package com.zikpak.facecheck.taxesServices.services.wcRiskService.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@RequiredArgsConstructor
@Builder
public class WcReportDto {
    private final String companyName;
    private final LocalDate periodStart;
    private final LocalDate periodEnd;
    private final List<WcReportLine> lines;
    private final BigDecimal grandTotal;
    private final BigDecimal emr;
    private final String policyNumber;
}
