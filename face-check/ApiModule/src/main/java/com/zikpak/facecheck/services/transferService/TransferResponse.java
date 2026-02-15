package com.zikpak.facecheck.services.transferService;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class TransferResponse {

    private Integer workerId;
    private Integer workSiteId;
    private String workSiteName;
    private String workerFullName;
    private LocalDateTime transferTime;
    private String formattedTransferTime;
    private String transferPhotoUrl;
    private Double transferLatitude;
    private Double transferLongitude;
    private String transferLocation;
    private String workSiteAddress;
    private Boolean isSuccessful;
    private String message;



}
