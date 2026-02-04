package com.zikpak.facecheck.requestsResponses.stripeDTOs;


import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateSubscriptionRequest {

        @NotNull
        @JsonProperty("companyId")
        private Integer companyId;

        @NotNull
        private BigDecimal pricePerEmployee;

        @NotNull
        private BigDecimal monthlySubscription;

}
