package com.zikpak.facecheck.taxesServices.services.wcRiskService.dto;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class WcCodeRequest {
    @NotNull(message = "This field is required")
    private String code;

    @NotNull(message = "This field is required")
    private String description;

    @NotNull(message = "This field is required")
    private BigDecimal rate;

    @NotNull(message = "This field is required")
    private BigDecimal emr;

    @NotNull(message = "This field is required")
    private String industryTag;

    @NotNull(message = "This field is required")
    private Integer effectiveYear;
}
