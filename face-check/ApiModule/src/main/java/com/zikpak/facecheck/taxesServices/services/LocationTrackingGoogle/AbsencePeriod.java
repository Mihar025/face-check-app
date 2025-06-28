package com.zikpak.facecheck.taxesServices.services.LocationTrackingGoogle;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Duration;
import java.time.Instant;

@Data
@AllArgsConstructor
public class AbsencePeriod {
    private Instant start;
    private Instant end;

    public Duration getDuration() {
        return Duration.between(start, end);
    }
}