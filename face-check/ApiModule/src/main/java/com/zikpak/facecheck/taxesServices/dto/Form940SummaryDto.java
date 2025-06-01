package com.zikpak.facecheck.taxesServices.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class Form940SummaryDto {

    private String employerName;
    private String employerEin;
    private String employerAddress;
    private int year;
    private int totalEmployees;
    private BigDecimal totalFutaWages;
    private BigDecimal totalFutaTax;



}
