package com.zikpak.facecheck.taxesServices.mapper;

import com.zikpak.facecheck.entity.PaymentHistoryIrs;
import com.zikpak.facecheck.taxesServices.dto.PaymentHistoryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentHistoryMapper {


    public PaymentHistoryResponse toCompanyWorkerResponse(PaymentHistoryIrs paymentHistoryIrs) {
        return PaymentHistoryResponse.builder()
                .paymentHistoryIrsId(paymentHistoryIrs.getPaymentHistoryIrsId())
                .companyId(paymentHistoryIrs.getCompany().getId())
                .amount(paymentHistoryIrs.getAmount())
                .paymentDate(paymentHistoryIrs.getPaymentDate())
                .quarter(paymentHistoryIrs.getQuarter())
                .year(paymentHistoryIrs.getYear())
                .paymentType(paymentHistoryIrs.getPaymentTypeEnum())
                .notes(paymentHistoryIrs.getNotes())
                .createDate(paymentHistoryIrs.getCreatedAt())
                .build();
    }
}