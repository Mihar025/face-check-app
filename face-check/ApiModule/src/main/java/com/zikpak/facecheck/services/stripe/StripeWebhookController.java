package com.zikpak.facecheck.services.stripe;


import com.stripe.model.Event;
import com.stripe.model.Subscription;
import com.stripe.net.Webhook;
import com.zikpak.facecheck.annotation.RateLimit;
import com.zikpak.facecheck.repository.CompanyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.ZoneId;

@RestController
@RequestMapping("billing/webhook")
@RequiredArgsConstructor
public class StripeWebhookController {

    @Value("${stripe.webhook-secret}")
    private String webhookSecret;

    private final CompanyRepository companyRepository;

    @PostMapping
    @RateLimit(requests = 100, perSeconds = 60)
    public ResponseEntity<String> handle(@RequestBody String payload,
                                         @RequestHeader("Stripe-Signature") String sigHeader) {

        Event event;

        try {
            event = Webhook.constructEvent(payload, sigHeader, webhookSecret);
        } catch (Exception e) {
            return ResponseEntity.status(400).body("Invalid signature");
        }

        switch (event.getType()) {

            case "customer.subscription.created":
            case "customer.subscription.updated":
                handleSubscription(event);
                break;

            case "customer.subscription.deleted":
                handleSubscriptionDeleted(event);
                break;
        }

        return ResponseEntity.ok("success");
    }

    private void handleSubscription(Event event) {

        Subscription subscription = (Subscription) event.getDataObjectDeserializer()
                .getObject()
                .orElse(null);

        if(subscription == null){
            return;
        }

        var company = companyRepository.findByStripeCustomerId(subscription.getCustomer());
        if (company == null) return;

        company.setStripeSubscriptionId(subscription.getId());
        company.setSubscriptionStatus(subscription.getStatus());
        company.setStripeSubscriptionItemId(subscription.getItems().getData().get(0).getId());
        company.setSubscriptionCurrentPeriodEnd(
                Instant.ofEpochSecond(subscription.getCurrentPeriodEnd())
                        .atZone(ZoneId.systemDefault())
                        .toLocalDateTime()
        );

        companyRepository.save(company);
    }

    private void handleSubscriptionDeleted(Event event) {

        Subscription subscription = (Subscription) event.getDataObjectDeserializer()
                .getObject()
                .orElse(null);

        if(subscription == null){
            return;
        }


        var company = companyRepository.findByStripeSubscriptionId(subscription.getId());
        if (company == null) return;

        company.setSubscriptionStatus("canceled");
        companyRepository.save(company);

    }


}
