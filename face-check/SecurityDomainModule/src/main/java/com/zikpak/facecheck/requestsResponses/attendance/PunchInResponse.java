package com.zikpak.facecheck.requestsResponses.attendance;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PunchInResponse {


    private Integer workerId;
    private Integer workSiteId;
    private String workSiteName;
    private String workerFullName;
    private LocalDateTime checkInTime;
    private String formattedCheckInTime;
    private String checkInPhotoUrl;
    private Double checkInLatitude;
    private Double checkInLongitude;
    private String workSiteAddress;
    private Boolean isSuccessful;
    private String message;

}
