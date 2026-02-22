package com.zikpak.facecheck.services.remoteWorkerService;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RemoteVerificationStatsResponse {


    private long completedToday;
    private long missedToday;
    private long pendingToday;
    private long totalToday;
    private double complianceRate;


}
