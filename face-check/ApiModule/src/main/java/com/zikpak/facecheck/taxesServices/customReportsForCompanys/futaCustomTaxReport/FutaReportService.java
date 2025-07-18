package com.zikpak.facecheck.taxesServices.customReportsForCompanys.futaCustomTaxReport;

import com.zikpak.facecheck.entity.Company;
import com.zikpak.facecheck.entity.EmployerTaxRecord;
import com.zikpak.facecheck.entity.User;
import com.zikpak.facecheck.repository.CompanyRepository;
import com.zikpak.facecheck.repository.EmployerTaxRecordRepository;
import com.zikpak.facecheck.repository.PaymentHistoryIrsRepository;
import com.zikpak.facecheck.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class FutaReportService {

    private final CompanyRepository companyRepository;
    private final EmployerTaxRecordRepository employerTaxRecordRepository;
    private final PaymentHistoryIrsRepository paymentHistoryRepository;
    private final UserRepository userRepository;

    // FUTA константы
    private static final BigDecimal ANNUAL_WAGE_LIMIT = new BigDecimal("7000.00");
    private static final BigDecimal STANDARD_FUTA_RATE = new BigDecimal("0.006"); // 0.6%
    private static final BigDecimal NY_CREDIT_REDUCTION = new BigDecimal("0.003"); // 0.3%
    private static final BigDecimal QUARTERLY_THRESHOLD = new BigDecimal("500.00");

    /**
     * Генерирует квартальный FUTA отчет
     */
    public FutaReportDTO generateQuarterlyFutaReport(Integer companyId, Integer year, Integer quarter) {
        log.info("📊 Генерируем квартальный FUTA отчет для компании {} Q{} {}", companyId, quarter, year);

        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new EntityNotFoundException("Компания не найдена: " + companyId));

        LocalDate[] quarterDates = getQuarterDates(year, quarter);
        LocalDate startDate = quarterDates[0];
        LocalDate endDate = quarterDates[1];

        // Получаем tax records за квартал
        List<EmployerTaxRecord> taxRecords = employerTaxRecordRepository
                .findByCompanyIdAndPeriodStartGreaterThanEqualAndPeriodEndLessThanEqual(companyId, startDate, endDate);

        // Считаем основные суммы
        FutaCalculationResult calculation = calculateFutaForPeriod(taxRecords, companyId, year,startDate);

        // Платежи за квартал
        BigDecimal totalPaid = paymentHistoryRepository.getTotalPaidForQuarterFUTA(companyId, quarter, year);

        // Строим employee details
        List<EmployeeFutaDTO> employeeDetails = buildEmployeeDetails(calculation.getEmployeeCalculations());

        return FutaReportDTO.builder()
                // Company info
                .companyId(company.getId())
                .companyName(company.getCompanyName())
                .companyAddress(company.getCompanyAddress())
                .companyCity(company.getCompanyCity())
                .companyState(company.getCompanyState())
                .companyZipCode(company.getCompanyZipCode())
                .companyPhone(company.getCompanyPhone())
                .employerEIN(company.getEmployerEIN())

                // Period info
                .periodStart(startDate)
                .periodEnd(endDate)
                .reportType("Quarterly")
                .taxYear(year)
                .quarter(quarter)

                // FUTA calculations
                .totalGrossWages(calculation.getTotalGrossWages())
                .totalFutaWageBase(calculation.getTotalFutaWageBase())
                .totalFutaTaxOwed(calculation.getTotalFutaTax())
                .futaRate(STANDARD_FUTA_RATE)
                .nyCreditReduction(NY_CREDIT_REDUCTION)
                .effectiveFutaRate(STANDARD_FUTA_RATE.add(NY_CREDIT_REDUCTION))

                // Payment info
                .totalFutaTaxPaid(totalPaid)
                .remainingFutaLiability(calculation.getTotalFutaTax().subtract(totalPaid).max(BigDecimal.ZERO))
                .needsPayment(calculation.getTotalFutaTax().compareTo(QUARTERLY_THRESHOLD) > 0)

                // Employee data
                .totalEmployees(calculation.getTotalEmployees())
                .employeesSubjectToFuta(calculation.getEmployeesSubjectToFuta())
                .employeeDetails(employeeDetails)

                // Compliance
                .complianceStatus(isCompliant(calculation.getTotalFutaTax(), totalPaid))
                .nextPaymentDue(calculateNextPaymentDue(quarter, year))
                .notes(generateComplianceNotes(quarter, calculation.getTotalFutaTax()))
                .build();
    }

    /**
     * Генерирует годовой FUTA отчет
     */
    public FutaReportDTO generateAnnualFutaReport(Integer companyId, Integer year) {
        log.info("📊 Генерируем годовой FUTA отчет для компании {} за {}", companyId, year);

        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new EntityNotFoundException("Компания не найдена: " + companyId));

        LocalDate startDate = LocalDate.of(year, 1, 1);
        LocalDate endDate = LocalDate.of(year, 12, 31);

        // Получаем все tax records за год
        List<EmployerTaxRecord> taxRecords = employerTaxRecordRepository
                .findByCompanyIdAndPeriodStartGreaterThanEqualAndPeriodEndLessThanEqual(companyId, startDate, endDate);

        // Считаем годовые суммы
        FutaCalculationResult annualCalculation = calculateFutaForPeriod(taxRecords, companyId, year,startDate);

        // Квартальная разбивка
        List<QuarterlyFutaDTO> quarterlyBreakdown = calculateQuarterlyBreakdown(companyId, year);

        // Все платежи за год
        BigDecimal totalPaid = paymentHistoryRepository.getTotalPaidForFUTA(companyId, year);

        // Employee details с годовыми данными
        List<EmployeeFutaDTO> employeeDetails = buildEmployeeDetails(annualCalculation.getEmployeeCalculations());

        return FutaReportDTO.builder()
                // Company info (same as quarterly)
                .companyId(company.getId())
                .companyName(company.getCompanyName())
                .companyAddress(company.getCompanyAddress())
                .companyCity(company.getCompanyCity())
                .companyState(company.getCompanyState())
                .companyZipCode(company.getCompanyZipCode())
                .companyPhone(company.getCompanyPhone())
                .employerEIN(company.getEmployerEIN())

                // Period info
                .periodStart(startDate)
                .periodEnd(endDate)
                .reportType("Annual")
                .taxYear(year)
                .quarter(null)

                // Annual FUTA totals
                .totalGrossWages(annualCalculation.getTotalGrossWages())
                .totalFutaWageBase(annualCalculation.getTotalFutaWageBase())
                .totalFutaTaxOwed(annualCalculation.getTotalFutaTax())
                .futaRate(STANDARD_FUTA_RATE)
                .nyCreditReduction(NY_CREDIT_REDUCTION)
                .effectiveFutaRate(STANDARD_FUTA_RATE.add(NY_CREDIT_REDUCTION))

                // Payment info
                .totalFutaTaxPaid(totalPaid)
                .remainingFutaLiability(annualCalculation.getTotalFutaTax().subtract(totalPaid).max(BigDecimal.ZERO))
                .needsPayment(annualCalculation.getTotalFutaTax().subtract(totalPaid).compareTo(BigDecimal.ONE) > 0)

                // Quarterly breakdown
                .quarterlyBreakdown(quarterlyBreakdown)

                // Employee data
                .totalEmployees(annualCalculation.getTotalEmployees())
                .employeesSubjectToFuta(annualCalculation.getEmployeesSubjectToFuta())
                .employeeDetails(employeeDetails)

                // Compliance
                .complianceStatus(isCompliant(annualCalculation.getTotalFutaTax(), totalPaid))
                .nextPaymentDue(LocalDate.of(year + 1, 1, 31)) // Form 940 due date
                .notes(generateAnnualComplianceNotes(year, annualCalculation.getTotalFutaTax()))
                .build();
    }

    // ========================================
    // PRIVATE HELPER METHODS
    // ========================================

    /**
     * Основной расчет FUTA (использует логику из Form940PdfGeneratorService)
     */
    private FutaCalculationResult calculateFutaForPeriod(List<EmployerTaxRecord> taxRecords, Integer companyId, Integer year, LocalDate startDate) {

        if (taxRecords.isEmpty()) {
            return FutaCalculationResult.builder()
                    .totalGrossWages(BigDecimal.ZERO)
                    .totalFutaWageBase(BigDecimal.ZERO)
                    .totalFutaTax(BigDecimal.ZERO)
                    .totalEmployees(0)
                    .employeesSubjectToFuta(0)
                    .employeeCalculations(new ArrayList<>())
                    .build();
        }

        // Группируем по сотрудникам
        Map<Integer, List<EmployerTaxRecord>> recordsByEmployee = taxRecords.stream()
                .collect(Collectors.groupingBy(r -> r.getEmployee().getId()));

        List<EmployeeFutaCalculation> employeeCalculations = new ArrayList<>();
        BigDecimal totalGrossWages = BigDecimal.ZERO;
        BigDecimal totalFutaWageBase = BigDecimal.ZERO;
        BigDecimal totalFutaTax = BigDecimal.ZERO;
        int employeesSubjectToFuta = 0;

        for (Map.Entry<Integer, List<EmployerTaxRecord>> entry : recordsByEmployee.entrySet()) {
            User employee = entry.getValue().get(0).getEmployee();
            List<EmployerTaxRecord> employeeRecords = entry.getValue();

            // Gross wages для этого employee в периоде
            BigDecimal employeeGrossWages = employeeRecords.stream()
                    .map(r -> Optional.ofNullable(r.getGrossPay()).orElse(BigDecimal.ZERO))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);



            BigDecimal ytdBeforePeriod = employerTaxRecordRepository
                    .sumGrossPayByEmployeeBeforeDate(employee.getId(), startDate);
            BigDecimal futaWageBase = calculateEmployeeFutaWageBase(employeeGrossWages, ytdBeforePeriod);


            // FUTA tax с учетом NY credit reduction
            BigDecimal effectiveRate = STANDARD_FUTA_RATE.add(NY_CREDIT_REDUCTION);
            BigDecimal employeeFutaTax = futaWageBase.multiply(effectiveRate);

            // Проверяем превышение лимита
            boolean exceededLimit = ytdBeforePeriod.add(employeeGrossWages).compareTo(ANNUAL_WAGE_LIMIT) >= 0;

            EmployeeFutaCalculation empCalc = EmployeeFutaCalculation.builder()
                    .employee(employee)
                    .grossWages(employeeGrossWages)
                    .futaWageBase(futaWageBase)
                    .futaTax(employeeFutaTax)
                    .yearToDateWages(ytdBeforePeriod.add(employeeGrossWages))
                    .exceededLimit(exceededLimit)
                    .build();

            employeeCalculations.add(empCalc);

            // Накапливаем общие суммы
            totalGrossWages = totalGrossWages.add(employeeGrossWages);
            totalFutaWageBase = totalFutaWageBase.add(futaWageBase);
            totalFutaTax = totalFutaTax.add(employeeFutaTax);

            if (futaWageBase.compareTo(BigDecimal.ZERO) > 0) {
                employeesSubjectToFuta++;
            }
        }

        return FutaCalculationResult.builder()
                .totalGrossWages(totalGrossWages)
                .totalFutaWageBase(totalFutaWageBase)
                .totalFutaTax(totalFutaTax)
                .totalEmployees(recordsByEmployee.size())
                .employeesSubjectToFuta(employeesSubjectToFuta)
                .employeeCalculations(employeeCalculations)
                .build();
    }

    /**
     * Расчет FUTA wage base для одного employee (логика из Form940PdfGeneratorService)
     */
    private BigDecimal calculateEmployeeFutaWageBase(BigDecimal periodWages, BigDecimal yearToDateWages) {
        // Остаток лимита на начало периода
        BigDecimal remainingLimit = ANNUAL_WAGE_LIMIT.subtract(yearToDateWages);

        if (remainingLimit.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }
        return periodWages.min(remainingLimit);
    }


    /**
     * Квартальная разбивка для годового отчета
     */
    private List<QuarterlyFutaDTO> calculateQuarterlyBreakdown(Integer companyId, Integer year) {
        List<QuarterlyFutaDTO> quarterlyData = new ArrayList<>();

        for (int quarter = 1; quarter <= 4; quarter++) {
            LocalDate[] quarterDates = getQuarterDates(year, quarter);
            LocalDate startDate = quarterDates[0];
            LocalDate endDate = quarterDates[1];

            // Tax records за квартал
            List<EmployerTaxRecord> quarterRecords = employerTaxRecordRepository
                    .findByCompanyIdAndPeriodStartGreaterThanEqualAndPeriodEndLessThanEqual(companyId, startDate, endDate);

            FutaCalculationResult quarterResult = calculateFutaForPeriod(quarterRecords, companyId, year, startDate);

            // Платежи за квартал
            BigDecimal quarterPaid = paymentHistoryRepository.getTotalPaidForQuarterFUTA(companyId, quarter, year);

            quarterlyData.add(QuarterlyFutaDTO.builder()
                    .quarter(quarter)
                    .quarterStart(startDate)
                    .quarterEnd(endDate)
                    .grossWages(quarterResult.getTotalGrossWages())
                    .futaWageBase(quarterResult.getTotalFutaWageBase())
                    .futaTaxOwed(quarterResult.getTotalFutaTax())
                    .futaTaxPaid(quarterPaid)
                    .isLiable(quarterResult.getTotalFutaTax().compareTo(QUARTERLY_THRESHOLD) > 0)
                    .build());
        }

        return quarterlyData;
    }

    private List<EmployeeFutaDTO> buildEmployeeDetails(List<EmployeeFutaCalculation> calculations) {
        return calculations.stream()
                .map(calc -> EmployeeFutaDTO.builder()
                        .employeeName(calc.getEmployee().getFirstName() + " " + calc.getEmployee().getLastName())
                        .grossWages(calc.getGrossWages())
                        .futaWageBase(calc.getFutaWageBase())
                        .futaTax(calc.getFutaTax())
                        .exceededLimit(calc.getExceededLimit())
                        .build())
                .sorted(Comparator.comparing(EmployeeFutaDTO::getGrossWages).reversed())
                .collect(Collectors.toList());
    }

    private LocalDate[] getQuarterDates(int year, int quarter) {
        switch (quarter) {
            case 1: return new LocalDate[]{LocalDate.of(year, 1, 1), LocalDate.of(year, 3, 31)};
            case 2: return new LocalDate[]{LocalDate.of(year, 4, 1), LocalDate.of(year, 6, 30)};
            case 3: return new LocalDate[]{LocalDate.of(year, 7, 1), LocalDate.of(year, 9, 30)};
            case 4: return new LocalDate[]{LocalDate.of(year, 10, 1), LocalDate.of(year, 12, 31)};
            default: throw new IllegalArgumentException("Invalid quarter: " + quarter);
        }
    }

    private Boolean isCompliant(BigDecimal taxOwed, BigDecimal taxPaid) {
        BigDecimal difference = taxOwed.subtract(taxPaid);
        return difference.compareTo(BigDecimal.ONE) <= 0; // $1 tolerance
    }

    private LocalDate calculateNextPaymentDue(Integer quarter, Integer year) {
        switch (quarter) {
            case 1: return LocalDate.of(year, 4, 30);
            case 2: return LocalDate.of(year, 7, 31);
            case 3: return LocalDate.of(year, 10, 31);
            case 4: return LocalDate.of(year + 1, 1, 31);
            default: throw new IllegalArgumentException("Invalid quarter");
        }
    }

    private List<String> generateComplianceNotes(Integer quarter, BigDecimal taxOwed) {
        List<String> notes = new ArrayList<>();
        notes.add("FUTA rate: 0.6% + 0.3% NY credit reduction = 0.9% effective rate");
        notes.add("Applied to first $7,000 of wages per employee per year");

        if (taxOwed.compareTo(QUARTERLY_THRESHOLD) > 0) {
            notes.add("⚠️ Quarterly payment required - liability exceeds $500");
        } else {
            notes.add("✅ No quarterly payment required - liability under $500");
        }

        notes.add("NY is a credit reduction state - additional 0.3% applies");
        return notes;
    }

    private List<String> generateAnnualComplianceNotes(Integer year, BigDecimal taxOwed) {
        List<String> notes = new ArrayList<>();
        notes.add("Form 940 must be filed by January 31, " + (year + 1));
        notes.add("All quarterly FUTA payments must be reconciled");
        notes.add("NY State credit reduction: +0.3% to standard 0.6% rate");
        notes.add("Maintain payroll records for at least 4 years");

        if (taxOwed.compareTo(BigDecimal.ZERO) > 0) {
            notes.add("⚠️ Review quarterly payments to ensure full compliance");
        } else {
            notes.add("✅ No FUTA liability for this year");
        }

        return notes;
    }
}