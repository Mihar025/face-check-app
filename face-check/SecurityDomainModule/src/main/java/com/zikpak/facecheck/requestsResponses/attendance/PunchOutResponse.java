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
public class PunchOutResponse {

    private Integer workerId;
    private Integer workSiteId;
    private String workSiteName;
    private String workerFullName;

    private LocalDateTime checkInTime;
    private String formattedCheckInTime;

    private LocalDateTime checkOutTime;
    private String formattedCheckOutTime;
    private String checkOutPhotoUrl;
    private Double checkOutLatitude;
    private Double checkOutLongitude;

    private Double hoursWorked;
    private Double overtimeHours;

    private String workSiteAddress;
    private Boolean isSuccessful;
    private String message;
}
