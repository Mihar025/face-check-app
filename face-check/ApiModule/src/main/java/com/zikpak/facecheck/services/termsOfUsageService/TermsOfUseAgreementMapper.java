package com.zikpak.facecheck.services.termsOfUsageService;


import com.zikpak.facecheck.entity.TermsOfUseAgreement;
import org.springframework.stereotype.Service;

@Service
public class TermsOfUseAgreementMapper {
    public TermsOfUseRequestDataResponse toTermsOfUse(TermsOfUseAgreement savedTermsAndPolicy) {

        return TermsOfUseRequestDataResponse.builder()
                .id(savedTermsAndPolicy.getId())
                .event(savedTermsAndPolicy.getEvent())
                .userId(savedTermsAndPolicy.getUserId())
                .timeStamp(savedTermsAndPolicy.getTimeStamp())
                .termsVersion(savedTermsAndPolicy.getTermsVersion())
                .privacyVersion(savedTermsAndPolicy.getPrivacyVersion())
                .ip(savedTermsAndPolicy.getIp())
                .device(savedTermsAndPolicy.getDevice())
                .osVersion(savedTermsAndPolicy.getOsVersion())
                .build();
    }
}
