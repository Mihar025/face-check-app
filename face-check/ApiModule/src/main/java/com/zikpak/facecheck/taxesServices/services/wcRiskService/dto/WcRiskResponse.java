package com.zikpak.facecheck.taxesServices.services.wcRiskService.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class WcRiskResponse {
    private String code;

    private String description;

    private BigDecimal rate;

    private BigDecimal emr;

    private String industryTag;

    private Integer effectiveYear;
}
