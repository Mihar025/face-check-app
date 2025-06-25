package com.zikpak.facecheck.taxesServices.ASCIIservices;

import com.zikpak.facecheck.entity.Company;
import com.zikpak.facecheck.entity.User;
import com.zikpak.facecheck.helperServices.WorkerPayRollService;
import com.zikpak.facecheck.repository.CompanyRepository;
import com.zikpak.facecheck.repository.UserRepository;
import com.zikpak.facecheck.requestsResponses.finance.WorkerYearlySummaryDto;
import com.zikpak.facecheck.services.amazonS3Service.AmazonS3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class EFW2GeneratorService {
    private static final int RECORD_LENGTH = 512;
    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;
    private final WorkerPayRollService workerPayRollService;
    private final AmazonS3Service amazonS3Service;

    /**
     * Generates the electronic EFW2 file as ASCII bytes.
     */
    public byte[] generateEfw2File(Integer companyId, int year) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new RuntimeException("Company not found: " + companyId));
        String rawEin = company.getEmployerEIN() != null
                ? company.getEmployerEIN().replaceAll("\\D", "") : "";
        String employerEin = padLeft(rawEin, 9).replace(' ', '0');
        if (employerEin.length() != 9) {
            log.warn("Normalized EIN length not 9 for company {}: {}", companyId, rawEin);
        }

        List<User> employees = userRepository.findAllByCompanyId(companyId);
        LocalDate today = LocalDate.now();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyyMMdd");

        StringBuilder sb = new StringBuilder();

        // Record Type 1: File Header
        String headerBody = "1"
                + employerEin
                + fmt.format(today)
                + padRight(String.valueOf(year), 4)
                + "W2  "; // Standard EFW2 file type
        sb.append(formatRecord(headerBody));

        // Record Type 2: One line per employee
        for (User user : employees) {
            WorkerYearlySummaryDto dto = workerPayRollService.calculateWorkerYearlyTotals(user.getId(), year);
            String rawSsn = user.getSSN_WORKER() != null
                    ? user.getSSN_WORKER().replaceAll("\\D", "") : "";
            String ssn = padLeft(rawSsn, 9).replace(' ', '0');
            if (ssn.length() != 9) {
                log.warn("Normalized SSN length not 9 for user {}: {}", user.getId(), rawSsn);
            }

            BigDecimal wages = dto.getGrossPayTotal() != null ? dto.getGrossPayTotal() : BigDecimal.ZERO;
            BigDecimal federal = dto.getFederalWithholdingTotal() != null
                    ? dto.getFederalWithholdingTotal() : BigDecimal.ZERO;
            BigDecimal ssTax = dto.getSocialSecurityEmployeeTotal() != null
                    ? dto.getSocialSecurityEmployeeTotal() : BigDecimal.ZERO;
            BigDecimal medWages = dto.getMedicareTotal() != null
                    ? dto.getMedicareTotal() : BigDecimal.ZERO;
            // Medicare tax withheld: 1.45% of wages
            BigDecimal medTax = medWages.multiply(new BigDecimal("0.0145"))
                    .setScale(0, RoundingMode.HALF_UP);

            String detailBody = "2"
                    + ssn
                    + employerEin
                    + padLeft(formatCents(wages), 10)
                    + padLeft(formatCents(federal), 10)
                    + padLeft(formatCents(ssTax), 10)
                    + padLeft(formatCents(medWages), 10)
                    + padLeft(formatCents(medTax), 10);
            sb.append(formatRecord(detailBody));
        }

        // Record Type 3: W-3 summary (totals)
        BigDecimal totalWages = BigDecimal.ZERO;
        BigDecimal totalFed = BigDecimal.ZERO;
        BigDecimal totalSS = BigDecimal.ZERO;
        BigDecimal totalMedW = BigDecimal.ZERO;
        BigDecimal totalMedT = BigDecimal.ZERO;
        for (User u : employees) {
            WorkerYearlySummaryDto d = workerPayRollService.calculateWorkerYearlyTotals(u.getId(), year);
            BigDecimal medW = d.getMedicareTotal() != null ? d.getMedicareTotal() : BigDecimal.ZERO;
            BigDecimal medT = medW.multiply(new BigDecimal("0.0145"))
                    .setScale(0, RoundingMode.HALF_UP);
            totalWages = totalWages.add(d.getGrossPayTotal() != null ? d.getGrossPayTotal() : BigDecimal.ZERO);
            totalFed = totalFed.add(d.getFederalWithholdingTotal() != null ? d.getFederalWithholdingTotal() : BigDecimal.ZERO);
            totalSS = totalSS.add(d.getSocialSecurityEmployeeTotal() != null ? d.getSocialSecurityEmployeeTotal() : BigDecimal.ZERO);
            totalMedW = totalMedW.add(medW);
            totalMedT = totalMedT.add(medT);
        }
        String summaryBody = "3"
                + employerEin
                + padLeft(formatCents(totalWages), 10)
                + padLeft(formatCents(totalFed), 10)
                + padLeft(formatCents(totalSS), 10)
                + padLeft(formatCents(totalMedW), 10)
                + padLeft(formatCents(totalMedT), 10);
        sb.append(formatRecord(summaryBody));

        // Record Type 9: Trailer
        String trailerBody = "9" + padLeft(String.valueOf(employees.size()), 6);
        sb.append(formatRecord(trailerBody));

        byte[] fileContent = sb.toString().getBytes(StandardCharsets.US_ASCII);
        uploadToS3(company, companyId, year, fileContent);


        return fileContent;
    }

    /** Formats to exactly 512 bytes: pads to 510 then CRLF */
    private String formatRecord(String body) {
        String padded = padRight(body, RECORD_LENGTH - 2);
        return padded + "\r\n";
    }

    private String padRight(String s, int len) {
        if (s == null) s = "";
        return String.format("%-" + len + "s", s);
    }

    private String padLeft(String s, int len) {
        if (s == null) s = "";
        return String.format("%" + len + "s", s);
    }

    /** Converts BigDecimal dollars to cents without decimal point */
    private String formatCents(BigDecimal value) {
        if (value == null) return "0";
        long cents = value.multiply(BigDecimal.valueOf(100)).longValue();
        return String.valueOf(cents);
    }

    private void uploadToS3(Company company, Integer companyId, int year, byte[] fileContent) {
        try {
            String companyKeyPart = company.getCompanyName()
                    .trim()
                    .replaceAll("[^A-Za-z0-9]+", "_");

            String fileName = String.format("EFW2_%d_%d.txt", companyId, year);

            String key = String.format("%s/%d/efw2/%d/%s",
                    companyKeyPart,
                    companyId,
                    year,
                    fileName
            );

            amazonS3Service.uploadPdfToS3(fileContent, key);
            log.info("Successfully uploaded EFW2 file to S3 with key: {}", key);

        } catch (Exception e) {
            log.error("Failed to upload EFW2 file to S3", e);
            // Don't throw exception, just log the error since file generation succeeded
        }
    }
}
