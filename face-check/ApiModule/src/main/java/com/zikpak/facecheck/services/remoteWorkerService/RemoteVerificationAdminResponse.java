package com.zikpak.facecheck.services.remoteWorkerService;


import com.zikpak.facecheck.entity.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RemoteVerificationAdminResponse {

    private Integer verificationId;

    // Worker info
    private Integer workerId;
    private String workerFirstName;
    private String workerLastName;
    private String workerEmail;
    private String workerPhone;

    // Company info
    private Integer companyId;
    private String companyName;

    // Verification data
    private Status status;
    private Boolean isSuccessful;
    private Boolean isMissed;
    private String isMissedMessage;
    private String message;

    // Photo
    private String photoUrl;

    // Location
    private Double latitude;
    private Double longitude;
    private String locationAddress;

    // Times
    private LocalDateTime verificationTime;
    private String formattedVerificationTime;
    private LocalDate createdAt;
}