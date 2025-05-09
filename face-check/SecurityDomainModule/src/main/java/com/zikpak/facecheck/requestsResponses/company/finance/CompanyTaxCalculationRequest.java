package com.zikpak.facecheck.requestsResponses.company.finance;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CompanyTaxCalculationRequest {

    @NotNull
    private Integer year;

    @NotNull
    private Integer month;

    }
