package com.zikpak.facecheck.requestsResponses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkerPhotosResponse {
    private Integer workerId;
    private String workerName;
    private String date;
    private String checkInPhotoUrl;
    private String checkOutPhotoUrl;
    private String checkInTime;
    private String checkOutTime;
}