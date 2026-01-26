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
    public BillingResponse activateBilling(Authentication authentication) throws StripeException {
        User user = (User) authentication.getPrincipal();
        Integer companyId = user.getCompany().getId();
        validateCompanyAccess(user, companyId);

        var company = companyRepository.findById(companyId) //replace on id from request
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

        int employees =  userRepository.countActiveWorkersByCompanyId(companyId);

        if (employees == 0) {
            throw new RuntimeException("Cannot activate billing with 0 employees");
        }

        long basePriceAmount = company.getMonthlySubscription()
                .multiply(new BigDecimal("100"))
                .longValueExact();

        long seatsAmount = company.getPricePerEmployee()
                .multiply(new BigDecimal("100"))
                .longValueExact();


        Price basePrice = Price.create(
                PriceCreateParams.builder()
                        .setUnitAmount(basePriceAmount)
                        .setCurrency("usd")
                        .setRecurring(
                                PriceCreateParams.Recurring.builder()
                                        .setInterval(PriceCreateParams.Recurring.Interval.MONTH)
                                        .build()
                        )
                        .setProduct(stripeBaseProductId)
                        .setNickname("Base price for company: " + company.getCompanyName())
                        .build()
        );

        log.debug("stripeSecretKey present? {}", stripeSecretKey != null && !stripeSecretKey.isBlank());
        log.debug("stripeBaseProductId={}", stripeBaseProductId);
        log.debug("successUrl={}, cancelUrl={}", successUrl, cancelUrl);


        Price seatsPrice = Price.create(
                PriceCreateParams.builder()
                        .setUnitAmount(seatsAmount)
                        .setCurrency("usd")
                        .setRecurring(
                                PriceCreateParams.Recurring.builder()
                                        .setInterval(PriceCreateParams.Recurring.Interval.MONTH)
                                        .build()
                        )
                        .setProduct(stripeBaseProductId)
                        .setNickname("Price for employee for company: " + company.getCompanyName())
                        .build()
        );

        // 3. Checkout Session с 2 товарами
        try {
            Session session = Session.create(
                    SessionCreateParams.builder()
                            .setMode(SessionCreateParams.Mode.SUBSCRIPTION)
                            .setCustomer(company.getStripeCustomerId())

                            // BASE PRICE
                            .addLineItem(
                                    SessionCreateParams.LineItem.builder()
                                            .setPrice(basePrice.getId())
                                            .setQuantity(1L)
                                            .build()
                            )

                            // SEATS PRICE
                            .addLineItem(
                                    SessionCreateParams.LineItem.builder()
                                            .setPrice(seatsPrice.getId())
                                            .setQuantity((long) employees)  // ✅ тут employees
                                            .build()
                            )

                            .setSuccessUrl(successUrl + "?session_id={CHECKOUT_SESSION_ID}")
                            .setCancelUrl(cancelUrl)
                            .build()
            );

            return new BillingResponse(session.getUrl());

        } catch (com.stripe.exception.InvalidRequestException e) {
            var err = e.getStripeError();
            log.error("Stripe InvalidRequestException: message={}, param={}, code={}, requestId={}",
                    e.getMessage(),
                    err != null ? err.getParam() : null,
                    err != null ? err.getCode() : null,
                    e.getRequestId()
            );
            throw e;
        }

    }


    @Transactional
    public BillingResponse activateBillingV2(Authentication authentication, CreateSubscriptionRequest createSubscriptionRequest) throws StripeException {

        log.info("PER MONTH: {}", createSubscriptionRequest.getMonthlySubscription());
        log.info("PRICE PER EMPLOYEE: {}", createSubscriptionRequest.getPricePerEmployee());

        User user = (User) authentication.getPrincipal();

        Integer companyId = user.getCompany().getId();
        validateCompanyAccess(user, companyId);

        var company = companyRepository.findById(companyId) //replace on id from request
                .orElseThrow(() -> new EntityNotFoundException("Company with ID: " + companyId + " not found"));

        if (company.getCompanyOwner() == null) {
            throw new IllegalStateException("Company owner is null for companyId=" + companyId);
        }

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

        int employees =  userRepository.countActiveWorkersByCompanyId(companyId);
        if (employees == 0) {
            throw new RuntimeException("Cannot activate billing with 0 employees");
        }

        // 2. Seats price taking number from Request, and need to transform from BigDecimal to long L taking this from request
        long basePriceAmount = createSubscriptionRequest.getMonthlySubscription()
                .multiply(new BigDecimal("100"))
                .longValueExact();

        long seatsAmount = createSubscriptionRequest.getPricePerEmployee()
                .multiply(new BigDecimal("100"))
                .longValueExact();


        Price basePrice = Price.create(
                PriceCreateParams.builder()
                        .setUnitAmount(basePriceAmount)
                        .setCurrency("usd")
                        .setRecurring(
                                PriceCreateParams.Recurring.builder()
                                        .setInterval(PriceCreateParams.Recurring.Interval.MONTH)
                                        .build()
                        )
                        .setProduct(stripeBaseProductId)
                        .setNickname("Base price for company: " + company.getCompanyName())
                        .build()
        );

        log.debug("stripeSecretKey present? {}", stripeSecretKey != null && !stripeSecretKey.isBlank());
        log.debug("stripeBaseProductId={}", stripeBaseProductId);
        log.debug("successUrl={}, cancelUrl={}", successUrl, cancelUrl);


        Price seatsPrice = Price.create(
                PriceCreateParams.builder()
                        .setUnitAmount(seatsAmount)
                        .setCurrency("usd")
                        .setRecurring(
                                PriceCreateParams.Recurring.builder()
                                        .setInterval(PriceCreateParams.Recurring.Interval.MONTH)
                                        .build()
                        )
                        .setProduct(stripeBaseProductId)
                        .setNickname("Price for employee for company: " + company.getCompanyName())
                        .build()
        );

        // 3. Checkout Session с 2 товарами
        try {
            Session session = Session.create(
                    SessionCreateParams.builder()
                            .setMode(SessionCreateParams.Mode.SUBSCRIPTION)
                            .setCustomer(company.getStripeCustomerId())

                            // BASE PRICE
                            .addLineItem(
                                    SessionCreateParams.LineItem.builder()
                                            .setPrice(basePrice.getId())
                                            .setQuantity(1L)
                                            .build()
                            )

                            // SEATS PRICE
                            .addLineItem(
                                    SessionCreateParams.LineItem.builder()
                                            .setPrice(seatsPrice.getId())
                                            .setQuantity((long) employees)  // ✅ тут employees
                                            .build()
                            )

                            .setSuccessUrl(successUrl + "?session_id={CHECKOUT_SESSION_ID}")
                            .setCancelUrl(cancelUrl)
                            .build()
            );

            company.setMonthlySubscription(createSubscriptionRequest.getMonthlySubscription());
            company.setPricePerEmployee(createSubscriptionRequest.getPricePerEmployee());
            companyRepository.save(company);

            return new BillingResponse(session.getUrl());

        } catch (com.stripe.exception.InvalidRequestException e) {
            var err = e.getStripeError();
            log.error("Stripe InvalidRequestException: message={}, param={}, code={}, requestId={}",
                    e.getMessage(),
                    err != null ? err.getParam() : null,
                    err != null ? err.getCode() : null,
                    e.getRequestId()
            );
            throw e;
        }



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
