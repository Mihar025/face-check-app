package com.zikpak.facecheck.services.stripe;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BillingResponse {
    @JsonProperty("checkoutUrl")
    private String checkoutURL;
}
