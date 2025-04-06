package com.zikpak.facecheck.authRequests;


import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
public class PaymentRequest {

        @NotNull(message = "Hour rate is required")
        @DecimalMin(value = "0.0", message = "Hour rate must be positive")
        private BigDecimal hourRate;

        @NotNull(message = "Overtime rate is required")
        @DecimalMin(value = "0.0", message = "Overtime rate must be positive")
        private BigDecimal overtimeRate;

}
