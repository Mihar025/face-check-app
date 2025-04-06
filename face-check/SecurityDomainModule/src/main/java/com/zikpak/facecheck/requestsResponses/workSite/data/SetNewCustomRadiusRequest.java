package com.zikpak.facecheck.requestsResponses.workSite.data;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class SetNewCustomRadiusRequest {
    @Positive(message = "Radius value must be greater than 0!")
    @NotNull(message = "Custom radius value cannot be null!")
    private Double customRadius;

}
