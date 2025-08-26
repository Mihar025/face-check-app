package com.zikpak.facecheck.services.contactSalesService;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ContactSalesFormResponse {


    private Integer id;

    private String firstName ;

    private String lastName ;

    private String phoneNumber;


}
