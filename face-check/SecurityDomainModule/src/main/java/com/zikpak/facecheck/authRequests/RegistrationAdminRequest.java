package com.zikpak.facecheck.authRequests;

import com.zikpak.facecheck.entity.Gender;
import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Builder
public class RegistrationAdminRequest {
    @NotBlank(message = "Firstname cannot be empty or blank")
    private String firstName;
    @NotBlank(message = "Lastname cannot be empty or blank")
    private String lastName;
    @NotBlank(message = "Home Address cannot be empty or blank")
    private String homeAddress;

    private LocalDate dateOfBirth;

    @NotNull(message = "Gender is required")
    private Gender gender;

    @NotBlank(message = "Phonenumber is required!")
    private String phoneNumber;

    private BigDecimal SSN_WORKER;

    @Email(regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$", message = "Email is not formatted well!")
    @NotBlank(message = "Email is required!")
    @Pattern(regexp = "^[^;'\"]*$", message = "Email contains invalid characters")
    private String email;

    @NotBlank(message = "Password is mandatory!")
    @Size(min = 6, message = "Password should be minimum 4 characters")
    private String password;
}
