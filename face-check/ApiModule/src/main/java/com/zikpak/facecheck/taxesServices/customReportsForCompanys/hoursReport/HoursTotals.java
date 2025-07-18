package com.zikpak.facecheck.taxesServices.customReportsForCompanys.hoursReport;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class HoursTotals {
    private BigDecimal totalRegular;
    private BigDecimal totalOvertime;
    private BigDecimal totalHours;
    private BigDecimal averagePerEmployee;
    private BigDecimal overtimePercentage;
}
