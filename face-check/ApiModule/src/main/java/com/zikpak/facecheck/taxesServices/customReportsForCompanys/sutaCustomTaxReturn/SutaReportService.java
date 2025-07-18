package com.zikpak.facecheck.taxesServices.customReportsForCompanys.sutaCustomTaxReturn;

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
public class SutaReportService {

    private final CompanyRepository companyRepository;
    private final EmployerTaxRecordRepository employerTaxRecordRepository;
    private final PaymentHistoryIrsRepository paymentHistoryRepository;
    private final UserRepository userRepository;

    // SUTA константы для NY
    private static final BigDecimal ANNUAL_WAGE_LIMIT = new BigDecimal("13000.00");
    private static final BigDecimal STANDARD_SUTA_RATE = new BigDecimal("0.006"); // 0.6% standard
    private static final BigDecimal QUARTERLY_THRESHOLD = new BigDecimal("500.00");

    /**
     * Генерирует квартальный SUTA отчет
     */
    public SutaReportDTO generateQuarterlySutaReport(Integer companyId, Integer year, Integer quarter) {
        log.info("📊 Генерируем квартальный SUTA отчет для компании {} Q{} {}", companyId, quarter, year);

        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new EntityNotFoundException("Компания не найдена: " + companyId));

        LocalDate[] quarterDates = getQuarterDates(year, quarter);
        LocalDate startDate = quarterDates[0];
        LocalDate endDate = quarterDates[1];

        // Получаем tax records за квартал
        List<EmployerTaxRecord> taxRecords = employerTaxRecordRepository
                .findByCompanyIdAndPeriodStartGreaterThanEqualAndPeriodEndLessThanEqual(companyId, startDate, endDate);

        // Считаем основные суммы
        SutaCalculationResult calculation = calculateSutaForPeriod(taxRecords, companyId, year, startDate);

        // Платежи за квартал
        BigDecimal totalPaid = paymentHistoryRepository.getTotalPaidForQuarterSUTA(companyId, quarter, year);

        // Строим employee details
        List<EmployeeSutaDTO> employeeDetails = buildEmployeeDetails(calculation.getEmployeeCalculations());

        // Company SUTA rate (из БД или дефолт)
        BigDecimal companySutaRate = Optional.ofNullable(company.getSocialSecurityTaxForCompany())
                .orElse(new BigDecimal("4.1"));

        return SutaReportDTO.builder()
                // Company info
                .companyId(company.getId())
                .companyName(company.getCompanyName())
                .companyAddress(company.getCompanyAddress())
                .companyCity(company.getCompanyCity())
                .companyState(company.getCompanyState())
                .companyZipCode(company.getCompanyZipCode())
                .companyPhone(company.getCompanyPhone())
                .employerEIN(company.getEmployerEIN())
                .sutaAccountNumber(generateSutaAccountNumber(company)) // может быть в отдельном поле

                // Period info
                .periodStart(startDate)
                .periodEnd(endDate)
                .reportType("Quarterly")
                .taxYear(year)
                .quarter(quarter)

                // SUTA calculations
                .totalGrossWages(calculation.getTotalGrossWages())
                .totalSutaWageBase(calculation.getTotalSutaWageBase())
                .totalSutaTaxOwed(calculation.getTotalSutaTax())
                .sutaRate(companySutaRate.divide(new BigDecimal("100"))) // из % в decimal
                .standardSutaRate(STANDARD_SUTA_RATE)
                .experienceRate(companySutaRate.divide(new BigDecimal("100")).subtract(STANDARD_SUTA_RATE))

                // Payment info
                .totalSutaTaxPaid(totalPaid)
                .remainingSutaLiability(calculation.getTotalSutaTax().subtract(totalPaid).max(BigDecimal.ZERO))
                .needsPayment(calculation.getTotalSutaTax().compareTo(QUARTERLY_THRESHOLD) > 0)

                // Employee data
                .totalEmployees(calculation.getTotalEmployees())
                .employeesSubjectToSuta(calculation.getEmployeesSubjectToSuta())
                .employeeDetails(employeeDetails)

