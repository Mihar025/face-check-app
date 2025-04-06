package com.zikpak.facecheck.requestsResponses.worker;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdatePunchInForWorkerResponse {
    private Integer workerId;
    private String current_work_site;
    private String workerFullName;

    private LocalDateTime newPunchInTime;


}
