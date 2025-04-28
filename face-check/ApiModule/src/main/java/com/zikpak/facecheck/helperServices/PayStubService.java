package com.zikpak.facecheck.helperServices;


import com.zikpak.facecheck.entity.employee.WorkerPayroll;
import com.zikpak.facecheck.repository.WorkerPayrollRepository;
import com.zikpak.facecheck.requestsResponses.PayStubDTO;
import com.zikpak.facecheck.services.amazonS3Service.AmazonS3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
public class PayStubService {

    private final WorkerPayrollRepository workerPayrollRepository;
    private final PayStubPdfGeneratorService payStubPdfGeneratorService;
    private final AmazonS3Service amazonS3Service;

    public byte[] generatePayStubPdf(Integer payrollId) {
        WorkerPayroll payroll = workerPayrollRepository.findById(payrollId)
                .orElseThrow(() -> new RuntimeException("Payroll not found"));

        var worker = payroll.getWorker();
        var company = worker.getCompany();

        PayStubDTO stub = PayStubDTO.builder()
                .employeeName(worker.getFirstName() + " " + worker.getLastName())
                .employeeAddress(worker.getHomeAddress())
                .employerName(company.getCompanyName())
                .employerAddress(company.getCompanyAddress())
                .periodStart(payroll.getPeriodStart())
                .periodEnd(payroll.getPeriodEnd())
                .totalGrossPay(payroll.getGrossPay())
                .federalTax(payroll.getFederalWithholding())
                .socialSecurityTax(payroll.getSocialSecurityEmployee())
                .medicareTax(payroll.getMedicare())
                .stateTax(payroll.getNyStateWithholding())
                .localTax(payroll.getNyLocalWithholding())
                .netPay(payroll.getNetPay())
                .build();
        byte[] pdf = payStubPdfGeneratorService.generatePayStubPdf(stub);

        String fileName = "paystubs/" + payroll.getWorker().getId() + "/" + payrollId + ".pdf";
        amazonS3Service.uploadPdfToS3(pdf, fileName);

        return payStubPdfGeneratorService.generatePayStubPdf(stub);
    }




}
