package com.zikpak.facecheck.controllers;

import com.stripe.exception.StripeException;
import com.zikpak.facecheck.requestsResponses.stripeDTOs.CreateSubscriptionRequest;
import com.zikpak.facecheck.services.stripe.BillingResponse;
import com.zikpak.facecheck.services.stripe.BillingService;
import org.springframework.web.bind.annotation.RequestBody;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("billing")
@RequiredArgsConstructor
public class BillingController {

    private final BillingService billingService;

    @PostMapping("/activate/{companyId}")
    public BillingResponse createSubscription(@PathVariable ("companyId") Integer companyId, Authentication authentication) throws StripeException {
        return billingService.activateBilling(companyId, authentication);
    }

    @PostMapping("/cancel/{companyId}")
    public ResponseEntity<?> cancelSubscription(@PathVariable Integer companyId, Authentication authentication) throws StripeException {
        billingService.cancelSubscription(companyId, authentication);
        return ResponseEntity.ok("Subscription will be canceled at period end");
    }

    @PostMapping("/update-seats/{companyId}")
    public ResponseEntity<?> updateSeats(@PathVariable Integer companyId, Authentication authentication) throws StripeException {
        billingService.updateSeats(companyId, authentication);
        return ResponseEntity.ok("Subscription seats updated successfully");
    }

    @PostMapping("/activate/subscription")
    public BillingResponse createSubscriptionV2(Authentication authentication, @RequestBody CreateSubscriptionRequest request) throws StripeException {
        System.out.println("AUTH name=" + authentication.getName());
        System.out.println("AUTH authorities=" + authentication.getAuthorities());
        return billingService.activateBillingV2(authentication, request);
    }





}
