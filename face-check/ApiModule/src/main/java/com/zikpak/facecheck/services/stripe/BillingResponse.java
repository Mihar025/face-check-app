package com.zikpak.facecheck.services.stripe;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BillingResponse {

    private String checkoutURL;

}
