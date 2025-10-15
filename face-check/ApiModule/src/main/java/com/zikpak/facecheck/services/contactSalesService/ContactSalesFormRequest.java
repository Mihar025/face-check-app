package com.zikpak.facecheck.services.contactSalesService;


import jakarta.persistence.Column;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ContactSalesFormRequest {

    @NotNull(message = "First Name is required!")
    private String firstName;

    @NotNull(message = "First Name is required!")
    private String lastName;

    @NotNull(message = "First Name is required!")
    private String phoneNumber;

}
