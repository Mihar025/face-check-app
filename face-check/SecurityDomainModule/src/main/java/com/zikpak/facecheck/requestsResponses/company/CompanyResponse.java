package com.zikpak.facecheck.requestsResponses.company;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CompanyResponse {

    private Integer companyId;

    private String companyName;

    private String companyAddress;

    private String companyPhone;

    private String companyEmail;

    private Integer workersQuantity;



}
