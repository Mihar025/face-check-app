package com.zikpak.facecheck.services.stripe;

import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.Price;
import com.stripe.model.Subscription;
import com.stripe.param.CustomerCreateParams;
import com.stripe.param.PriceCreateParams;
import com.stripe.param.SubscriptionUpdateParams;
import com.stripe.param.checkout.SessionCreateParams;
import com.zikpak.facecheck.entity.User;
import com.zikpak.facecheck.repository.CompanyRepository;
import com.zikpak.facecheck.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import com.stripe.model.checkout.Session;

@Service
@RequiredArgsConstructor
public class BillingService {

    private final CompanyRepository companyRepository;

    @Value( "${stripe.success-url}")
    private  String successUrl;
    @Value( "${stripe.cancel-url}")
    private String cancelUrl;




    public BillingResponse activateBilling(Integer companyId, Authentication authentication) throws StripeException {
        User user = (User) authentication.getPrincipal();
        validateCompanyAccess(user, companyId);

        var company = companyRepository.findById(companyId)
                .orElseThrow(() -> new EntityNotFoundException("Company with ID: " + companyId + " not found"));

        if(company.getStripeCustomerId() == null){
            Customer customer = Customer.create(
                    CustomerCreateParams.builder()
                            .setEmail(company.getCompanyOwner().getEmail())
                            .setName(company.getCompanyName())
                            .build()
            );

            company.setStripeCustomerId(customer.getId());
            companyRepository.save(company);
        }

        int employees = company.getWorkersQuantity();

        if (employees == 0) {
            throw new RuntimeException("Cannot activate billing with 0 employees");
        }

        // 1. Base subscription price ($15)
        String basePriceId = "price_FACE_BASE_15"; // возьми из application.yml

        // 2. Seats price ($8 × employees)
        long seatsAmount = employees * 800L;

        Price seatsPrice = Price.create(
                PriceCreateParams.builder()
                        .setUnitAmount(seatsAmount)
                        .setCurrency("usd")
                        .setRecurring(
                                PriceCreateParams.Recurring.builder()
                                        .setInterval(PriceCreateParams.Recurring.Interval.MONTH)
                                        .build()
                        )
                        .setProduct("prod_FACE_CHECK")
                        .build()
        );

        // 3. Checkout Session с 2 товарами
        Session session = Session.create(
                SessionCreateParams.builder()
                        .setMode(SessionCreateParams.Mode.SUBSCRIPTION)
                        .setCustomer(company.getStripeCustomerId())

                        // BASE PRICE
                        .addLineItem(
                                SessionCreateParams.LineItem.builder()
                                        .setPrice(basePriceId)
                                        .setQuantity(1L)
                                        .build()
                        )

                        // SEATS PRICE
                        .addLineItem(
                                SessionCreateParams.LineItem.builder()
                                        .setPrice(seatsPrice.getId())
                                        .setQuantity(1L)
                                        .build()
                        )

                        .setSuccessUrl(successUrl + "?session_id={CHECKOUT_SESSION_ID}")
                        .setCancelUrl(cancelUrl)
                        .build()
        );

        return new BillingResponse(session.getUrl());
    }



    public void cancelSubscription(Integer companyId, Authentication authentication) throws StripeException {
        User user = (User) authentication.getPrincipal();
        validateCompanyAccess(user, companyId);

        var company = companyRepository.findById(companyId)
                .orElseThrow(() -> new EntityNotFoundException("Company with ID: " + companyId + " not found"));

        if (company.getStripeSubscriptionId() == null) {
            throw new RuntimeException("Company has no active subscription");
        }

        Subscription subscription = Subscription.retrieve(company.getStripeSubscriptionId());

        Subscription updated = subscription.update(
                SubscriptionUpdateParams.builder()
                        .setCancelAtPeriodEnd(true)
                        .build()
        );

        company.setSubscriptionStatus(updated.getStatus());
        companyRepository.save(company);
    }


    public void updateSeats(Integer companyId, Authentication authentication) throws StripeException {
        User user = (User) authentication.getPrincipal();
        validateCompanyAccess(user, companyId);
        updateSeatsInternal(companyId);
    }


    public void updateSeats(Integer companyId) throws StripeException {
        updateSeatsInternal(companyId);
    }


    private void updateSeatsInternal(Integer companyId) throws StripeException{
        var company = companyRepository.findById(companyId)
                .orElseThrow(() -> new EntityNotFoundException("Company with ID: " + companyId + " not found"));

        if (company.getStripeSubscriptionId() == null) {
            throw new RuntimeException("Company has no active subscription");
        }

        int employees = company.getWorkersQuantity();
        long newAmount = employees * 800L;

        // создаём новый price только для seats
        Price price = Price.create(
                PriceCreateParams.builder()
                        .setUnitAmount(newAmount)
                        .setCurrency("usd")
                        .setRecurring(
                                PriceCreateParams.Recurring.builder()
                                        .setInterval(PriceCreateParams.Recurring.Interval.MONTH)
                                        .build()
                        )
                        .setProduct("prod_FACE_CHECK")
                        .build()
        );

        // получаем подписку
        Subscription subscription = Subscription.retrieve(company.getStripeSubscriptionId());

        // обновляем только seat item
        SubscriptionUpdateParams params = SubscriptionUpdateParams.builder()
                .addItem(
                        SubscriptionUpdateParams.Item.builder()
                                .setId(company.getStripeSubscriptionItemId())
                                .setPrice(price.getId())
                                .build()
                )
                .setProrationBehavior(SubscriptionUpdateParams.ProrationBehavior.NONE)
                .build();

        Subscription updated = subscription.update(params);

        company.setSubscriptionStatus(updated.getStatus());
        companyRepository.save(company);

    }







    private void validateCompanyAccess(User user, Integer companyId) {
        boolean isAdmin = user.getRoles().stream()
                .anyMatch(role -> role.getName().equals("ADMIN") ||
                        role.getName().equals("AppOwner"));

        if (!isAdmin) {
            throw new AccessDeniedException("You don't have permission for this operation!");
        }

        // ✅ Проверяем принадлежность к компании
        if (!user.getCompany().getId().equals(companyId)) {
            throw new AccessDeniedException("You don't have access to this company!");
        }
    }










}
