package com.zikpak.facecheck.controllers;

import com.zikpak.facecheck.annotation.RateLimit;
import com.zikpak.facecheck.requestsResponses.PageResponse;
import com.zikpak.facecheck.services.contactSalesService.ContactSalesFormRequest;
import com.zikpak.facecheck.services.contactSalesService.ContactSalesFormResponse;
import com.zikpak.facecheck.services.contactSalesService.ContactSalesService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("sales")
@RequiredArgsConstructor
public class ContactSalesController {

    private final ContactSalesService contactSalesService;

    @GetMapping
    public PageResponse<ContactSalesFormResponse> getAllContactForms(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return contactSalesService.findAllContactForms(page, size);
    }

    @GetMapping("/{id}")
    public ContactSalesFormResponse getContactFormById(@PathVariable Integer id) {
        return contactSalesService.findContactFormById(id);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteContactForm(@PathVariable Integer id) {
        contactSalesService.deleteContactFormById(id);
        return ResponseEntity.noContent().build();
    }


    @PostMapping
    @RateLimit(requests = 3, perSeconds = 60)
    public ContactSalesFormResponse createContactForm(
            @RequestBody @Valid ContactSalesFormRequest request
    ) {
        return contactSalesService.saveContactForm(request);
    }




}
