package com.zikpak.facecheck.taxesServices.services;

import com.zikpak.facecheck.entity.employee.WorkerAttendance;
import com.zikpak.facecheck.entity.employee.WorkerPayroll;
import com.zikpak.facecheck.repository.WorkerAttendanceRepository;
import com.zikpak.facecheck.repository.WorkerPayrollRepository;
import com.zikpak.facecheck.requestsResponses.PayStubDTO;
import com.zikpak.facecheck.services.amazonS3Service.AmazonS3Service;
import com.zikpak.facecheck.taxesServices.dto.PayStubFileDTO;
import com.zikpak.facecheck.taxesServices.pdfServices.PayStubPdfGeneratorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;


@RequiredArgsConstructor
@Service
@Slf4j
public class PayStubService {

    private final WorkerPayrollRepository workerPayrollRepository;
    private final WorkerAttendanceRepository attendanceRepository;
    private final PayStubPdfGeneratorService payStubPdfGeneratorService;
    private final AmazonS3Service amazonS3Service;

    public byte[] generatePayStubPdf(Integer payrollId) {
        WorkerPayroll payroll = workerPayrollRepository.findById(payrollId)
                .orElseThrow(() -> new RuntimeException("Payroll not found"));

        var worker = payroll.getWorker();
        var company = worker.getCompany();

        // 1. Собираем все посещения за этот pay period
        List<WorkerAttendance> attendanceList = attendanceRepository.findAllByWorkerIdAndCheckInTimeBetween(
                worker.getId(),
                payroll.getPeriodStart().atStartOfDay(),
                payroll.getPeriodEnd().atTime(23, 59, 59)
        );


        /*
        // 2. Собираем map<DayOfWeek, LocalDate> — для печати дат
        Map<DayOfWeek, LocalDate> datesPerDay = new HashMap<>();
        for (WorkerAttendance attendance : attendanceList) {
            if (attendance.getCheckInTime() == null) continue;
            DayOfWeek day = attendance.getCheckInTime().getDayOfWeek();
            LocalDate attendanceDate = attendance.getCheckInTime().toLocalDate();
            datesPerDay.putIfAbsent(day, attendanceDate);
        }

         */

        Map<LocalDate, BigDecimal> hoursWorkedPerDate = new TreeMap<>();  // TreeMap для автоматической сортировки по датам
        Map<LocalDate, BigDecimal> grossPayPerDate = new TreeMap<>();
        Map<LocalDate, DayOfWeek> dateToDayOfWeek = new TreeMap<>();

        for (WorkerAttendance attendance : attendanceList) {
            if (attendance.getCheckInTime() == null) continue;

            LocalDate date = attendance.getCheckInTime().toLocalDate();
            DayOfWeek dayOfWeek = date.getDayOfWeek();

            // Сохраняем день недели для даты
            dateToDayOfWeek.put(date, dayOfWeek);

            // а) Часы
            BigDecimal hours = BigDecimal.valueOf(
                    Optional.ofNullable(attendance.getHoursWorked()).orElse(0.0)
            ).setScale(2, RoundingMode.HALF_UP);

            hoursWorkedPerDate.merge(date, hours, BigDecimal::add);

            // b) Gross-pay
            BigDecimal pay = Optional.ofNullable(attendance.getGrossPayPerDay())
                    .orElse(BigDecimal.ZERO)
                    .setScale(2, RoundingMode.HALF_UP);

            grossPayPerDate.merge(date, pay, BigDecimal::add);
        }

        // 3. Считаем YTD Gross и YTD Net (с начала года до текущего payPeriodEnd)
        List<WorkerPayroll> ytdPayrolls = workerPayrollRepository.findAllByWorkerIdAndPeriodEndBetween(
                worker.getId(),
                LocalDate.of(payroll.getPeriodEnd().getYear(), 1, 1),
                payroll.getPeriodEnd()
        );

        BigDecimal yearToDateGross = ytdPayrolls.stream()
                .map(p -> p.getGrossPay() != null ? p.getGrossPay() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal yearToDateNet = ytdPayrolls.stream()
                .map(p -> p.getNetPay() != null ? p.getNetPay() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 5. Вычисляем параметры Insurance
        Boolean       hasInsurance      = Boolean.TRUE.equals(worker.getEnrolledInHealthPlan());
        BigDecimal    monthlyPremium    = hasInsurance
                ? Optional.ofNullable(worker.getMonthlyHealthPremium())
                .orElse(BigDecimal.ZERO)
                : BigDecimal.ZERO;


        // добавляем в начале метода
        int payPeriods = switch (company.getCompanyPaymentPosition()) {
            case WEEKLY   -> 52;
            case BIWEEKLY -> 26;
        };

// дальше вместо деления на 52
        BigDecimal periodHealthDeduction = monthlyPremium
                .multiply(BigDecimal.valueOf(12))
                .divide(BigDecimal.valueOf(payPeriods), 2, RoundingMode.HALF_UP);

// и в stub — это удержано за период
        BigDecimal chargePeriod = hasInsurance
                ? periodHealthDeduction
                : BigDecimal.ZERO;



        // 6. Вычисляем параметры SickLeave
        BigDecimal accrued   = Optional.ofNullable(worker.getSickLeaveAccrued()).orElse(BigDecimal.ZERO);
        BigDecimal used      = Optional.ofNullable(worker.getSickLeaveUsed()).orElse(BigDecimal.ZERO);
        BigDecimal remaining = accrued.subtract(used).max(BigDecimal.ZERO);

        // 7. Собираем DTO
        PayStubDTO stub = PayStubDTO.builder()
                // ————— Employee info —————
                .workerId(worker.getId())
                .employeeName(worker.getFirstName() + " "
                        + Optional.ofNullable(worker.getMiddleInitial()).orElse("") + " "
                        + worker.getLastName())
                .employeeSsn(worker.getSSN_WORKER())
                .employeeAddress(worker.getHomeAddress())
                .employeeCity(worker.getCity())
                .employeeState(worker.getState())
                .employeeZipCode(worker.getZipcode())
                .employeePhoneNumber(worker.getPhoneNumber())

                // ————— Company info —————
                .companyId(company.getId())
                .companyName(company.getCompanyName())
                .employerAddress(company.getCompanyAddress())
                .companyCity(company.getCompanyCity())
                .companyState(company.getCompanyState())
                .companyZipCode(company.getCompanyZipCode())
                .companyPhoneNumber(company.getCompanyPhone())

                // ————— Period info —————
                .periodStart(payroll.getPeriodStart())
                .periodEnd(payroll.getPeriodEnd())

                // ————— Earnings & taxes —————
                .totalGrossPay(payroll.getGrossPay())
                .federalTax(payroll.getFederalWithholding())
                .socialSecurityTax(payroll.getSocialSecurityEmployee())
                .medicareTax(payroll.getMedicare())
                .stateTax(payroll.getNyStateWithholding())
                .localTax(payroll.getNyLocalWithholding())
                .netPay(payroll.getNetPay())
                .hoursWorkedPerDate(hoursWorkedPerDate)
                .grossPayPerDate(grossPayPerDate)

                // ————— Insurance fields —————
                .userActivatedInsurance(hasInsurance)
                .healthInsuranceMonthly(monthlyPremium.setScale(2, RoundingMode.HALF_UP))
                .healthInsuranceChargePeriod(chargePeriod.setScale(2, RoundingMode.HALF_UP))
                // ————— Sick Leave fields —————
                .sickLeaveAccrued(accrued.setScale(2, RoundingMode.HALF_UP))
                .sickLeaveUsed(used.setScale(2, RoundingMode.HALF_UP))
                .sickLeaveRemaining(remaining.setScale(2, RoundingMode.HALF_UP))




                // ————— Rate & hours —————
                .baseHourlyRate(worker.getBaseHourlyRate())
                .totalHours(payroll.getTotalHours())

                // ————— Dates per day —————
                .dateToDayOfWeek(dateToDayOfWeek)
                .year(LocalDate.now().getYear())
                .build();


        byte[] pdf = payStubPdfGeneratorService.generatePayStubPdf(stub);
        String companyKeyPart = company.getCompanyName()
                .trim()
                .replaceAll("[^A-Za-z0-9]", "_");

        String workerKeyPart = (worker.getFirstName() + "_" + worker.getLastName() + "_" + worker.getId())
                .trim()
                .replaceAll("[^A-Za-z0-9_]", "_");

        String periodPart = payroll.getPeriodStart().toString()
                + "_"
                + payroll.getPeriodEnd().toString();

        // собираем финальный ключ
        String key = String.format(
                "%s/%d/paystubs/%s/%s/%d.pdf",
                companyKeyPart,
                company.getId(),
                workerKeyPart,
                periodPart,
                payrollId
        );

        amazonS3Service.uploadPdfToS3(pdf, key);
        return pdf;
    }


    public List<PayStubFileDTO> getPayStubFilesList(Integer companyId, LocalDate startDate, LocalDate endDate) {
        // Используем твой существующий метод
        List<WorkerPayroll> payrolls = workerPayrollRepository.findAllByCompanyIdAndPeriodBetween(
                companyId, startDate, endDate);

        return payrolls.stream()
                .map(this::convertToPayStubFileDTO)
                .sorted(Comparator.comparing(PayStubFileDTO::getEmployeeName)
                        .thenComparing(PayStubFileDTO::getPeriodStart).reversed())
                .collect(Collectors.toList());
    }

    public List<PayStubFileDTO> getWorkerPayStubFilesList(Integer companyId, Integer workerId,
                                                           LocalDate startDate, LocalDate endDate) {
        // Получаем все payroll записи для компании за период, потом фильтруем по workerId
        List<WorkerPayroll> payrolls = workerPayrollRepository.findAllByCompanyIdAndPeriodBetween(
                companyId, startDate, endDate);

        return payrolls.stream()
                .filter(payroll -> payroll.getWorker().getId().equals(workerId))
                .map(this::convertToPayStubFileDTO)
                .sorted(Comparator.comparing(PayStubFileDTO::getPeriodStart).reversed())
                .collect(Collectors.toList());
    }

    public Map<String, List<PayStubFileDTO>> getGroupedPayStubFilesList(Integer companyId,
                                                                         LocalDate startDate, LocalDate endDate) {
        List<PayStubFileDTO> allFiles = getPayStubFilesList(companyId, startDate, endDate);

        // Группируем по имени сотрудника
        return allFiles.stream()
                .collect(Collectors.groupingBy(PayStubFileDTO::getEmployeeName));
    }

    private PayStubFileDTO convertToPayStubFileDTO(WorkerPayroll payroll) {
        String fileName = String.format("PayStub_%s_%s_to_%s.pdf",
                payroll.getWorker().getFirstName() + "_" + payroll.getWorker().getLastName(),
                payroll.getPeriodStart().toString(),
                payroll.getPeriodEnd().toString());

        String downloadUrl = String.format("/taxes-forms/download-paystub/%d", payroll.getId());

        return new PayStubFileDTO(
                payroll.getId(),
                fileName,
                payroll.getWorker().getFirstName() + " " + payroll.getWorker().getLastName(),
                payroll.getPeriodStart().toString(),
                payroll.getPeriodEnd().toString(),
                downloadUrl,
                null // fileSize можно добавить позже если нужно
        );
    }




}
