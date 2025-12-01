package com.zikpak.facecheck.controllers;

import com.stripe.exception.StripeException;
import com.zikpak.facecheck.services.stripe.BillingResponse;
import com.zikpak.facecheck.services.stripe.BillingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("billing")
@RequiredArgsConstructor
public class BillingController {

    private final BillingService billingService;

    @PostMapping("/activate/{companyId}")
    public BillingResponse createSubscription(@PathVariable ("companyId") Integer companyId) throws StripeException {
        return billingService.activateBilling(companyId);
    }

    @PostMapping("/cancel/{companyId}")
    public ResponseEntity<?> cancelSubscription(@PathVariable Integer companyId) throws StripeException {
        billingService.cancelSubscription(companyId);
        return ResponseEntity.ok("Subscription will be canceled at period end");
    }

    @PostMapping("/update-seats/{companyId}")
    public ResponseEntity<?> updateSeats(@PathVariable Integer companyId) throws StripeException {
        billingService.updateSeats(companyId);
        return ResponseEntity.ok("Subscription seats updated successfully");
    }


}
