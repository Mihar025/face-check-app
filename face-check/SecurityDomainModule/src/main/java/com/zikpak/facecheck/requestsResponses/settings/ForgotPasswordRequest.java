package com.zikpak.facecheck.requestsResponses.settings;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ForgotPasswordRequest {
    @Email(regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$", message = "Email is not formatted well!")
    @NotBlank(message = "Email is required!")
    @Pattern(regexp = "^[^;'\"]*$", message = "Email contains invalid characters")
    private String email;

    @NotBlank
    private String newPassword;

    @NotBlank
    private String confirmNewPassword;

    private String code;

}
