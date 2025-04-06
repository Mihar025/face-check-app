package com.zikpak.facecheck.requestsResponses;


import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
@Builder
public class CompanyUpdatingResponse {

    private Integer companyId;

    private String companyName;

    private String companyAddress;

    private String companyPhone;

    private String companyEmail;

    private Integer workersQuantity;

}
