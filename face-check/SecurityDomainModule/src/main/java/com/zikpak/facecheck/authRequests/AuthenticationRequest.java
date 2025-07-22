package com.zikpak.facecheck.authRequests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationRequest {

    @Email(regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$", message = "Email is not formatted well!")
   @NotBlank(message = "Email is required!")
    private String email;
    @NotBlank(message = "Password is required!")
    @NotEmpty(message = "Password is required")
    private String password;



}
