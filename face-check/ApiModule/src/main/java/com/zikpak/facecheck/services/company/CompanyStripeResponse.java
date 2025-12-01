package com.zikpak.facecheck.services.company;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CompanyStripeResponse {
    private Integer companyId;
    private Integer workersQuantity;
    private String subscriptionStatus;
}

