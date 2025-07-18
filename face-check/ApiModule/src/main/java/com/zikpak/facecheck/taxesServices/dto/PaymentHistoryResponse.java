package com.zikpak.facecheck.taxesServices.dto;


import com.zikpak.facecheck.entity.PaymentType;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class PaymentHistoryResponse {

    private Integer paymentHistoryIrsId;

    private Integer companyId;

    private BigDecimal amount;
    private BigDecimal totalPayedAmountForQuarter;

    private LocalDate paymentDate;

    private Integer quarter;

    private Integer year;

    private PaymentType paymentType; // Например: "regular", "overpayment from 941-X", "prior quarter credit"

    private String notes;

    private LocalDateTime createDate;


}
