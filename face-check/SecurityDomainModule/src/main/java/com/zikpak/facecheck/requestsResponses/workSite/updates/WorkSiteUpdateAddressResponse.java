package com.zikpak.facecheck.requestsResponses.workSite.updates;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WorkSiteUpdateAddressResponse {

    private Integer worksiteId;
    private String workSiteAddress;

}
