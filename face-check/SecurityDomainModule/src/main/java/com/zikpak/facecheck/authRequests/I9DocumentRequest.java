package com.zikpak.facecheck.authRequests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Builder
public class I9DocumentRequest {

    @NotBlank
    private String documentTitle;
    @NotBlank
    private String issuingAuthority;
    @NotBlank
    private String documentNumber;
    @PastOrPresent
    private LocalDate expirationDate;
}
