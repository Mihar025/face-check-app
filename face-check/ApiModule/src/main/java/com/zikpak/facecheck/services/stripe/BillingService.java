package com.zikpak.facecheck.services.stripe;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.Price;
import com.stripe.model.Subscription;
import com.stripe.param.CustomerCreateParams;
import com.stripe.param.PriceCreateParams;
import com.stripe.param.SubscriptionUpdateParams;
import com.stripe.param.checkout.SessionCreateParams;
import com.zikpak.facecheck.entity.Company;
import com.zikpak.facecheck.entity.User;
import com.zikpak.facecheck.repository.CompanyRepository;
import com.zikpak.facecheck.repository.RoleRepository;
import com.zikpak.facecheck.repository.UserRepository;
import com.zikpak.facecheck.requestsResponses.stripeDTOs.CreateSubscriptionRequest;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import com.stripe.model.checkout.Session;

import java.math.BigDecimal;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class BillingService {

    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;


    @Value( "${stripe.success-url}")
    private  String successUrl;

    @Value( "${stripe.cancel-url}")
    private String cancelUrl;

    @Value( "${stripe.base-price-id}")
    private String stripeBasePriceId;

    @Value( "${stripe.product-id}")
    private String stripeBaseProductId;

    @Value("${stripe.secret-key}")
    private String stripeSecretKey;

    @PostConstruct
    public void init() {
        Stripe.apiKey = stripeSecretKey;
    }

    @Transactional
    public BillingResponse activateBilling(Integer companyId, Authentication authentication) throws StripeException {
        User user = (User) authentication.getPrincipal();
        validateCompanyAccess(user, companyId);

        var company = companyRepository.findById(companyId)
                .orElseThrow(() -> new EntityNotFoundException("Company not found"));

        // Проверяем что цены установлены
        if (company.getStripeBasePriceId() == null || company.getStripeSeatsPriceId() == null) {
            throw new IllegalStateException("Pricing not configured. Contact support.");
        }

        if (company.getStripeCustomerId() == null) {
            Customer customer = Customer.create(
                    CustomerCreateParams.builder()
                            .setEmail(company.getCompanyOwner().getEmail())
                            .setName(company.getCompanyName())
                            .build()
            );
            company.setStripeCustomerId(customer.getId());
            companyRepository.save(company);
        }

        int employees = userRepository.countActiveWorkersByCompanyId(companyId);
        if (employees == 0) {
            throw new RuntimeException("Cannot activate with 0 employees");
        }

        Session session = Session.create(
                SessionCreateParams.builder()
                        .setMode(SessionCreateParams.Mode.SUBSCRIPTION)
                        .setCustomer(company.getStripeCustomerId())
                        .addLineItem(
                                SessionCreateParams.LineItem.builder()
                                        .setPrice(company.getStripeBasePriceId())
                                        .setQuantity(1L)
                                        .build()
                        )
                        .addLineItem(
                                SessionCreateParams.LineItem.builder()
                                        .setPrice(company.getStripeSeatsPriceId())
                                        .setQuantity((long) employees)
                                        .build()
                        )
                        .setSuccessUrl(successUrl + "?session_id={CHECKOUT_SESSION_ID}")
                        .setCancelUrl(cancelUrl)
                        .build()
        );

        return new BillingResponse(session.getUrl());
    }


    @Transactional
    public BillingResponse activateBillingV2(Authentication authentication, CreateSubscriptionRequest request) throws StripeException {

        User user = (User) authentication.getPrincipal();
        Integer companyId = request.getCompanyId();
        validateCompanyAccess(user, companyId);

        var company = companyRepository.findById(companyId)
                .orElseThrow(() -> new EntityNotFoundException("Company not found"));

        if (company.getCompanyOwner() == null) {
            throw new IllegalStateException("Company owner is null");
        }

        // Stripe Customer
        if (company.getStripeCustomerId() == null) {
            Customer customer = Customer.create(
                    CustomerCreateParams.builder()
                            .setEmail(company.getCompanyOwner().getEmail())
                            .setName(company.getCompanyName())
                            .build()
            );
            company.setStripeCustomerId(customer.getId());
        }

        int employees = userRepository.countActiveWorkersByCompanyId(companyId);
        if (employees == 0) {
            throw new RuntimeException("Cannot activate with 0 employees");
        }

        BigDecimal newMonthly = request.getMonthlySubscription();
        BigDecimal newPerEmployee = request.getPricePerEmployee();

        // ✅ ОПТИМИЗАЦИЯ: создаём Price только если нужно
        boolean priceChanged = company.getStripeBasePriceId() == null
                || company.getStripeSeatsPriceId() == null
                || !newMonthly.equals(company.getMonthlySubscription())
                || !newPerEmployee.equals(company.getPricePerEmployee());

        if (priceChanged) {
            log.info("Creating new Stripe Prices for company {}", companyId);

            long basePriceAmount = newMonthly.multiply(new BigDecimal("100")).longValueExact();
            long seatsAmount = newPerEmployee.multiply(new BigDecimal("100")).longValueExact();

            Price basePrice = Price.create(
                    PriceCreateParams.builder()
                            .setUnitAmount(basePriceAmount)
                            .setCurrency("usd")
                            .setRecurring(PriceCreateParams.Recurring.builder()
                                    .setInterval(PriceCreateParams.Recurring.Interval.MONTH)
                                    .build())
                            .setProduct(stripeBaseProductId)
                            .setNickname("Base: " + company.getCompanyName())
                            .build()
            );

            Price seatsPrice = Price.create(
                    PriceCreateParams.builder()
                            .setUnitAmount(seatsAmount)
                            .setCurrency("usd")
                            .setRecurring(PriceCreateParams.Recurring.builder()
                                    .setInterval(PriceCreateParams.Recurring.Interval.MONTH)
                                    .build())
                            .setProduct(stripeBaseProductId)
                            .setNickname("Per employee: " + company.getCompanyName())
                            .build()
            );

            company.setStripeBasePriceId(basePrice.getId());
            company.setStripeSeatsPriceId(seatsPrice.getId());
            company.setMonthlySubscription(newMonthly);
            company.setPricePerEmployee(newPerEmployee);
        } else {
            log.info("Reusing existing Stripe Prices for company {}", companyId);
        }

        // Checkout Session — всегда используем сохранённые ID
        Session session = Session.create(
                SessionCreateParams.builder()
                        .setMode(SessionCreateParams.Mode.SUBSCRIPTION)
                        .setCustomer(company.getStripeCustomerId())
                        .addLineItem(
                                SessionCreateParams.LineItem.builder()
                                        .setPrice(company.getStripeBasePriceId())
                                        .setQuantity(1L)
                                        .build()
                        )
                        .addLineItem(
                                SessionCreateParams.LineItem.builder()
                                        .setPrice(company.getStripeSeatsPriceId())
                                        .setQuantity((long) employees)
                                        .build()
                        )
                        .setSuccessUrl(successUrl + "?session_id={CHECKOUT_SESSION_ID}")
                        .setCancelUrl(cancelUrl)
                        .build()
        );

        companyRepository.save(company);
        return new BillingResponse(session.getUrl());
    }




    @Transactional
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
        BigDecimal pricePerSeat = company.getPricePerEmployee();

        long newAmount = pricePerSeat.multiply(new BigDecimal("100"))
                .multiply(new BigDecimal(employees))
                .longValueExact();

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
                        .setProduct(stripeBaseProductId)
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
        System.out.println("DEBUG billing: userId=" + user.getId()
                + " roles=" + user.getRoles().stream().map(r->r.getName()).toList()
                + " userCompanyId=" + (user.getCompany()!=null ? user.getCompany().getId() : null)
                + " targetCompanyId=" + companyId);



        boolean isAppOwner = user.getRoles().stream().anyMatch(
                       role -> role.getName().equals("AppOwner"));

        if (isAppOwner) {
            return;
        }

        boolean isAdmin = user.getRoles().stream().anyMatch(
                role -> role.getName().equals("ADMIN"));

        if (!isAdmin) {
            throw new AccessDeniedException("You don't have permission for this operation!");
        }

        // ✅ Проверяем принадлежность к компании
        if (!user.getCompany().getId().equals(companyId)) {
            throw new AccessDeniedException("You don't have access to this company!");
        }
    }







}
