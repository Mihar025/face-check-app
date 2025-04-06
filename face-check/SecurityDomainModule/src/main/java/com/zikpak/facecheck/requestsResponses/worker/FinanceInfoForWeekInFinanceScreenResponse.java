package com.zikpak.facecheck.requestsResponses.worker;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FinanceInfoForWeekInFinanceScreenResponse {
    private Double totalHoursWorked;
    private BigDecimal totalGrossPay;
    private BigDecimal totalNetPay;
    private LocalDate periodStart;
    private LocalDate periodEnd;
    private List<DailyFinanceInfo> dailyInfo;
}