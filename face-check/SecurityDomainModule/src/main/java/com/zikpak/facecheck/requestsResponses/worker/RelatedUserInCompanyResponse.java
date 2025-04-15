package com.zikpak.facecheck.requestsResponses.worker;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Data
@Getter
@Setter
@Builder
public class RelatedUserInCompanyResponse {

    private Integer workerId;

    private Integer companyId;

    private String firstName;

    private String lastName;

    private String email;

    private BigDecimal baseHourlyRate;

    private boolean enabled;


}
