package com.zikpak.facecheck.requestsResponses.attendance;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
@Getter
@AllArgsConstructor
public class DailyEarningResponse {


    private LocalDate date;
    private double netPay;


}
