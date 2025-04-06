package com.zikpak.facecheck.requestsResponses.settings;


import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class SettingsPasswordRequest {
    @NotEmpty(message = "This field cannot be empty!")
    private String currentPassword;

    @NotEmpty(message = "This field cannot be empty!")
    private String newPassword;

    @NotEmpty(message = "This field cannot be empty!")
    private String confirmPassword;

}
