package com.zikpak.facecheck.taxesServices.services.LocationTrackingGoogle;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LocationRecordDto {
    private Integer id;
    private Integer userId;
    private String userFirstName;
    private String userLastName;
    private double latitude;
    private double longitude;
    private Instant timestamp;
    private Double accuracy;
    private Double speed;
    private Double bearing;
    private Double altitude;
    private Integer batteryLevel;
    private Double distanceFromPrevious;
}