package com.zikpak.facecheck.taxesServices.services.LocationTrackingGoogle;

import lombok.Builder;
import lombok.Data;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class WorkPresenceStats {
    private LocalDate date;
    private long totalLocationPoints;
    private long pointsAtWorksite;
    private Duration timeAtWork;
    private List<AbsencePeriod> absencePeriods;
}



