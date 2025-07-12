package com.zikpak.facecheck.authRequests;

import com.zikpak.facecheck.entity.CompanyPaymentPosition;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

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

    @NotNull(message = "This field is required!!!")
    private String employerEIN;

    @NotNull(message = "This field is required!!!")
    private String specialTwoCharConditionCodeForMTA305;


    @NotNull(message = "This field is required!!!")
    private String companyStateIdNumber;

    @NotNull(message = "This field is required!!!")
    private CompanyPaymentPosition companyPaymentPosition;

    @NotNull(message = "SUTA Rate is required! ")
    private BigDecimal socialSecurityTaxForCompany; // SUTA RATE;

    @NotNull
    private BigDecimal experienceModRate;


    @NotNull(message = "WC Policy Number is required")
    private String wcPolicyNumber;

    @NotNull(message = "WC Insurance Carrier is required")
    private String wcInsuranceCarrier;

    @NotNull(message = "This field is required!")
    private String fundingBankName;
    @NotNull(message = "This field is required!")
    private String fundingRoutingNumber;
    @NotNull(message = "This field is required!")
    private String fundingAccountNumber;
    @NotNull(message = "This field is required!")
    private String returnMailingAddress;
    @NotNull(message = "This field is required!")
    private String defaultMemo;
    @NotNull(message = "This field is required!")
    private String signatureName;
    @NotNull(message = "This field is required!")
    private String signatureTitle;


}
