package com.zikpak.facecheck.requestsResponses.workSite.data;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateNameRequest {
    @NotNull(message = "Work Site name is mandatory")
    private String name;


}
