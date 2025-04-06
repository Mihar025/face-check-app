package com.zikpak.facecheck.requestsResponses.workSite;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WorkSiteRequest {

    @NotNull(message = "Work site name cannot be null")
    @NotEmpty(message = "Work site name is mandatory!")
    private String workSiteName;

    @NotNull(message = "Work site Address cannot be null")
    @NotEmpty(message = "Work site Address is mandatory!")
    private String address;

    @NotNull(message = "Work site Latitude cannot be null")
    private Double latitude;

    @NotNull(message = "Work site Longitude cannot be null")
    private Double longitude;

    @NotNull(message = "Work site Allowed Radius cannot be null")
    private Double allowedRadius;

    @NotNull(message = "Work Day Start cannot be null")
    private LocalTime workDayStart;

    @NotNull(message = "Work Day End cannot be null")
    private LocalTime workDayEnd;


}
