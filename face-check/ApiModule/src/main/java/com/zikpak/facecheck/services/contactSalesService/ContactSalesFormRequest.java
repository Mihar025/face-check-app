package com.zikpak.facecheck.services.contactSalesService;


import jakarta.persistence.Column;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ContactSalesFormRequest {

    private String firstName;

    private String lastName;

    private String phoneNumber ;

}
