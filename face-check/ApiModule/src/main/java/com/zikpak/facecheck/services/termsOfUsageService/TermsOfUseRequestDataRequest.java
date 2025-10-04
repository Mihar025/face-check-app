package com.zikpak.facecheck.services.termsOfUsageService;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class TermsOfUseRequestDataRequest {
    private Integer id;

    private String event;

    private Integer userId;

    private String termsVersion;

    private String privacyVersion;

    private String ip;

    private String device;

    private String osVersion;



}
