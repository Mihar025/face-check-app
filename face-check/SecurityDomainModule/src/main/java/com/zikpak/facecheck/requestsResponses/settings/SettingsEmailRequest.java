package com.zikpak.facecheck.requestsResponses.settings;


import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SettingsEmailRequest {

    @NotEmpty(message = "This field cannot be empty!")
    private String currentEmail;
    @NotEmpty(message = "This field cannot be empty!")

    private String newEmail;
    @NotEmpty(message = "This field cannot be empty!")

    private String confirmNewEmail;



}

