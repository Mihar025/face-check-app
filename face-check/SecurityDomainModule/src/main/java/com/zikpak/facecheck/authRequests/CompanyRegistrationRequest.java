package com.zikpak.facecheck.authRequests;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CompanyRegistrationRequest {

    @NotNull(message = "Company name field is required")
    @NotEmpty(message = "Company name cannot be empty! Please provide company information")
    private String companyName;

    @NotNull(message = "Company address field is required")
    @NotEmpty(message = "Company address cannot be empty! Please provide company address ")
    private String companyAddress;

    @NotNull(message = "Company phonenumber is required")
    @NotEmpty(message = "Company phonenumber is required!")
    private String companyPhone;

    @NotNull(message = "Company Email is required!")
    @NotEmpty(message = "Company email cannot be empty")
    private String companyEmail;

    @NotNull(message = "Company city is required")
    private String companyCity;

    @NotNull(message = "Company State is required")
    private String companyState;

    @NotNull(message = "Company ZipCode is required")
    private String companyZipCode;

    @NotNull(message = "This field maximal required!!!")
    private String employerEIN; // Обязательно уникальный EIN для каждой компании

}
