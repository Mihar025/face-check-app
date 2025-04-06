package com.zikpak.facecheck.requestsResponses.worker;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserFullContactInformation {

    private String fullName;

    private String phoneNumber;

    private String email;

    private String address;

    private String photoFileName;

    private String photoUrl;




}