                // Compliance
                .complianceStatus(isCompliant(calculation.getTotalSutaTax(), totalPaid))
                .nextPaymentDue(calculateNextPaymentDue(quarter, year))
                .notes(generateComplianceNotes(quarter, calculation.getTotalSutaTax(), companySutaRate))
                .build();
    }

    /**
     * Генерирует годовой SUTA отчет
     */
    public SutaReportDTO generateAnnualSutaReport(Integer companyId, Integer year) {
        log.info("📊 Генерируем годовой SUTA отчет для компании {} за {}", companyId, year);

        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new EntityNotFoundException("Компания не найдена: " + companyId));

        LocalDate startDate = LocalDate.of(year, 1, 1);
        LocalDate endDate = LocalDate.of(year, 12, 31);

        // Получаем все tax records за год
        List<EmployerTaxRecord> taxRecords = employerTaxRecordRepository
                .findByCompanyIdAndPeriodStartGreaterThanEqualAndPeriodEndLessThanEqual(companyId, startDate, endDate);

        // Считаем годовые суммы
        SutaCalculationResult annualCalculation = calculateSutaForPeriod(taxRecords, companyId, year,startDate);

        // Квартальная разбивка
        List<QuarterlySutaDTO> quarterlyBreakdown = calculateQuarterlyBreakdown(companyId, year);

        // Все платежи за год - пока нет метода в репозитории, добавим
        BigDecimal totalPaid = quarterlyBreakdown.stream()
                .map(QuarterlySutaDTO::getSutaTaxPaid)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Employee details с годовыми данными
        List<EmployeeSutaDTO> employeeDetails = buildEmployeeDetails(annualCalculation.getEmployeeCalculations());

        // Company SUTA rate
        BigDecimal companySutaRate = Optional.ofNullable(company.getSocialSecurityTaxForCompany())
                .orElse(new BigDecimal("4.1"));

        return SutaReportDTO.builder()
                // Company info
                .companyId(company.getId())
                .companyName(company.getCompanyName())
                .companyAddress(company.getCompanyAddress())
                .companyCity(company.getCompanyCity())
                .companyState(company.getCompanyState())
                .companyZipCode(company.getCompanyZipCode())
                .companyPhone(company.getCompanyPhone())
                .employerEIN(company.getEmployerEIN())
                .sutaAccountNumber(generateSutaAccountNumber(company))

                // Period info
                .periodStart(startDate)
                .periodEnd(endDate)
                .reportType("Annual")
                .taxYear(year)
                .quarter(null)

                // Annual SUTA totals
                .totalGrossWages(annualCalculation.getTotalGrossWages())
                .totalSutaWageBase(annualCalculation.getTotalSutaWageBase())
                .totalSutaTaxOwed(annualCalculation.getTotalSutaTax())
                .sutaRate(companySutaRate.divide(new BigDecimal("100")))
                .standardSutaRate(STANDARD_SUTA_RATE)
                .experienceRate(companySutaRate.divide(new BigDecimal("100")).subtract(STANDARD_SUTA_RATE))

                // Payment info
                .totalSutaTaxPaid(totalPaid)
                .remainingSutaLiability(annualCalculation.getTotalSutaTax().subtract(totalPaid).max(BigDecimal.ZERO))
                .needsPayment(annualCalculation.getTotalSutaTax().subtract(totalPaid).compareTo(BigDecimal.ONE) > 0)

                // Quarterly breakdown
                .quarterlyBreakdown(quarterlyBreakdown)

                // Employee data
                .totalEmployees(annualCalculation.getTotalEmployees())
                .employeesSubjectToSuta(annualCalculation.getEmployeesSubjectToSuta())
                .employeeDetails(employeeDetails)

                // Compliance
                .complianceStatus(isCompliant(annualCalculation.getTotalSutaTax(), totalPaid))
                .nextPaymentDue(LocalDate.of(year + 1, 1, 31)) // NYS-45 due date
                .notes(generateAnnualComplianceNotes(year, annualCalculation.getTotalSutaTax(), companySutaRate))
                .build();
    }

    // ========================================
    // PRIVATE HELPER METHODS
    // ========================================

    /**
     * Основной расчет SUTA (адаптирован из FUTA логики)
     */
    private SutaCalculationResult calculateSutaForPeriod(List<EmployerTaxRecord> taxRecords, Integer companyId, Integer year, LocalDate startDate) {

        if (taxRecords.isEmpty()) {
            return SutaCalculationResult.builder()
                    .totalGrossWages(BigDecimal.ZERO)
                    .totalSutaWageBase(BigDecimal.ZERO)
                    .totalSutaTax(BigDecimal.ZERO)
                    .totalEmployees(0)
                    .employeesSubjectToSuta(0)
                    .employeeCalculations(new ArrayList<>())
                    .build();
        }

        // Группируем по сотрудникам
        Map<Integer, List<EmployerTaxRecord>> recordsByEmployee = taxRecords.stream()
                .collect(Collectors.groupingBy(r -> r.getEmployee().getId()));

        List<EmployeeSutaCalculation> employeeCalculations = new ArrayList<>();
        BigDecimal totalGrossWages = BigDecimal.ZERO;
        BigDecimal totalSutaWageBase = BigDecimal.ZERO;
        BigDecimal totalSutaTax = BigDecimal.ZERO;
        int employeesSubjectToSuta = 0;

        for (Map.Entry<Integer, List<EmployerTaxRecord>> entry : recordsByEmployee.entrySet()) {
            User employee = entry.getValue().get(0).getEmployee();
            List<EmployerTaxRecord> employeeRecords = entry.getValue();

            // Gross wages для этого employee в периоде
            BigDecimal employeeGrossWages = employeeRecords.stream()
                    .map(r -> Optional.ofNullable(r.getGrossPay()).orElse(BigDecimal.ZERO))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);


            // Получаем YTD до начала периода
            BigDecimal ytdBeforeQuarter = employerTaxRecordRepository
                    .sumSutaTaxableWagesByEmployeeBeforeDate(employee.getId(), startDate);

            BigDecimal yearToDateSutaWages = ytdBeforeQuarter.add(employeeGrossWages);


            BigDecimal remainingLimit = ANNUAL_WAGE_LIMIT.subtract(ytdBeforeQuarter).max(BigDecimal.ZERO);
            BigDecimal sutaWageBase = employeeGrossWages.min(remainingLimit);

            Company company = companyRepository.findById(companyId).orElseThrow();
            BigDecimal sutaRate = Optional.ofNullable(company.getSocialSecurityTaxForCompany())
                    .orElse(new BigDecimal("4.1"));
            BigDecimal employeeSutaTax = sutaWageBase.multiply(sutaRate.divide(new BigDecimal("100"), 4, BigDecimal.ROUND_HALF_UP));

            boolean exceededLimit = remainingLimit.compareTo(BigDecimal.ZERO) == 0;

            EmployeeSutaCalculation empCalc = EmployeeSutaCalculation.builder()
                    .employee(employee)
                    .grossWages(employeeGrossWages)
                    .sutaWageBase(sutaWageBase)
                    .sutaTax(employeeSutaTax)
                    .yearToDateWages(yearToDateSutaWages)
                    .exceededLimit(exceededLimit)
                    .build();

            employeeCalculations.add(empCalc);

            // Накапливаем общие суммы
            totalGrossWages = totalGrossWages.add(employeeGrossWages);
            totalSutaWageBase = totalSutaWageBase.add(sutaWageBase);
            totalSutaTax = totalSutaTax.add(employeeSutaTax);

            if (sutaWageBase.compareTo(BigDecimal.ZERO) > 0) {
                employeesSubjectToSuta++;
            }
        }

        return SutaCalculationResult.builder()
                .totalGrossWages(totalGrossWages)
                .totalSutaWageBase(totalSutaWageBase)
                .totalSutaTax(totalSutaTax)
                .totalEmployees(recordsByEmployee.size())
                .employeesSubjectToSuta(employeesSubjectToSuta)
                .employeeCalculations(employeeCalculations)
                .build();
    }

    /**
     * Расчет SUTA wage base для одного employee
     */
    private BigDecimal calculateEmployeeSutaWageBase(BigDecimal periodWages, BigDecimal yearToDateWages) {
        // Wages до начала текущего периода
        BigDecimal previousYtdWages = yearToDateWages.subtract(periodWages);

        // Остаток лимита на начало периода
        BigDecimal remainingLimit = ANNUAL_WAGE_LIMIT.subtract(previousYtdWages);

        if (remainingLimit.compareTo(BigDecimal.ZERO) <= 0) {
            // Уже превысил лимит в прошлых периодах
            return BigDecimal.ZERO;
        }

        // SUTA wage base = min(period wages, remaining limit)
        return periodWages.min(remainingLimit);
    }

    /**
     * Квартальная разбивка для годового отчета
     */
    private List<QuarterlySutaDTO> calculateQuarterlyBreakdown(Integer companyId, Integer year) {
        List<QuarterlySutaDTO> quarterlyData = new ArrayList<>();

        for (int quarter = 1; quarter <= 4; quarter++) {
            LocalDate[] quarterDates = getQuarterDates(year, quarter);
            LocalDate startDate = quarterDates[0];
            LocalDate endDate = quarterDates[1];

            // Tax records за квартал
            List<EmployerTaxRecord> quarterRecords = employerTaxRecordRepository
                    .findByCompanyIdAndPeriodStartGreaterThanEqualAndPeriodEndLessThanEqual(companyId, startDate, endDate);

            SutaCalculationResult quarterResult = calculateSutaForPeriod(quarterRecords, companyId, year, startDate);

            // Платежи за квартал
            BigDecimal quarterPaid = paymentHistoryRepository.getTotalPaidForQuarterSUTA(companyId, quarter, year);

            quarterlyData.add(QuarterlySutaDTO.builder()
                    .quarter(quarter)
                    .quarterStart(startDate)
                    .quarterEnd(endDate)
                    .grossWages(quarterResult.getTotalGrossWages())
                    .sutaWageBase(quarterResult.getTotalSutaWageBase())
                    .sutaTaxOwed(quarterResult.getTotalSutaTax())
                    .sutaTaxPaid(quarterPaid)
                    .isLiable(quarterResult.getTotalSutaTax().compareTo(QUARTERLY_THRESHOLD) > 0)
                    .build());
        }

        return quarterlyData;
    }

    private List<EmployeeSutaDTO> buildEmployeeDetails(List<EmployeeSutaCalculation> calculations) {
        return calculations.stream()
                .map(calc -> EmployeeSutaDTO.builder()
                        .employeeName(calc.getEmployee().getFirstName() + " " + calc.getEmployee().getLastName())
                        .grossWages(calc.getGrossWages())
                        .sutaWageBase(calc.getSutaWageBase())
                        .sutaTax(calc.getSutaTax())
                        .exceededLimit(calc.getExceededLimit())
                        .build())
                .sorted(Comparator.comparing(EmployeeSutaDTO::getGrossWages).reversed())
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
        // NY SUTA quarterly due dates
        switch (quarter) {
            case 1: return LocalDate.of(year, 4, 30);
            case 2: return LocalDate.of(year, 7, 31);
            case 3: return LocalDate.of(year, 10, 31);
            case 4: return LocalDate.of(year + 1, 1, 31);
            default: throw new IllegalArgumentException("Invalid quarter");
        }
    }

    private List<String> generateComplianceNotes(Integer quarter, BigDecimal taxOwed, BigDecimal sutaRate) {
        List<String> notes = new ArrayList<>();
        notes.add("NY SUTA rate: " + sutaRate + "% (company-specific experience rate)");
        notes.add("Applied to first $13,000 of wages per employee per year");

        if (taxOwed.compareTo(QUARTERLY_THRESHOLD) > 0) {
            notes.add("⚠️ Quarterly payment required - liability exceeds $500");
        } else {
            notes.add("✅ No quarterly payment required - liability under $500");
        }

        notes.add("File quarterly return using NYS-45 form");
        notes.add("New employers start with standard rate, then get experience rating");
        return notes;
    }

    private List<String> generateAnnualComplianceNotes(Integer year, BigDecimal taxOwed, BigDecimal sutaRate) {
        List<String> notes = new ArrayList<>();
        notes.add("Annual NYS-45 reconciliation due January 31, " + (year + 1));
        notes.add("All quarterly SUTA payments must be reconciled");
        notes.add("Current company experience rate: " + sutaRate + "%");
        notes.add("Experience rate may change based on claims history");

        if (taxOwed.compareTo(BigDecimal.ZERO) > 0) {
            notes.add("⚠️ Review quarterly payments to ensure full compliance");
        } else {
            notes.add("✅ No SUTA liability for this year");
        }

        notes.add("Maintain payroll records for NY State audit purposes");
        return notes;
    }

    private String generateSutaAccountNumber(Company company) {
        // Генерируем account number на основе EIN или создаем фиктивный
        if (company.getEmployerEIN() != null) {
            return "NY-" + company.getEmployerEIN().replaceAll("[^0-9]", "").substring(0, 6);
        }
        return "NY-" + String.format("%06d", company.getId());
    }
}