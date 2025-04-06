package com.zikpak.facecheck.requestsResponses.workSite.updates;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WorkSiteUpdateLocationRequest {
    @NotNull(message = "New Latitude cannot be null")
    private Double newLatitude;
    @NotNull(message = "New Longitude cannot be null")
    private Double newLongitude;
    @NotNull(message = "New Radius cannot be null")
    private Double newRadius;

}
