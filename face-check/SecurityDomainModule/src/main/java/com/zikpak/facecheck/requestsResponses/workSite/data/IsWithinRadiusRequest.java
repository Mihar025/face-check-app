package com.zikpak.facecheck.requestsResponses.workSite.data;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class IsWithinRadiusRequest {

    @NotNull(message = "Latitude is mandatory")
    private double latitude;

    @NotNull(message = "Longitude is mandatory")
    private double longitude;


}
