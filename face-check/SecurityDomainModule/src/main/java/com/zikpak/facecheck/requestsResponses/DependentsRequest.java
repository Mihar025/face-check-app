package com.zikpak.facecheck.requestsResponses;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class DependentsRequest {

    @NotBlank
    String firstName;
    @NotBlank String lastName;
    @NotNull
    LocalDate birthDate;
}
