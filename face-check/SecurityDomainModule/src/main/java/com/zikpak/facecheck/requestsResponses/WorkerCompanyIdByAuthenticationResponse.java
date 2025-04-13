package com.zikpak.facecheck.requestsResponses;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WorkerCompanyIdByAuthenticationResponse {
    private Integer companyId;
}
