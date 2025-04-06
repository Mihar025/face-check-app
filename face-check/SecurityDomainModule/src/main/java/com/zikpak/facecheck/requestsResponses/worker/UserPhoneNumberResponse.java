package com.zikpak.facecheck.requestsResponses.worker;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserPhoneNumberResponse {
    private String phoneNumber;
}
