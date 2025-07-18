package com.zikpak.facecheck.taxesServices.services.wcRiskService.mapper;


import com.zikpak.facecheck.entity.WcRiskClass;
import com.zikpak.facecheck.taxesServices.services.wcRiskService.dto.WcRiskResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WcRiskClassMapper {


    public WcRiskResponse toWcRisk (WcRiskClass wc){
        return WcRiskResponse.builder()
                .code(wc.getCode())
                .description(wc.getDescription())
                .rate(wc.getRate())
                .industryTag(wc.getIndustryTag())
                .effectiveYear(wc.getEffectiveYear())
                .build();
    }

}
