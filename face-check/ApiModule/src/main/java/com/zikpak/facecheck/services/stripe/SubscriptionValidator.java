package com.zikpak.facecheck.services.stripe;

import com.zikpak.facecheck.entity.Company;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class SubscriptionValidator {

    public void validate(Company company) {
        if (company.getSubscriptionStatus() == null) {
            throw new RuntimeException("Subscription is not active");
        }

        if (!company.getSubscriptionStatus().equals("active")) {
            throw new RuntimeException("Subscription is not active");
        }
        if (company.getSubscriptionCurrentPeriodEnd()
                .isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Subscription expired");
        }
    }
}
