package com.zikpak.facecheck.taxesServices.newTaxesController;

import com.zikpak.facecheck.requestsResponses.PageResponse;
import com.zikpak.facecheck.taxesServices.dto.PaymentHistoryRequest;
import com.zikpak.facecheck.taxesServices.dto.PaymentHistoryResponse;
import com.zikpak.facecheck.taxesServices.services.PaymentHistoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@RequestMapping("payment-history-irs")
public class PaymentHistoryIrsController {
    private final PaymentHistoryService paymentHistoryService;


    @GetMapping("/all")
    public PageResponse<PaymentHistoryResponse> getAllPayments(
            @RequestParam Integer companyId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return paymentHistoryService.findAllPaymentsForIrs(page, size, companyId);
    }

    @GetMapping("/quarter")
    public PageResponse<PaymentHistoryResponse> getPaymentsByQuarter(
            @RequestParam Integer companyId,
            @RequestParam int year,
            @RequestParam int quarter,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return paymentHistoryService.findAllPaymentsForIrsQuarter(page, size, companyId, year, quarter);
    }

    @GetMapping("/total")
    public BigDecimal getTotalPaidForQuarter(
            @RequestParam Integer companyId,
            @RequestParam int quarter,
            @RequestParam int year
    ) {
        return paymentHistoryService.getTotalPaymentsForQuarter941Form(companyId, quarter, year);
    }

    @GetMapping("/by-date")
    public PaymentHistoryResponse getPaymentByDate(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        return paymentHistoryService.findPaymentByDate(date);
    }

    @PostMapping("/add/{companyId}")
    public PaymentHistoryResponse addPayment(
            @Valid @RequestBody PaymentHistoryRequest request,
            @PathVariable("companyId") Integer companyId
    ) {
        return paymentHistoryService.addNewPayment(request, companyId);
    }

    @DeleteMapping("/delete/{paymentId}")
    public void deletePayment(@PathVariable Integer paymentId) {
        paymentHistoryService.deletePaymentById(paymentId);
    }


}
