package com.zikpak.facecheck.requestsResponses.workSite.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SetNewCustomRadiusResponse {

    private Integer workSiteId;

    private Double customRadius;

}
