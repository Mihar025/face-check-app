package com.zikpak.facecheck.requestsResponses.worker;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserEmailResponse {

    private String email;

}
