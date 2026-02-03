package com.zikpak.facecheck.requestsResponses.company;


import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class CompanyResponse {

    private Integer companyId;

    private String companyName;

    private String companyAddress;

    private String companyPhone;

    private String companyEmail;

    private Integer workersQuantity;

    private String subscriptionStatus;

    private BigDecimal monthlySubscription;

    private BigDecimal pricePerEmployee;


}
