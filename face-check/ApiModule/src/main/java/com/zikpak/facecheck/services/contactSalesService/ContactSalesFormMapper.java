package com.zikpak.facecheck.services.contactSalesService;


import com.zikpak.facecheck.entity.ContactSalesForm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ContactSalesFormMapper {


    public ContactSalesFormResponse toContactFormResponse(ContactSalesForm contactSalesForm) {
        return  ContactSalesFormResponse.builder()
                .id(contactSalesForm.getId())
                .firstName(contactSalesForm.getFirstName())
                .lastName(contactSalesForm.getLastName())
                .phoneNumber(contactSalesForm.getPhoneNumber())
                .build();


    }
}
