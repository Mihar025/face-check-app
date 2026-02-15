package com.zikpak.facecheck.taxesServices.customReportsForCompanys.payrollReport;

import com.zikpak.facecheck.entity.Company;
import com.zikpak.facecheck.entity.User;
import com.zikpak.facecheck.entity.employee.WorkerAttendance;
import com.zikpak.facecheck.entity.employee.WorkerPayroll;
import com.zikpak.facecheck.repository.CompanyRepository;
import com.zikpak.facecheck.repository.UserRepository;
import com.zikpak.facecheck.repository.WorkerAttendanceRepository;
import com.zikpak.facecheck.repository.WorkerPayrollRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.IsoFields;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PayrollSummaryDataService {

    private final WorkerPayrollRepository workerPayrollRepository;
    private final WorkerAttendanceRepository attendanceRepository;
    private final CompanyRepository companyRepository;
    private final UserRepository workerRepository;

    public PayrollSummaryReportDTO generatePayrollSummaryData(
            Integer companyId, LocalDate startDate, LocalDate endDate) {

        // 1. Получаем данные компании
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new RuntimeException("Company not found"));

        // 2. Получаем все payrolls, пересекающиеся с периодом
        List<WorkerPayroll> payrolls = workerPayrollRepository
                .findAllByCompanyIdAndPeriodOverlap(companyId, startDate, endDate);

        // 3. Получаем всех сотрудников компании
        List<User> workers = workerRepository.findAllByCompanyId(companyId);
        List<Integer> workerIds = workers.stream()
                .map(User::getId)
                .toList();

        // 4. Загружаем все посещения за период и группируем по workerId
        LocalDateTime from = startDate.atStartOfDay();
        LocalDateTime to   = endDate.atTime(23, 59, 59);
        Map<Integer, List<WorkerAttendance>> attendanceByWorker =
                attendanceRepository
                        .findAllByWorkerIdInAndCheckInTimeBetween(workerIds, from, to)
                        .stream()
                        .collect(Collectors.groupingBy(a -> a.getWorker().getId()));

        // 5. Сначала создаем разбивку по сотрудникам (источник правды)
        List<EmployeeSummaryDTO> employeeBreakdown =
                createEmployeeBreakdown(payrolls, workers, attendanceByWorker);

// 6. Считаем итоги ИЗ breakdown (один источник данных)
        BigDecimal totalGross = employeeBreakdown.stream()
                .map(EmployeeSummaryDTO::getGrossPay)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalNet = employeeBreakdown.stream()
                .map(EmployeeSummaryDTO::getNetPay)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalHours = employeeBreakdown.stream()
                .map(EmployeeSummaryDTO::getHoursWorked)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        int totalEmployees = employeeBreakdown.size();

        BigDecimal averageRate = totalHours.compareTo(BigDecimal.ZERO) > 0
                ? totalGross.divide(totalHours, 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;


        // 7. Определяем тип отчета
        String reportType = determineReportType(startDate, endDate);

        return PayrollSummaryReportDTO.builder()
                // Company Info
                .companyId(company.getId())
                .companyName(company.getCompanyName())
                .companyAddress(company.getCompanyAddress())
                .companyCity(company.getCompanyCity())
                .companyState(company.getCompanyState())
                .companyZipCode(company.getCompanyZipCode())
                .companyPhone(company.getCompanyPhone())

                // Period Info
                .periodStart(startDate)
                .periodEnd(endDate)
                .reportType(reportType)

                // Summary Totals
                .totalGrossPay(totalGross)
                .totalNetPay(totalNet)
                .totalTaxesWithheld(BigDecimal.ZERO)
                .totalHoursWorked(totalHours)
                .totalEmployees(totalEmployees)
                .averageHourlyRate(averageRate)

                // Employee Breakdown
                .employeeBreakdown(employeeBreakdown)
                .build();
    }

    private PayrollTotals calculatePayrollTotals(List<WorkerPayroll> payrolls) {
        if (payrolls.isEmpty()) {
            return PayrollTotals.builder()
                    .totalGross(BigDecimal.ZERO)
                    .totalNet(BigDecimal.ZERO)
                    .totalTaxes(BigDecimal.ZERO)
                    .totalHours(BigDecimal.ZERO)
                    .totalEmployees(0)
                    .averageRate(BigDecimal.ZERO)
                    .build();
        }

        BigDecimal totalGross = payrolls.stream()
                .map(p -> Optional.ofNullable(p.getGrossPay()).orElse(BigDecimal.ZERO))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalNet = payrolls.stream()
                .map(p -> Optional.ofNullable(p.getNetPay()).orElse(BigDecimal.ZERO))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalTaxes = payrolls.stream()
                .map(this::calculateTotalTaxesForPayroll)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalHours = payrolls.stream()
                .map(p -> BigDecimal.valueOf(Optional.ofNullable(p.getTotalHours()).orElse(0.0)))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        int totalEmployees = payrolls.stream()
                .map(p -> p.getWorker().getId())
                .collect(Collectors.toSet())
                .size();

        BigDecimal averageRate = safeDivide(totalGross, totalHours);

        return PayrollTotals.builder()
                .totalGross(totalGross)
                .totalNet(totalNet)
                .totalTaxes(totalTaxes)
                .totalHours(totalHours)
                .totalEmployees(totalEmployees)
                .averageRate(averageRate)
                .build();
    }

    private BigDecimal calculateTotalTaxesForPayroll(WorkerPayroll payroll) {
        BigDecimal taxes = BigDecimal.ZERO;

        taxes = taxes.add(Optional.ofNullable(payroll.getFederalWithholding()).orElse(BigDecimal.ZERO));
        taxes = taxes.add(Optional.ofNullable(payroll.getSocialSecurityEmployee()).orElse(BigDecimal.ZERO));
        taxes = taxes.add(Optional.ofNullable(payroll.getMedicare()).orElse(BigDecimal.ZERO));
        taxes = taxes.add(Optional.ofNullable(payroll.getNyStateWithholding()).orElse(BigDecimal.ZERO));
        taxes = taxes.add(Optional.ofNullable(payroll.getNyLocalWithholding()).orElse(BigDecimal.ZERO));
        taxes = taxes.add(Optional.ofNullable(payroll.getNyDisabilityWithholding()).orElse(BigDecimal.ZERO));
        taxes = taxes.add(Optional.ofNullable(payroll.getNyPaidFamilyLeave()).orElse(BigDecimal.ZERO));

        return taxes;
    }


    private List<EmployeeSummaryDTO> createEmployeeBreakdown(
            List<WorkerPayroll> payrolls,
            List<User> workers,
            Map<Integer, List<WorkerAttendance>> attendanceByWorker
    ) {
        List<EmployeeSummaryDTO> breakdown = new ArrayList<>();

        for (User worker : workers) {
            List<WorkerPayroll> workerPayrolls = payrolls.stream()
                    .filter(p -> p.getWorker().getId().equals(worker.getId()))
                    .collect(Collectors.toList());
            if (workerPayrolls.isEmpty()) continue;

            List<WorkerAttendance> workerAttendances =
                    attendanceByWorker.getOrDefault(worker.getId(), Collections.emptyList());

            BigDecimal[] hrs = calculateHoursBreakdown(workerPayrolls, workerAttendances);
            BigDecimal regular  = hrs[0];
            BigDecimal overtime = hrs[1];
            BigDecimal hoursWorked = regular.add(overtime);

            BigDecimal gross = workerPayrolls.stream()
                    .map(p -> Optional.ofNullable(p.getGrossPay()).orElse(BigDecimal.ZERO))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal net = workerPayrolls.stream()
                    .map(p -> Optional.ofNullable(p.getNetPay()).orElse(BigDecimal.ZERO))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal rate = worker.getBaseHourlyRate();

            breakdown.add(EmployeeSummaryDTO.builder()
                    .employeeName(worker.getFirstName() + " " + worker.getLastName())
                    .grossPay(gross)
                    .netPay(net)
                    .hoursWorked(hoursWorked)
                    .hourlyRate(rate)
                    .regularHours(regular)
                    .overtimeHours(overtime)
                    .build()
            );
        }

        return breakdown;
    }




    // 3) Полностью обновлённый calculateHoursBreakdown — без N+1
    private BigDecimal[] calculateHoursBreakdown(
            List<WorkerPayroll> payrolls,
            List<WorkerAttendance> workerAttendances
    ) {
        Map<Integer, BigDecimal> weeklyHours = new HashMap<>();

        for (WorkerPayroll p : payrolls) {
            LocalDate start = p.getPeriodStart();
            LocalDate end   = p.getPeriodEnd();

            workerAttendances.stream()
                    .filter(a -> {
                        LocalDate d = a.getCheckInTime().toLocalDate();
                        return !d.isBefore(start) && !d.isAfter(end);
                    })
                    .forEach(a -> {
                        int week = a.getCheckInTime().get(IsoFields.WEEK_OF_WEEK_BASED_YEAR);
                        BigDecimal hrs = BigDecimal.valueOf(
                                Optional.ofNullable(a.getHoursWorked()).orElse(0.0)
                        );
                        weeklyHours.merge(week, hrs, BigDecimal::add);
                    });
        }

        BigDecimal regular = BigDecimal.ZERO;
        BigDecimal overtime = BigDecimal.ZERO;

        for (BigDecimal total : weeklyHours.values()) {
            if (total.compareTo(BigDecimal.valueOf(40)) > 0) {
                regular = regular.add(BigDecimal.valueOf(40));
                overtime = overtime.add(total.subtract(BigDecimal.valueOf(40)));
            } else {
                regular = regular.add(total);
            }
        }

        return new BigDecimal[]{ regular, overtime };
    }



    private String determineReportType(LocalDate start, LocalDate end) {
        if (ChronoUnit.WEEKS.between(start, end) < 1) {
            return "Weekly";
        }
        if (start.withDayOfMonth(1).equals(start)
                && end.equals(start.withDayOfMonth(start.lengthOfMonth()))) {
            return "Monthly";
        }
        return "Custom Period";
    }


    private BigDecimal safeDivide(BigDecimal num, BigDecimal denom) {
        return denom != null
                && denom.compareTo(BigDecimal.ZERO) > 0
                ? num.divide(denom, 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;
    }


}
