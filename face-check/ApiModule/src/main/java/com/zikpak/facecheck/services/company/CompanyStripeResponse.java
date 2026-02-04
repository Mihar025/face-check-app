package com.zikpak.facecheck.services.company;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CompanyStripeResponse {
    private Integer companyId;
    private Integer workersQuantity;
    private String subscriptionStatus;

    private BigDecimal monthlySubscription;
    private BigDecimal pricePerEmployee;
    private String stripeBasePriceId;
    private String stripeSeatsPriceId;
    private LocalDateTime subscriptionCurrentPeriodEnd;
}

