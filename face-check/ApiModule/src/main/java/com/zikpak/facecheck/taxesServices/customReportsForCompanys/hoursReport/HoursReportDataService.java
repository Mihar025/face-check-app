package com.zikpak.facecheck.taxesServices.customReportsForCompanys.hoursReport;

import com.zikpak.facecheck.entity.Company;
import com.zikpak.facecheck.entity.User;
import com.zikpak.facecheck.entity.employee.WorkerAttendance;
import com.zikpak.facecheck.repository.CompanyRepository;
import com.zikpak.facecheck.repository.UserRepository;
import com.zikpak.facecheck.repository.WorkerAttendanceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.time.temporal.IsoFields;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class HoursReportDataService {

    private final WorkerAttendanceRepository attendanceRepository;
    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;

    public HoursReportDTO generateHoursReportData(
            Integer companyId, LocalDate startDate, LocalDate endDate) {

        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new RuntimeException("Company not found"));


        // Получаем все attendance для компании за период
        List<WorkerAttendance> attendances = attendanceRepository
                .findAllByCompanyIdAndCheckInTimeBetween(
                        companyId,
                        startDate.atStartOfDay(),
                        endDate.atTime(23, 59, 59)
                );

        // Получаем уникальных работников из attendances
        Set<Integer> workerIds = attendances.stream()
                .map(a -> a.getWorker().getId())
                .collect(Collectors.toSet());

        // ИСПРАВЛЕНО: используем findAllById для множества ID
        List<User> workers = userRepository.findAllById(workerIds);


        // 4. Вычисляем totals
        HoursTotals totals = calculateHoursTotals(attendances);

        // 5. Создаем breakdown по сотрудникам
        List<EmployeeHoursDTO> employeeHours = createEmployeeHoursBreakdown(attendances, workers);

        // 6. Создаем daily breakdown
        Map<LocalDate, DailyHoursDTO> dailyHours = createDailyHoursBreakdown(attendances);

        // 7. Находим top performers
        List<EmployeeHoursDTO> topPerformers = findTopPerformers(employeeHours);

        return HoursReportDTO.builder()
                .companyName(company.getCompanyName())
                .companyAddress(company.getCompanyAddress())
                .companyCity(company.getCompanyCity())
                .companyState(company.getCompanyState())
                .companyZipCode(company.getCompanyZipCode())
                .companyPhone(company.getCompanyPhone())
                .periodStart(startDate)
                .periodEnd(endDate)
                .reportType(determineReportType(startDate, endDate))
                .totalRegularHours(totals.getTotalRegular())
                .totalOvertimeHours(totals.getTotalOvertime())
                .totalHours(totals.getTotalHours())
                .averageHoursPerEmployee(totals.getAveragePerEmployee())
                .overtimePercentage(totals.getOvertimePercentage())
                .totalEmployees(workers.size())
                .employeeHours(employeeHours)
                .dailyHours(dailyHours)
                .topPerformers(topPerformers)
                .build();
    }

    private HoursTotals calculateHoursTotals(List<WorkerAttendance> attendances) {
        Map<Integer, Map<Integer, BigDecimal>> weeklyHoursByWorker = new HashMap<>();

        for (WorkerAttendance attendance : attendances) {
            int workerId = attendance.getWorker().getId();
            int weekOfYear = attendance.getCheckInTime().get(IsoFields.WEEK_OF_WEEK_BASED_YEAR);

            BigDecimal dailyHours = BigDecimal.valueOf(
                    Optional.ofNullable(attendance.getHoursWorked()).orElse(0.0)
            );

            weeklyHoursByWorker
                    .computeIfAbsent(workerId, k -> new HashMap<>())
                    .merge(weekOfYear, dailyHours, BigDecimal::add);
        }

        BigDecimal totalRegular = BigDecimal.ZERO;
        BigDecimal totalOvertime = BigDecimal.ZERO;

        for (Map<Integer, BigDecimal> weeklyHours : weeklyHoursByWorker.values()) {
            for (BigDecimal weeklyTotal : weeklyHours.values()) {
                if (weeklyTotal.compareTo(BigDecimal.valueOf(40)) > 0) {
                    totalRegular = totalRegular.add(BigDecimal.valueOf(40));
                    totalOvertime = totalOvertime.add(weeklyTotal.subtract(BigDecimal.valueOf(40)));
                } else {
                    totalRegular = totalRegular.add(weeklyTotal);
                }
            }
        }

        BigDecimal totalHours = totalRegular.add(totalOvertime);

        Set<Integer> uniqueWorkers = weeklyHoursByWorker.keySet();

        BigDecimal averagePerEmployee = uniqueWorkers.size() > 0
                ? totalHours.divide(BigDecimal.valueOf(uniqueWorkers.size()), 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        BigDecimal overtimePercentage = totalHours.compareTo(BigDecimal.ZERO) > 0
                ? totalOvertime.multiply(BigDecimal.valueOf(100)).divide(totalHours, 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        return HoursTotals.builder()
                .totalRegular(totalRegular)
                .totalOvertime(totalOvertime)
                .totalHours(totalHours)
                .averagePerEmployee(averagePerEmployee)
                .overtimePercentage(overtimePercentage)
                .build();
    }




    private List<EmployeeHoursDTO> createEmployeeHoursBreakdown(
            List<WorkerAttendance> attendances, List<User> workers) {

        Map<Integer, List<WorkerAttendance>> attendanceByWorker = attendances.stream()
                .collect(Collectors.groupingBy(a -> a.getWorker().getId()));

        List<EmployeeHoursDTO> result = new ArrayList<>();

        for (User worker : workers) {
            List<WorkerAttendance> workerAttendances = attendanceByWorker
                    .getOrDefault(worker.getId(), Collections.emptyList());

            if (!workerAttendances.isEmpty()) {
                EmployeeHoursDTO dto = createEmployeeHoursSummary(worker, workerAttendances);
                result.add(dto);
            }
        }

        result.sort((a, b) -> b.getTotalHours().compareTo(a.getTotalHours()));
        return result;
    }

    private EmployeeHoursDTO createEmployeeHoursSummary(User worker, List<WorkerAttendance> attendances) {
        Map<Integer, BigDecimal> weeklyHours = new HashMap<>();

        for (WorkerAttendance attendance : attendances) {
            int weekOfYear = attendance.getCheckInTime().get(IsoFields.WEEK_OF_WEEK_BASED_YEAR);
            BigDecimal dailyHours = BigDecimal.valueOf(
                    Optional.ofNullable(attendance.getHoursWorked()).orElse(0.0)
            );

            weeklyHours.merge(weekOfYear, dailyHours, BigDecimal::add);
        }

        BigDecimal regularHours = BigDecimal.ZERO;
        BigDecimal overtimeHours = BigDecimal.ZERO;

        for (BigDecimal weeklyTotal : weeklyHours.values()) {
            if (weeklyTotal.compareTo(BigDecimal.valueOf(40)) > 0) {
                regularHours = regularHours.add(BigDecimal.valueOf(40));
                overtimeHours = overtimeHours.add(weeklyTotal.subtract(BigDecimal.valueOf(40)));
            } else {
                regularHours = regularHours.add(weeklyTotal);
            }
        }

        BigDecimal totalHours = regularHours.add(overtimeHours);
        BigDecimal dailyAverage = attendances.size() > 0
                ? totalHours.divide(BigDecimal.valueOf(attendances.size()), 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        BigDecimal overtimeRate = totalHours.compareTo(BigDecimal.ZERO) > 0
                ? overtimeHours.multiply(BigDecimal.valueOf(100)).divide(totalHours, 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        BigDecimal hourlyRate = worker.getBaseHourlyRate();
        BigDecimal overtimePayRate = hourlyRate.multiply(BigDecimal.valueOf(1.5));

        BigDecimal totalEarnings = regularHours.multiply(hourlyRate)
                .add(overtimeHours.multiply(overtimePayRate));

        return EmployeeHoursDTO.builder()
                .employeeName(worker.getFirstName() + " " + worker.getLastName())
                .regularHours(regularHours.setScale(2, RoundingMode.HALF_UP))
                .overtimeHours(overtimeHours.setScale(2, RoundingMode.HALF_UP))
                .totalHours(totalHours.setScale(2, RoundingMode.HALF_UP))
                .dailyAverage(dailyAverage)
                .overtimeRate(overtimeRate)
                .hourlyRate(hourlyRate)
                .totalEarnings(totalEarnings.setScale(2, RoundingMode.HALF_UP))
                .build();
    }


    private Map<LocalDate, DailyHoursDTO> createDailyHoursBreakdown(List<WorkerAttendance> attendances) {
        Map<LocalDate, List<WorkerAttendance>> attendanceByDate = attendances.stream()
                .filter(a -> a.getCheckInTime() != null)
                .collect(Collectors.groupingBy(a -> a.getCheckInTime().toLocalDate()));

        Map<LocalDate, DailyHoursDTO> dailyHours = new LinkedHashMap<>();

        for (Map.Entry<LocalDate, List<WorkerAttendance>> entry : attendanceByDate.entrySet()) {
            LocalDate date = entry.getKey();
            List<WorkerAttendance> dayAttendances = entry.getValue();

            BigDecimal totalDayHours = BigDecimal.ZERO;
            BigDecimal totalOvertimeHours = BigDecimal.ZERO;

            for (WorkerAttendance attendance : dayAttendances) {
                BigDecimal hours = BigDecimal.valueOf(
                        Optional.ofNullable(attendance.getHoursWorked()).orElse(0.0)
                );
                totalDayHours = totalDayHours.add(hours);

                if (hours.compareTo(BigDecimal.valueOf(8)) > 0) {
                    totalOvertimeHours = totalOvertimeHours.add(hours.subtract(BigDecimal.valueOf(8)));
                }
            }

            BigDecimal averageHoursPerEmployee = dayAttendances.size() > 0
                    ? totalDayHours.divide(BigDecimal.valueOf(dayAttendances.size()), 2, RoundingMode.HALF_UP)
                    : BigDecimal.ZERO;

            DailyHoursDTO dailyDTO = DailyHoursDTO.builder()
                    .date(date)
                    .totalHours(totalDayHours.setScale(2, RoundingMode.HALF_UP))
                    .employeesWorked(dayAttendances.size())
                    .averageHoursPerEmployee(averageHoursPerEmployee)
                    .totalOvertimeHours(totalOvertimeHours.setScale(2, RoundingMode.HALF_UP))
                    .build();

            dailyHours.put(date, dailyDTO);
        }

        return dailyHours;
    }

    private List<EmployeeHoursDTO> findTopPerformers(List<EmployeeHoursDTO> employeeHours) {
        return employeeHours.stream()
                .sorted((a, b) -> b.getTotalHours().compareTo(a.getTotalHours()))
                .limit(5) // Top 5 performers
                .collect(Collectors.toList());
    }

    private String determineReportType(LocalDate startDate, LocalDate endDate) {
        long daysBetween = ChronoUnit.DAYS.between(startDate, endDate);

        if (daysBetween <= 7) {
            return "Weekly";
        } else if (daysBetween <= 31) {
            return "Monthly";
        } else {
            return "Custom Period";
        }
    }

}
