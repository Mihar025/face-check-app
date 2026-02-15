package com.zikpak.facecheck.requestsResponses;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompanyUpdatingRequest {

    @NotNull(message = "This field is required, and cannot be empty!")
    @NotEmpty(message = "This field is required, and cannot be empty!")
    private String companyName;
    @NotNull(message = "This field is required, and cannot be empty!")
    @NotEmpty(message = "This field is required, and cannot be empty!")
    private String companyAddress;
    @NotNull(message = "This field is required, and cannot be empty!")
    @NotEmpty(message = "This field is required, and cannot be empty!")
    private String companyPhone;
    @NotNull(message = "This field is required, and cannot be empty!")
    @NotEmpty(message = "This field is required, and cannot be empty!")
    private String companyEmail;

  //  @NotNull(message = "This field is required, and cannot be empty!")
   // private Integer workersQuantity;
}
