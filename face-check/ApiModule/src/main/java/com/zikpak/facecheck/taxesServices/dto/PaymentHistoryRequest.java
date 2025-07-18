package com.zikpak.facecheck.taxesServices.dto;

import com.zikpak.facecheck.entity.PaymentType;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentHistoryRequest {

    @NotNull(message = "Field cannot be empty!")
    private BigDecimal amount;

    @NotNull(message = "Field cannot be empty!")
    private Integer quarter;

    @NotNull(message = "Field cannot be empty!")
    private LocalDate paymentDate;

    @NotNull(message = "Field cannot be empty!")
    private Integer year;

    @NotNull(message = "Fields Cannot be Empty!")
    private PaymentType paymentType;

    @NotNull(message = "Field cannot be empty!")
    @NotEmpty(message = "Field cannot be empty!")
    private String notes;



}
