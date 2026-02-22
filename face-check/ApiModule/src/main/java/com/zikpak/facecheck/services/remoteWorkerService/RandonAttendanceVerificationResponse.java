package com.zikpak.facecheck.services.remoteWorkerService;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RandonAttendanceVerificationResponse {

    private Integer workerId;

    private Integer verificationId;

    private Integer workSiteId;

    private String workSiteName;

    private String workerFullName;

    private LocalDateTime randomAttendanceVerificationTime;

    private String formattedRandomAttendanceVerificationTime;

    private String randomAttendanceVerificationPhotoUrl;

    private Double randomAttendanceVerificationLatitude;

    private Double randomAttendanceVerificationLongitude;

    private String randomAttendanceVerificationLocation;

    private String workSiteAddress;

    private Boolean isSuccessful;

    private Boolean isMissed;

    private String isMissedMessage;

    private String message;

}
