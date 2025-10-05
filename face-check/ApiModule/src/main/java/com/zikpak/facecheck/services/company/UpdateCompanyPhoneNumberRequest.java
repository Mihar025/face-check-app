package com.zikpak.facecheck.services.company;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCompanyPhoneNumberRequest {

    @NotNull(message = "This field cannot be null")
    private String phoneNumber;

}
