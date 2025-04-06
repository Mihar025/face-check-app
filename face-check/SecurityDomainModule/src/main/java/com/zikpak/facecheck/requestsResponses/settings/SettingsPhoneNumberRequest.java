package com.zikpak.facecheck.requestsResponses.settings;


import lombok.Data;

@Data
public class SettingsPhoneNumberRequest {

    private String currentPhoneNumber;

    private String newPhoneNumber;

    private String confirmNewPhoneNumber;


}
