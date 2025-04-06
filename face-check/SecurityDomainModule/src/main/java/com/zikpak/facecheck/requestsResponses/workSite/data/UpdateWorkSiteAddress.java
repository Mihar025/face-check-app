package com.zikpak.facecheck.requestsResponses.workSite.data;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateWorkSiteAddress {


    @NotNull(message = "Address is mandatory!")
    private String address;

}
