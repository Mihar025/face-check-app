package com.zikpak.facecheck.taxesServices.services.LocationTrackingGoogle;

import com.fasterxml.jackson.annotation.JsonFormat;
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
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    private Instant timestamp;

    private Double accuracy;
    private Double speed;
    private Double bearing;
    private Double altitude;
    private String provider;
    private Integer batteryLevel;


}
