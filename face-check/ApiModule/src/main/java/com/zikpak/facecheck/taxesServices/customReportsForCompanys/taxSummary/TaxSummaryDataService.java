package com.zikpak.facecheck.taxesServices.customReportsForCompanys.taxSummary;
import com.zikpak.facecheck.entity.*;
import com.zikpak.facecheck.entity.employee.WorkerPayroll;
import com.zikpak.facecheck.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaxSummaryDataService {

    private final EmployerTaxRecordRepository employerTaxRecordRepository;
    private final PaymentHistoryIrsRepository paymentHistoryRepository;
    private final CompanyRepository companyRepository;
    private final WorkerPayrollRepository workerPayrollRepository;


    public TaxSummaryReportDTO generateTaxSummaryReport(
            Integer companyId, LocalDate startDate, LocalDate endDate) {

        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new RuntimeException("Company not found"));

        // Определяем тип отчета
        String reportType = determineReportType(startDate, endDate);
        Integer quarter = getQuarter(startDate);
        Integer taxYear = startDate.getYear();

        // Получаем все налоговые записи за период
        List<EmployerTaxRecord> taxRecords = employerTaxRecordRepository
                .findByCompanyIdAndPeriodStartBetween(companyId, startDate, endDate);

        List<WorkerPayroll> payrolls = workerPayrollRepository
                .findAllByCompanyIdAndPeriodBetween(companyId, startDate, endDate);


        // Вычисляем итоговые суммы
        TaxSummaryTotals totals = calculateTaxSummaryTotals(taxRecords, payrolls);

        // Создаем breakdown по типам налогов
        List<TaxBreakdownDTO> taxBreakdown = createTaxBreakdown(taxRecords);

        // Создаем employee summary
        List<EmployeeTaxSummaryDTO> employeeSummary = createEmployeeTaxSummary(taxRecords, payrolls);

        // Получаем статус платежей
        PaymentStatus paymentStatus = getPaymentStatus(companyId, taxYear, quarter, totals, startDate, endDate);

        // Определяем требуемые формы
        List<String> formsRequired = determineRequiredForms(reportType, taxYear, quarter);

        return TaxSummaryReportDTO.builder()
                // Company Info
                .companyId(company.getId())
                .companyName(company.getCompanyName())
                .companyAddress(company.getCompanyAddress())
                .companyCity(company.getCompanyCity())
                .companyState(company.getCompanyState())
                .companyZipCode(company.getCompanyZipCode())
                .companyPhone(company.getCompanyPhone())
                .employerEIN(company.getEmployerEIN())

                // Period Info
                .periodStart(startDate)
                .periodEnd(endDate)
                .reportType(reportType)
                .taxYear(taxYear)
                .quarter(quarter)

                // Summary Totals
                .totalGrossWages(totals.getTotalGrossWages())
                .totalTaxableWages(totals.getTotalTaxableWages())
                .totalFederalTaxWithheld(totals.getTotalFederalTax())
                .totalSocialSecurityTax(totals.getTotalSocialSecurityTax())
                .totalMedicareTax(totals.getTotalMedicareTax())
                .totalStateTaxWithheld(totals.getTotalStateTax())
                .totalLocalTaxWithheld(totals.getTotalLocalTax())
                .totalFUTATax(totals.getTotalFUTATax())
                .totalSUTATax(totals.getTotalSUTATax())
                .totalEmployerTaxes(totals.getTotalEmployerTaxes())
                .totalEmployeeTaxes(totals.getTotalEmployeeTaxes())
                .totalTaxLiability(totals.getTotalTaxLiability())

                // Employee Info
                .totalEmployees(totals.getTotalEmployees())
                .activeEmployees(totals.getActiveEmployees())

                // Breakdowns
                .taxBreakdown(taxBreakdown)
                .employeeTaxSummary(employeeSummary)

                // Payment Status
                .totalTaxesPaid(paymentStatus.getTotalPaid())
                .remainingTaxLiability(paymentStatus.getRemainingLiability())
                .complianceStatus(paymentStatus.isCompliant())

                // Forms
                .formsRequired(formsRequired)
                .build();
    }


    private TaxSummaryTotals calculateTaxSummaryTotals(List<EmployerTaxRecord> taxRecords,
                                                       List<WorkerPayroll> payrolls) {
        if (taxRecords.isEmpty()) {
            return TaxSummaryTotals.builder()
                    .totalGrossWages(BigDecimal.ZERO)
                    .totalTaxableWages(BigDecimal.ZERO)
                    .totalFederalTax(BigDecimal.ZERO)
                    .totalSocialSecurityTax(BigDecimal.ZERO)
                    .totalMedicareTax(BigDecimal.ZERO)
                    .totalStateTax(BigDecimal.ZERO)
                    .totalLocalTax(BigDecimal.ZERO)
                    .totalFUTATax(BigDecimal.ZERO)
                    .totalSUTATax(BigDecimal.ZERO)
                    .totalEmployerTaxes(BigDecimal.ZERO)
                    .totalEmployeeTaxes(BigDecimal.ZERO)
                    .totalTaxLiability(BigDecimal.ZERO)
                    .totalEmployees(0)
                    .activeEmployees(0)
                    .build();
        }

        // 1) Общая валовая
        BigDecimal totalGrossWages = taxRecords.stream()
                .map(r -> Optional.ofNullable(r.getGrossPay()).orElse(BigDecimal.ZERO))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 2) Базы для расчёта налогов
        BigDecimal totalSsBase = taxRecords.stream()
                .map(r -> Optional.ofNullable(r.getSocialSecurityTaxableWages()).orElse(BigDecimal.ZERO))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalMedicareBase = taxRecords.stream()
                .map(r -> Optional.ofNullable(r.getMedicareTaxableWages()).orElse(BigDecimal.ZERO)
                        .add(Optional.ofNullable(r.getAdditionalMedicareWages()).orElse(BigDecimal.ZERO)))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalFutaBase = taxRecords.stream()
                .map(r -> Optional.ofNullable(r.getFutaTaxableWages()).orElse(BigDecimal.ZERO))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalSutaBase = taxRecords.stream()
                .map(r -> Optional.ofNullable(r.getSutaTaxableWages()).orElse(BigDecimal.ZERO))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 3) Собираем сами суммы налогов (как раньше)
        BigDecimal totalFederalTax = taxRecords.stream()
                .map(r -> Optional.ofNullable(r.getFederalWithholding()).orElse(BigDecimal.ZERO))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalSocialSecurityTax = taxRecords.stream()
                .map(r -> Optional.ofNullable(r.getSocialSecurityTax()).orElse(BigDecimal.ZERO))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalMedicareTax = taxRecords.stream()
                .map(r -> Optional.ofNullable(r.getMedicareTax()).orElse(BigDecimal.ZERO))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalFUTATax = taxRecords.stream()
                .map(r -> Optional.ofNullable(r.getFutaTax()).orElse(BigDecimal.ZERO))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalSUTATax = taxRecords.stream()
                .map(r -> Optional.ofNullable(r.getSutaTax()).orElse(BigDecimal.ZERO))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 4) State & local (если у вас есть такие поля, иначе оставляем 0)
        BigDecimal totalStateTax = payrolls.stream()
                .map(p -> Optional.ofNullable(p.getNyStateWithholding()).orElse(BigDecimal.ZERO))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalLocalTax = payrolls.stream()
                .map(p -> Optional.ofNullable(p.getNyLocalWithholding()).orElse(BigDecimal.ZERO))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 5) Employer match для SS & Medicare
        BigDecimal employerSsTax     = totalSocialSecurityTax;
        BigDecimal employerMcrTax    = totalMedicareTax;

        BigDecimal totalEmployerTaxes = employerSsTax
                .add(employerMcrTax)
                .add(totalFUTATax)
                .add(totalSUTATax);

        BigDecimal totalEmployeeTaxes = totalFederalTax
                .add(totalSocialSecurityTax)
                .add(totalMedicareTax)
                .add(totalStateTax)
                .add(totalLocalTax);

        BigDecimal totalTaxLiability = totalEmployerTaxes.add(totalEmployeeTaxes);

        Set<Integer> uniqueEmployees = taxRecords.stream()
                .map(r -> r.getEmployee().getId())
                .collect(Collectors.toSet());

        return TaxSummaryTotals.builder()
                .totalGrossWages(totalGrossWages)
                .totalTaxableWages(totalGrossWages)
                .totalFederalTax(totalFederalTax)
                .totalSocialSecurityTax(totalSocialSecurityTax)
                .totalMedicareTax(totalMedicareTax)
                .totalStateTax(totalStateTax)
                .totalLocalTax(totalLocalTax)
                .totalFUTATax(totalFUTATax)
                .totalSUTATax(totalSUTATax)
                .totalEmployerTaxes(totalEmployerTaxes)
                .totalEmployeeTaxes(totalEmployeeTaxes)
                .totalTaxLiability(totalTaxLiability)
                .totalEmployees(uniqueEmployees.size())
                .activeEmployees(uniqueEmployees.size())
                .build();
    }

    private List<TaxBreakdownDTO> createTaxBreakdown(List<EmployerTaxRecord> taxRecords) {
        List<TaxBreakdownDTO> breakdown = new ArrayList<>();

        BigDecimal totalSocialSecurity = taxRecords.stream()
                .map(r -> Optional.ofNullable(r.getSocialSecurityTax()).orElse(BigDecimal.ZERO))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalMedicare = taxRecords.stream()
                .map(r -> Optional.ofNullable(r.getMedicareTax()).orElse(BigDecimal.ZERO))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalFUTA = taxRecords.stream()
                .map(r -> Optional.ofNullable(r.getFutaTax()).orElse(BigDecimal.ZERO))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalSUTA = taxRecords.stream()
                .map(r -> Optional.ofNullable(r.getSutaTax()).orElse(BigDecimal.ZERO))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Social Security (6.2% employee + 6.2% employer)
        breakdown.add(TaxBreakdownDTO.builder()
                .taxType("Social Security Tax")
                .employerPortion(totalSocialSecurity)
                .employeePortion(totalSocialSecurity)
                .totalAmount(totalSocialSecurity.multiply(BigDecimal.valueOf(2)))
                .description("6.2% each for employer and employee")
                .build());

        // Medicare (1.45% employee + 1.45% employer)
        breakdown.add(TaxBreakdownDTO.builder()
                .taxType("Medicare Tax")
                .employerPortion(totalMedicare)
                .employeePortion(totalMedicare)
                .totalAmount(totalMedicare.multiply(BigDecimal.valueOf(2)))
                .description("1.45% each for employer and employee")
                .build());

        // FUTA (employer only)
        breakdown.add(TaxBreakdownDTO.builder()
                .taxType("Federal Unemployment Tax (FUTA)")
                .employerPortion(totalFUTA)
                .employeePortion(BigDecimal.ZERO)
                .totalAmount(totalFUTA)
                .description("0.6% employer only (on first $7,000 per employee)")
                .build());

        // SUTA (employer only)
        breakdown.add(TaxBreakdownDTO.builder()
                .taxType("State Unemployment Tax (SUTA)")
                .employerPortion(totalSUTA)
                .employeePortion(BigDecimal.ZERO)
                .totalAmount(totalSUTA)
                .description("State unemployment tax rate varies by state")
                .build());

        return breakdown;
    }

    private List<EmployeeTaxSummaryDTO> createEmployeeTaxSummary(List<EmployerTaxRecord> taxRecords,
                                                                 List<WorkerPayroll> payrolls) {

        Map<Integer, List<EmployerTaxRecord>> recordsByEmployee = taxRecords.stream()
                .collect(Collectors.groupingBy(r -> r.getEmployee().getId()));
        Map<Integer,List<WorkerPayroll>> payrollsByEmp = payrolls.stream()
                .collect(Collectors.groupingBy(p -> p.getWorker().getId()));

        List<EmployeeTaxSummaryDTO> summary = new ArrayList<>();

        for (Map.Entry<Integer, List<EmployerTaxRecord>> entry : recordsByEmployee.entrySet()) {
            Integer empId = entry.getKey();
            List<EmployerTaxRecord> employeeRecords = entry.getValue();
            User employee = employeeRecords.get(0).getEmployee();

            BigDecimal grossWages = employeeRecords.stream()
                    .map(r -> Optional.ofNullable(r.getGrossPay()).orElse(BigDecimal.ZERO))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal federalWithholding = employeeRecords.stream()
                    .map(r -> Optional.ofNullable(r.getFederalWithholding()).orElse(BigDecimal.ZERO))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal socialSecurityWithholding = employeeRecords.stream()
                    .map(r -> Optional.ofNullable(r.getSocialSecurityTax()).orElse(BigDecimal.ZERO))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal medicareWithholding = employeeRecords.stream()
                    .map(r -> Optional.ofNullable(r.getMedicareTax()).orElse(BigDecimal.ZERO))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            List<WorkerPayroll> pp = payrollsByEmp.getOrDefault(empId, List.of());
            BigDecimal stateWithhold = pp.stream()
                    .map(p -> Optional.ofNullable(p.getNyStateWithholding()).orElse(BigDecimal.ZERO))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal localWithhold = pp.stream()
                    .map(p -> Optional.ofNullable(p.getNyLocalWithholding()).orElse(BigDecimal.ZERO))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal totalWithholdings = federalWithholding
                    .add(socialSecurityWithholding)
                    .add(medicareWithholding)
                    .add(stateWithhold)
                    .add(localWithhold);

            BigDecimal netPay = grossWages.subtract(totalWithholdings);

            summary.add(EmployeeTaxSummaryDTO.builder()
                    .employeeName(employee.getFirstName() + " " + employee.getLastName())
                    .grossWages(grossWages)
                    .federalWithholding(federalWithholding)
                    .socialSecurityWithholding(socialSecurityWithholding)
                    .medicareWithholding(medicareWithholding)
                    .stateWithholding(stateWithhold)
                    .localWithholding(localWithhold)
                    .totalWithholdings(totalWithholdings)
                    .netPay(netPay)
                    .build());
        }

        return summary.stream()
                .sorted(Comparator.comparing(EmployeeTaxSummaryDTO::getEmployeeName))
                .collect(Collectors.toList());
    }



    private PaymentStatus getPaymentStatus(
            Integer companyId,
            Integer taxYear,
            Integer quarter,
            TaxSummaryTotals totals,
            LocalDate startDate,
            LocalDate endDate
    ) {
        BigDecimal totalPaid = BigDecimal.ZERO;
        String reportType = determineReportType(startDate, endDate);

        boolean isQuarterly = "Quarterly".equals(reportType);
        boolean isAnnual    = "Annual".equals(reportType);

        if (isQuarterly) {
            // 1) 941
            BigDecimal paid941 = paymentHistoryRepository
                    .getTotalPaidForQuarter941(companyId, quarter, taxYear);
            // 2) FUTA — вызываем правильный метод
            BigDecimal paidFUTA = paymentHistoryRepository
                    .getTotalPaidForQuarterFUTA(companyId, quarter, taxYear);
            // 3) SUTA
            BigDecimal paidSUTA = paymentHistoryRepository
                    .getTotalPaidForQuarterSUTA(companyId, quarter, taxYear);

            totalPaid = paid941
                    .add(paidFUTA)
                    .add(paidSUTA);
        }

        else if (isAnnual) {
            // суммируем по всем четырём кварталам каждого типа
            for (int q = 1; q <= 4; q++) {
                totalPaid = totalPaid
                        .add(paymentHistoryRepository.getTotalPaidForQuarter941(companyId, q, taxYear))
                        .add(paymentHistoryRepository.getTotalPaidForQuarterFUTA(companyId, q, taxYear))
                        .add(paymentHistoryRepository.getTotalPaidForQuarterSUTA(companyId, q, taxYear));
            }

        } else {
            // месячно/произвольно — суммируем по дате и типам платежей
            BigDecimal paid941  = paymentHistoryRepository.getTotalPaidForPeriod(companyId, startDate, endDate);
            BigDecimal paidFUTA = paymentHistoryRepository.getTotalPaidForPeriodFUTA(companyId, startDate, endDate);
            BigDecimal paidSUTA = paymentHistoryRepository.getTotalPaidForPeriodSUTA(companyId, startDate, endDate);

            totalPaid = paid941.add(paidFUTA).add(paidSUTA);
        }


        // вычисляем остаток и соответствие
        BigDecimal remaining = totals.getTotalTaxLiability().subtract(totalPaid);
        boolean isCompliant = remaining.abs().compareTo(BigDecimal.valueOf(5)) <= 0;

        return PaymentStatus.builder()
                .totalPaid(totalPaid)
                .remainingLiability(remaining.max(BigDecimal.ZERO))
                .isCompliant(isCompliant)
                .build();
    }


    private String determineReportType(LocalDate start, LocalDate end) {
        long months = ChronoUnit.MONTHS.between(start, end);

        if (months >= 11) {
            return "Annual";
        } else if (months >= 2 && months <= 3) {
            return "Quarterly";
        } else if (months <= 1) {
            return "Monthly";
        }

        return "Custom Period";
    }

    private Integer getQuarter(LocalDate date) {
        int month = date.getMonthValue();
        return (month - 1) / 3 + 1;
    }

    private List<String> determineRequiredForms(String reportType, Integer taxYear, Integer quarter) {
        List<String> forms = new ArrayList<>();

        if ("Quarterly".equals(reportType)) {
            forms.add("Form 941 - Quarterly Federal Tax Return");
            forms.add("State Quarterly Report");
            forms.add("State Unemployment Tax Report");
        }

        if ("Annual".equals(reportType)) {
            forms.add("Form 940 - Annual Federal Unemployment Tax Return");
            forms.add("W-2 Forms for all employees");
            forms.add("W-3 Transmittal");
            forms.add("State Annual Report");
            forms.add("State Unemployment Tax Report");
            // Form 944 только для annual и только как альтернатива
            forms.add("Form 944 - Annual Federal Tax Return (alternative to quarterly 941s)");
        }

        return forms;
    }
}

