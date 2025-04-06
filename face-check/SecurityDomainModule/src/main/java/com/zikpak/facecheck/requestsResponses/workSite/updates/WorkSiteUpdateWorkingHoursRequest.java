package com.zikpak.facecheck.requestsResponses.workSite.updates;

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
public class WorkSiteUpdateWorkingHoursRequest {
    @NotNull(message = "New Start time cannot be null")
    private LocalTime newStart;
    @NotNull(message = "New End time cannot be null")
    private LocalTime newEnd;
}
