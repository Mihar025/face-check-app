package com.zikpak.facecheck.requestsResponses.attendance;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
@Getter
@AllArgsConstructor
@Builder
public class DailyEarningResponse {


    private LocalDate date;
    private double netPay;


}
