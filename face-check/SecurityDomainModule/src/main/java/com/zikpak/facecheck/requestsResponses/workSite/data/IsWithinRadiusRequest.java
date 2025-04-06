package com.zikpak.facecheck.requestsResponses.workSite.data;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class IsWithinRadiusRequest {

    @NotNull(message = "Latitude is mandatory")
    private double latitude;

    @NotNull(message = "Longitude is mandatory")
    private double longitude;


}
