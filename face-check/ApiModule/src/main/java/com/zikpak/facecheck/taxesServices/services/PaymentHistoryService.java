package com.zikpak.facecheck.taxesServices.services;

import com.zikpak.facecheck.entity.PaymentHistoryIrs;
import com.zikpak.facecheck.entity.PaymentType;
import com.zikpak.facecheck.entity.User;
import com.zikpak.facecheck.repository.CompanyRepository;
import com.zikpak.facecheck.repository.PaymentHistoryIrsRepository;
import com.zikpak.facecheck.requestsResponses.PageResponse;
import com.zikpak.facecheck.requestsResponses.worker.RelatedUserInCompanyResponse;
import com.zikpak.facecheck.taxesServices.dto.PaymentHistoryRequest;
import com.zikpak.facecheck.taxesServices.dto.PaymentHistoryResponse;
import com.zikpak.facecheck.taxesServices.mapper.PaymentHistoryMapper;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.Transient;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentHistoryService {

    private final PaymentHistoryIrsRepository paymentHistoryIrsRepository;
    private final CompanyRepository companyRepository;
    private final PaymentHistoryMapper paymentHistoryMapper;



    public BigDecimal getTotalPaymentsForQuarter941Form(Integer companyId, int quarter, int year) {
        return paymentHistoryIrsRepository.getTotalPaidForQuarter(companyId, quarter, year);
    }


    public PaymentHistoryResponse findPaymentByDate(LocalDate date) {
        var foundedPayment = paymentHistoryIrsRepository.findPaymentHistoryIrsByPaymentDate(date)
                .orElseThrow(() -> new EntityNotFoundException("Cannot find Payment by provided Date!"));
        return paymentHistoryMapper.toCompanyWorkerResponse(foundedPayment);
    }


    public PageResponse<PaymentHistoryResponse> findAllPaymentsForIrs(int page, int size, Integer companyId){

        var foundedCompany = companyRepository.findById(companyId)
                .orElseThrow(() -> new RuntimeException("Company not found with id: " + companyId));

        Pageable pageable = PageRequest.of(page, size, Sort.by("paymentDate").descending());
        Page<PaymentHistoryIrs> payments = paymentHistoryIrsRepository.findAllByCompany_Id(foundedCompany.getId(), pageable);
        if(payments.isEmpty()) {
            log.info("No employees found for company: {}", foundedCompany.getId());
        }
        List<PaymentHistoryResponse> paymentsResponse = payments.getContent()
                .stream()
                .map(paymentHistoryMapper::toCompanyWorkerResponse)
                .toList();
        return new PageResponse<>(
                paymentsResponse,
                payments.getNumber(),
                payments.getSize(),
                payments.getTotalElements(),
                payments.getTotalPages(),
                payments.isFirst(),
                payments.isLast()
        );
    }


    public PageResponse<PaymentHistoryResponse> findAllPaymentsForIrsAppOwner(Authentication authentication, int page, int size){
        checkIsUserHasAdminRoleAndBusinessOwner(authentication);
        Pageable pageable = PageRequest.of(page, size, Sort.by("paymentDate").descending());
        Page<PaymentHistoryIrs> payments = paymentHistoryIrsRepository.findAll(pageable);
        List<PaymentHistoryResponse> paymentsResponse = payments.getContent()
                .stream()
                .map(paymentHistoryMapper::toCompanyWorkerResponse)
                .toList();
        return new PageResponse<>(
                paymentsResponse,
                payments.getNumber(),
                payments.getSize(),
                payments.getTotalElements(),
                payments.getTotalPages(),
                payments.isFirst(),
                payments.isLast()
        );
    }


    public PageResponse<PaymentHistoryResponse> findAllPaymentsForIrsQuarter(int page, int size, Integer companyId, int year,  int quarter){

        var foundedCompany = companyRepository.findById(companyId)
                .orElseThrow(() -> new RuntimeException("Company not found with id: " + companyId));

        Pageable pageable = PageRequest.of(page, size, Sort.by("paymentDate").descending());
        Page<PaymentHistoryIrs> payments = paymentHistoryIrsRepository.findAllByCompany_IdAndYearAndQuarter(foundedCompany.getId(), quarter, year, pageable);
        if(payments.isEmpty()) {
            log.info("No employees found for company: {}", foundedCompany.getId());
        }
        List<PaymentHistoryResponse> paymentsResponse = payments.getContent()
                .stream()
                .map(paymentHistoryMapper::toCompanyWorkerResponse)
                .toList();
        return new PageResponse<>(
                paymentsResponse,
                payments.getNumber(),
                payments.getSize(),
                payments.getTotalElements(),
                payments.getTotalPages(),
                payments.isFirst(),
                payments.isLast()
        );
    }


    @Transactional(rollbackOn = Exception.class)
    public PaymentHistoryResponse addNewPayment(
            @Valid PaymentHistoryRequest request,
            Integer companyId
    ) {
        var company = companyRepository.findById(companyId)
                .orElseThrow(() -> new RuntimeException("Company not found with id: " + companyId));

        // 1) ищем существующий платёж за ту же дату/год/квартал
        var existingOpt = paymentHistoryIrsRepository
                .findByCompany_IdAndPaymentDateAndYearAndQuarterAndPaymentTypeEnum(
                        companyId, request.getPaymentDate(), request.getYear(), request.getQuarter(), request.getPaymentType()
                );

        PaymentHistoryIrs payment;
        if (existingOpt.isPresent()) {
            // 2a) если нашли — накидываем сумму и сохраняем
            payment = existingOpt.get();
            payment.setAmount(payment.getAmount().add(request.getAmount()));
        } else {
            // 2b) иначе создаём новый
            payment = new PaymentHistoryIrs();
            payment.setCompany(company);
            payment.setAmount(request.getAmount());
            payment.setPaymentDate(request.getPaymentDate());
            payment.setQuarter(request.getQuarter());
            payment.setYear(request.getYear());
            payment.setPaymentTypeEnum(request.getPaymentType());
            payment.setNotes(request.getNotes());
            payment.setCreatedAt(LocalDateTime.now());
        }
        // 3) сохраняем (save умеет и вставку, и обновление)
        paymentHistoryIrsRepository.save(payment);

        // 4) возвращаем DTO
        return paymentHistoryMapper.toCompanyWorkerResponse(payment);
    }


    @Transactional(rollbackOn = Exception.class)
    public void deletePaymentById(Integer paymentId){
        paymentHistoryIrsRepository.deleteById(paymentId);
    }



    private User checkIsUserHasAdminRoleAndBusinessOwner(Authentication authentication) {
        User user = (User) authentication.getPrincipal();

        boolean isAppOwner = user.getRoles().stream()
                .anyMatch(role -> "AppOwner".equals(role.getName()));

        if(!user.isAdmin() && !user.isBusinessOwner() && !isAppOwner) {
            throw new AccessDeniedException("You dont have permission for this operation!");
        }
        return user;
    }





}
