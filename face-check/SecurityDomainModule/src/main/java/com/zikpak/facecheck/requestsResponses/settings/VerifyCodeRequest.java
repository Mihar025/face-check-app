package com.zikpak.facecheck.requestsResponses.settings;


import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VerifyCodeRequest {

    @Email(regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$", message = "Email is not formatted well!")
    @NotBlank(message = "Email is required!")
    @Pattern(regexp = "^[^;'\"]*$", message = "Email contains invalid characters")
    private String email;

    @NotBlank
    @Size(min = 6, max = 6)
    private String code;


}
