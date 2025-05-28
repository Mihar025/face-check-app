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
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalAdjusters;

@Service
@RequiredArgsConstructor
public class EmployerTaxService {

    private final EmployerTaxRecordRepository employerTaxRecordRepository;
    // Максимальная облагаемая SS-зарплата за год (2025)
    private static final BigDecimal SS_WAGE_LIMIT = new BigDecimal("160200");
    // Порог для Additional Medicare Tax
    private static final BigDecimal ADDL_MEDICARE_THRESHOLD = new BigDecimal("200000");



    public EmployerTaxRecord calculateAndSaveEmployerTaxes(WorkerPayroll payroll) {
        // 1) Проверка на повторный расчёт
        if (employerTaxRecordRepository.existsByPayStubId(payroll.getId())) {
            throw new IllegalStateException("Employer taxes already calculated for this PayStub");
        }

        // 2) Год и ID сотрудника
        int year     = payroll.getPeriodStart().getYear();
        Integer empId   = payroll.getWorker().getId();

        // 3) Накопленные SS-базы YTD (tips и additional нам не нужны)
        BigDecimal ytdSsWages = employerTaxRecordRepository
                .sumSsTaxableWagesByEmployeeAndYear(empId, year);

        // 4) Суммы за текущий период
        BigDecimal dayGross = payroll.getGrossPay();
        // чаевых нет:
        BigDecimal dayTips  = BigDecimal.ZERO;

        // 5) Вычисляем дневные базы:
        // 5.1 SS wages — ограничиваем остатком до годового лимита
        BigDecimal remainingSs  = SS_WAGE_LIMIT.subtract(ytdSsWages).max(BigDecimal.ZERO);
        BigDecimal dailySsBase  = dayGross.min(remainingSs);

        // 5.2 SS tips — всегда 0
        BigDecimal dailySsTips  = BigDecimal.ZERO;

        // 5.3 Medicare база — только gross, т.к. чаевых нет
        BigDecimal dailyMedBase = dayGross;

        // 5.4 Additional Medicare — всегда 0 (никто не превышает порог)
        BigDecimal dailyAddlMedBase = BigDecimal.ZERO;

        // 6) Считаем сами налоги
        BigDecimal socialSecurity = calculatePercentage(dayGross, 6.2);
        BigDecimal medicare       = calculatePercentage(dayGross, 1.45);
        BigDecimal futa           = calculateFuta(payroll.getWorker(),
                payroll.getPeriodStart(),
                dayGross);
        BigDecimal suta           = calculateSuta(payroll.getCompany(), dayGross);

        BigDecimal totalEmployerTax = socialSecurity
                .add(medicare)
                .add(futa)
                .add(suta);

        // 7) Составляем и сохраняем запись с новыми полями
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

                // периоды
                .weekStart(payroll.getPeriodStart())
                .weekEnd(payroll.getPeriodEnd())
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

    private BigDecimal calculateFuta(User employee, LocalDate periodStart, BigDecimal currentGross) {
        int year = periodStart.getYear();
        BigDecimal ytdGross = employerTaxRecordRepository
                .sumGrossPayByEmployeeAndYear(employee.getId().longValue(), year);

        BigDecimal futaLimit = BigDecimal.valueOf(7000);
        BigDecimal remainingFutaGross = futaLimit.subtract(ytdGross);

        if (remainingFutaGross.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal taxableGross = currentGross.min(remainingFutaGross);
        return calculatePercentage(taxableGross, 0.6); // 0.6%
    }

    private BigDecimal calculateSuta(Company company, BigDecimal gross) {
        BigDecimal sutaRate = company.getSocialSecurityTaxForCompany();
        if (sutaRate == null) {
            sutaRate = BigDecimal.valueOf(4.0); // default fallback
        }
        return gross.multiply(sutaRate.divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP))
                .setScale(2, RoundingMode.HALF_UP);
    }
}
