package com.zikpak.facecheck.requestsResponses.worker;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DailyFinanceInfo {
    private LocalDate date;
    private Double hoursWorked;
    private BigDecimal grossPay;
}