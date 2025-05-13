package com.zikpak.facecheck.taxesServices.ASCIIservices;

import com.zikpak.facecheck.entity.Company;
import com.zikpak.facecheck.entity.User;
import com.zikpak.facecheck.helperServices.WorkerPayRollService;
import com.zikpak.facecheck.repository.CompanyRepository;
import com.zikpak.facecheck.repository.UserRepository;
import com.zikpak.facecheck.requestsResponses.finance.WorkerYearlySummaryDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public  class EFW2GeneratorService {
    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;
    private final WorkerPayRollService workerPayRollService;

    /**
     * Generates the electronic EFW2 file as ASCII bytes.
     * @param companyId ID of the company (from which we fetch EIN and employees)
     * @param year Tax year (e.g. 2025)
     * @return ASCII-encoded EFW2 file ready for SSA BSO upload
     */
    public byte[] generateEfw2File(Integer companyId, int year) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new RuntimeException("Company not found: " + companyId));
        String rawEin = company.getEmployerEIN() != null ? company.getEmployerEIN().replaceAll("\\D", "") : "";
        // Pad EIN to 9 digits (leading zeros if needed)
        String employerEin = padLeft(rawEin, 9).replace(' ', '0');
        if (employerEin.length() != 9) {
            log.warn("Normalized EIN length not 9 for company {}: {}", companyId, rawEin);
        }

        List<User> employees = userRepository.findAllByCompanyId(companyId);
        LocalDate today = LocalDate.now();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyyMMdd");

        StringBuilder sb = new StringBuilder();

        // Record Type 1: File Header
        String rec1 = "1"
                + padRight(employerEin, 9)      // EIN
                + fmt.format(today)            // File date YYYYMMDD
                + padRight(String.valueOf(year), 4)  // Tax year
                + padRight("EFW2", 4);       // File type
        rec1 = padRight(rec1, 511) + "\n";
        sb.append(rec1);

        // Record Type 2: One line per employee (W-2 detail)
        for (User user : employees) {
            WorkerYearlySummaryDto dto = workerPayRollService.calculateWorkerYearlyTotals(user.getId(), year);
            String rawSsn = user.getSSN_WORKER() != null
                    ? user.getSSN_WORKER().replaceAll("\\D", "")
                    : "";
            String ssn = padLeft(rawSsn, 9).replace(' ', '0');
            if (ssn.length() != 9) {
                log.warn("Normalized SSN length not 9 for user {}: {}", user.getId(), rawSsn);
            }

            String rec2Body = "2"
                    + padRight(ssn, 9)
                    + padRight(employerEin, 9)
                    + padLeft(formatAmount(dto.getGrossPayTotal()), 10)
                    + padLeft(formatAmount(dto.getFederalWithholdingTotal()), 10)
                    + padLeft(formatAmount(dto.getSocialSecurityEmployeeTotal()), 10)
                    + padLeft(formatAmount(dto.getMedicareTotal()), 10)
                    + padLeft(formatAmount(dto.getMedicareTotal()), 10);

            // Правильно добавляем символ перевода строки:
            String rec2 = padRight(rec2Body, 511) + "\n";
            sb.append(rec2);
        }



        // Record Type 3: W-3 summary (totals)
        BigDecimal totalW = BigDecimal.ZERO;
        BigDecimal totalF = BigDecimal.ZERO;
        BigDecimal totalS = BigDecimal.ZERO;
        BigDecimal totalM = BigDecimal.ZERO;
        for (User u : employees) {
            WorkerYearlySummaryDto d = workerPayRollService.calculateWorkerYearlyTotals(u.getId(), year);
            totalW = totalW.add(d.getGrossPayTotal() != null ? d.getGrossPayTotal() : BigDecimal.ZERO);
            totalF = totalF.add(d.getFederalWithholdingTotal() != null ? d.getFederalWithholdingTotal() : BigDecimal.ZERO);
            totalS = totalS.add(d.getSocialSecurityEmployeeTotal() != null ? d.getSocialSecurityEmployeeTotal() : BigDecimal.ZERO);
            totalM = totalM.add(d.getMedicareTotal() != null ? d.getMedicareTotal() : BigDecimal.ZERO);
        }
        String rec3 = "3"
                + padRight(employerEin, 9)
                + padLeft(formatAmount(totalW), 10)
                + padLeft(formatAmount(totalF), 10)
                + padLeft(formatAmount(totalS), 10)
                + padLeft(formatAmount(totalM), 10)
                + padRight("", 466);
        rec3 = padRight(rec3, 511) + "\n";
        sb.append(rec3);

        String rec9 = "9"
                + padLeft(String.valueOf(employees.size()), 6)
                + padRight("", 494);
        rec9 = padRight(rec9, 511) + "\n";
        sb.append(rec9);

        return sb.toString().getBytes(StandardCharsets.US_ASCII);
    }

    private String padRight(String s, int len) {
        if (s == null) s = "";
        return String.format("%-" + len + "s", s);
    }

    private String padLeft(String s, int len) {
        if (s == null) s = "";
        return String.format("%" + len + "s", s);
    }

    private String formatAmount(BigDecimal value) {
        if (value == null) return "0";
        long cents = value.multiply(BigDecimal.valueOf(100)).longValue();
        return String.valueOf(cents);
    }
}