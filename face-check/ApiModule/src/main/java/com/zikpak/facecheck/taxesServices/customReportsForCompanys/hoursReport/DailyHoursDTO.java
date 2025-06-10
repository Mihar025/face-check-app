package com.zikpak.facecheck.taxesServices.customReportsForCompanys.hoursReport;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class DailyHoursDTO {

    private LocalDate date;
    private BigDecimal totalHours;
    private Integer employeesWorked;
    private BigDecimal averageHoursPerEmployee;
    private BigDecimal totalOvertimeHours;
}
