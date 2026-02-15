package com.zikpak.facecheck.taxesServices.services;

import com.zikpak.facecheck.entity.Company;
import com.zikpak.facecheck.entity.EmployerTaxRecord;
import com.zikpak.facecheck.entity.User;
import com.zikpak.facecheck.entity.employee.WorkerPayroll;
import com.zikpak.facecheck.repository.EmployerTaxRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;

@Service
@RequiredArgsConstructor
public class EmployerTaxService {

    private final EmployerTaxRecordRepository employerTaxRecordRepository;

    // Максимальная облагаемая SS-зарплата за год (2025)
    private static final BigDecimal SS_WAGE_LIMIT = new BigDecimal("160200");
    // Порог для Additional Medicare Tax
    private static final BigDecimal ADDL_MEDICARE_THRESHOLD = new BigDecimal("200000");
    // FUTA wage limit
    private static final BigDecimal FUTA_WAGE_LIMIT = new BigDecimal("7000");
    // SUTA wage limit для NY — ПРОВЕРЬ АКТУАЛЬНОЕ ЗНАЧЕНИЕ!
    // NY 2025: $12,300 или $13,000 — уточни на сайте NY DOL
    private static final BigDecimal SUTA_WAGE_LIMIT = new BigDecimal("12300");



    public EmployerTaxRecord calculateAndSaveEmployerTaxes(WorkerPayroll payroll) {
        // 1) Проверка на повторный расчёт
        if (employerTaxRecordRepository.existsByPayStubId(payroll.getId())) {
            throw new IllegalStateException("Employer taxes already calculated for this PayStub");
        }

        // 2) Год и ID сотрудника
        int year = payroll.getPeriodStart().getYear();
        Integer empId = payroll.getWorker().getId();

        // 3) Загружаем все YTD суммы ОДНИМ БЛОКОМ (убираем дублирующие запросы)
        BigDecimal ytdSsWages = employerTaxRecordRepository
                .sumSsTaxableWagesByEmployeeAndYear(empId, year);
        BigDecimal ytdFutaWages = employerTaxRecordRepository
                .sumFutaTaxableWagesByEmployeeAndYear(empId, year);
        BigDecimal ytdSutaWages = employerTaxRecordRepository
                .sumSutaTaxableWagesByEmployeeAndYear(empId, year);

        BigDecimal dayGross = payroll.getGrossPay();

        // 4) Вычисляем дневные базы с учётом годовых лимитов:

        // 4.1 SS wages — ограничиваем остатком до годового лимита
        BigDecimal remainingSs = SS_WAGE_LIMIT.subtract(ytdSsWages).max(BigDecimal.ZERO);
        BigDecimal dailySsBase = dayGross.min(remainingSs);

        // 4.2 SS tips — всегда 0
        BigDecimal dailySsTips = BigDecimal.ZERO;

        // 4.3 Medicare база — только gross, т.к. чаевых нет
        BigDecimal dailyMedBase = dayGross;

        // 4.4 Additional Medicare — всегда 0 (никто не превышает порог)
        BigDecimal dailyAddlMedBase = BigDecimal.ZERO;

        // 4.5 FUTA taxable wages
        BigDecimal taxableFuta = dayGross.min(FUTA_WAGE_LIMIT.subtract(ytdFutaWages).max(BigDecimal.ZERO));

        // 4.6 SUTA taxable wages
        BigDecimal taxableSuta = dayGross.min(SUTA_WAGE_LIMIT.subtract(ytdSutaWages).max(BigDecimal.ZERO));

        // 5) Считаем сами налоги
        // FIX: SS считается от dailySsBase (с учётом лимита), а НЕ от dayGross
        BigDecimal socialSecurity = calculatePercentage(dailySsBase, 6.2);
        BigDecimal medicare = calculatePercentage(dailyMedBase, 1.45);

        // FIX: FUTA/SUTA считаем от уже посчитанных taxable wages (без повторных запросов в БД)
        BigDecimal futa = calculatePercentage(taxableFuta, 0.6);

        BigDecimal sutaRate = payroll.getCompany().getSocialSecurityTaxForCompany();
        if (sutaRate == null) sutaRate = BigDecimal.valueOf(4.1);
        BigDecimal suta = taxableSuta
                .multiply(sutaRate.divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP))
                .setScale(2, RoundingMode.HALF_UP);

        BigDecimal totalEmployerTax = socialSecurity
                .add(medicare)
                .add(futa)
                .add(suta);

        // Проверка на дубликат по периоду
        boolean exists = employerTaxRecordRepository.existsByEmployeeIdAndPeriodStartAndPeriodEnd(
                payroll.getWorker().getId(), payroll.getPeriodStart(), payroll.getPeriodEnd());
        if (exists) return null;

        // 6) Составляем и сохраняем запись
        EmployerTaxRecord record = EmployerTaxRecord.builder()
                .company(payroll.getCompany())
                .employee(payroll.getWorker())
                .payStub(payroll)

                // исходное
                .grossPay(dayGross)
                .federalWithholding(payroll.getFederalWithholding())

                // рассчитанные налоги
                .socialSecurityTax(socialSecurity)
                .medicareTax(medicare)
                .futaTax(futa)
                .sutaTax(suta)
                .totalEmployerTax(totalEmployerTax)

                // базы для 941 (5a–5d)
                .socialSecurityTaxableWages(dailySsBase)
                .socialSecurityTips(dailySsTips)
                .medicareTaxableWages(dailyMedBase)
                .additionalMedicareWages(dailyAddlMedBase)

                .futaTaxableWages(taxableFuta)
                .sutaTaxableWages(taxableSuta)

                // периоды
                .periodStart(payroll.getPeriodStart())
                .periodEnd(payroll.getPeriodEnd())
                .createdAt(LocalDate.now())

                .paymentDate(
                        payroll.getPeriodEnd()
                                .with(TemporalAdjusters.next(DayOfWeek.FRIDAY)))
                .build();
        return employerTaxRecordRepository.save(record);
    }

    private BigDecimal calculatePercentage(BigDecimal amount, double percent) {
        return amount.multiply(BigDecimal.valueOf(percent / 100)).setScale(2, RoundingMode.HALF_UP);
    }

}