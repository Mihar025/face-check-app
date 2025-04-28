package com.zikpak.facecheck.helperServices;


import com.zikpak.facecheck.entity.Company;
import com.zikpak.facecheck.entity.User;
import com.zikpak.facecheck.repository.CompanyRepository;
import com.zikpak.facecheck.repository.UserRepository;
import com.zikpak.facecheck.requestsResponses.finance.CompanyYearlySummaryDTO;
import com.zikpak.facecheck.requestsResponses.finance.WorkerYearlySummaryDto;
import com.zikpak.facecheck.services.amazonS3Service.AmazonS3Service;
import com.zikpak.facecheck.services.workAttendanceService.WorkAttendanceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CompanyPayrollService {

    private final UserRepository userRepository;
    private final WorkerPayRollService workerPayRollService;
    private final CompanyRepository companyRepository;
    private final W3PdfGeneratorService w3PdfGeneratorService;
    private final AmazonS3Service amazonS3Service;


    public CompanyYearlySummaryDTO calculateCompanyYearlySummary(Integer companyId, int year){
        List<User> workers = userRepository.findAllByCompanyId(companyId);
        BigDecimal totalWages = BigDecimal.ZERO;
        BigDecimal totalFederalIncomeTax = BigDecimal.ZERO;
        BigDecimal totalSocialSecurityWages = BigDecimal.ZERO;
        BigDecimal totalSocialSecurityTax = BigDecimal.ZERO;
        BigDecimal totalMedicareWages = BigDecimal.ZERO;
        BigDecimal totalMedicareTax = BigDecimal.ZERO;

        int totalEmployees = 0;
        for(User worker: workers){
            WorkerYearlySummaryDto workerSummary = workerPayRollService.calculateWorkerYearlyTotals(worker.getId(), year);
            totalWages = totalWages.add(workerSummary.getGrossPayTotal());
            totalFederalIncomeTax = totalFederalIncomeTax.add(workerSummary.getFederalWithholdingTotal());
            totalSocialSecurityWages = totalSocialSecurityWages.add(workerSummary.getGrossPayTotal());
            totalSocialSecurityTax = totalSocialSecurityTax.add(workerSummary.getSocialSecurityEmployeeTotal());
            totalMedicareWages = totalMedicareWages.add(workerSummary.getGrossPayTotal());
            totalMedicareTax = totalMedicareTax.add(workerSummary.getMedicareTotal());
            totalEmployees++;
        }
        return CompanyYearlySummaryDTO.builder()
                .totalWages(totalWages)
                .totalFederalIncomeTax(totalFederalIncomeTax)
                .totalSocialSecurityWages(totalSocialSecurityWages)
                .totalSocialSecurityTax(totalSocialSecurityTax)
                .totalMedicareWages(totalMedicareWages)
                .totalMedicareTax(totalMedicareTax)
                .totalEmployees(totalEmployees)
                .year(year)
                .build();

    }


    public Company findCompanyById(Integer companyId) {
        return companyRepository.findById(companyId)
                .orElseThrow(() -> new RuntimeException("Company not found"));
    }


    public byte[] generatePdfW3(Integer companyId, int year) {
        try {
            var summary = calculateCompanyYearlySummary(companyId, year);
            var company = findCompanyById(companyId);

            byte[] pdfContent = w3PdfGeneratorService.generateW3Pdf(
                    summary,
                    company.getCompanyName(),
                    company.getEmployerEIN(),
                    company.getCompanyAddress()
            );

            String fileName = "w3-summary/" + year + "/" + "w3_" + companyId + ".pdf";
            amazonS3Service.uploadPdfToS3(pdfContent, fileName);

            return pdfContent;
        } catch (Exception e) {
            log.error("❌ Ошибка генерации W-3 PDF: {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

}
