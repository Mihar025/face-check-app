package com.zikpak.facecheck.requestsResponses.workSite.data;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Data;

@Data
//@Builder
public class SetNewCustomRadiusRequest {
    @Positive(message = "Radius value must be greater than 0!")
    @NotNull(message = "Custom radius value cannot be null!")
    private Double customRadius;

}
