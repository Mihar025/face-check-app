package com.zikpak.facecheck.taxesServices.services.LocationTrackingGoogle;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.Instant;

@Data
public class LocationUpdateDto {

    @NotNull
    private Double latitude;

    @NotNull
    private Double longitude;

    @NotNull
    private Instant timestamp;

    private Float accuracy;
    private Float speed;
    private Float bearing;
    private Double altitude;
    private String provider;
    private Integer batteryLevel;


}
