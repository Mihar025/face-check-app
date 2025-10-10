package com.zikpak.facecheck.requestsResponses.admin;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
public class WorksiteWorkerResponse {

    private Integer workerId;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String workSiteAddress;
    private LocalDateTime punchIn;




}
