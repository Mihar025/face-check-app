package com.zikpak.facecheck.taxesServices.customReportsForCompanys.taxSummary;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class PaymentStatus {
    private BigDecimal totalPaid;
    private BigDecimal remainingLiability;
    private boolean isCompliant;
}