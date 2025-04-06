package com.zikpak.facecheck.requestsResponses.workSite;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WorkerCurrentlyWorkingInWorkSite {

        private Integer workerId;
        private Integer workSiteId;

        private LocalDateTime punchedIn;

        private String workerFullName;
        private String workerPhoneNumber;

        private String workSiteName;
        private String workSiteAddress;




}
