package com.zikpak.facecheck.requestsResponses.attendance;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PunchOutRequest {

    private Integer workSiteId;
    private String photoBase64;
    private Double latitude;
    private Double longitude;

}
