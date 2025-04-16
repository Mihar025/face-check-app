package com.zikpak.facecheck.requestsResponses;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class WorkerPersonalInformationResponse {
    private Integer workerId;
    private Integer companyId;
    private String firstName;
    private String lastName;
    private String email;
    private String companyName;
    private String phoneNumber;
    private String address;
    private BigDecimal baseHourlyRate;
    private String role;
}
