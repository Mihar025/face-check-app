package com.zikpak.facecheck.taxesServices.dto;

import lombok.*;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Form941Data {

    private int employeeCount;
    private BigDecimal totalGross;
    private BigDecimal totalFederalWithholding;
    private BigDecimal ssTaxableWages;               // для 5a
    private BigDecimal ssTaxableTips;                // для 5b
    private BigDecimal medicareTaxableWages;         // для 5c
    private BigDecimal additionalMedicareTaxableWages; // для 5d

    // Additional fields needed for XML e-file
    private String employerEIN;
    private String companyName;
    private String companyAddress;
    private String companyCity;
    private String companyState;
    private String companyZipCode;

    // Payment/deposit information
    private BigDecimal totalDeposits;

    // Signature information
    private String signerName;
    private String signerTitle;
    private String signerPhone;

    // Monthly liability breakdown (for monthly depositors)
    private BigDecimal month1Liability;
    private BigDecimal month2Liability;
    private BigDecimal month3Liability;

}
