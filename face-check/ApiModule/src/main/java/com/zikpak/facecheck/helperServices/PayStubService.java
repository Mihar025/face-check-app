package com.zikpak.facecheck.helperServices;

import com.zikpak.facecheck.entity.employee.WorkerAttendance;
import com.zikpak.facecheck.entity.employee.WorkerPayroll;
import com.zikpak.facecheck.repository.WorkerAttendanceRepository;
import com.zikpak.facecheck.repository.WorkerPayrollRepository;
import com.zikpak.facecheck.requestsResponses.PayStubDTO;
import com.zikpak.facecheck.services.amazonS3Service.AmazonS3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.*;

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

        // 1. Загружаем посещения по дате
        List<WorkerAttendance> attendanceList = attendanceRepository.findAllByWorkerIdAndCheckInTimeBetween(
                worker.getId(),
                payroll.getPeriodStart().atStartOfDay(),
                payroll.getPeriodEnd().atTime(23, 59, 59)
        );

        // 2. Инициализируем Map-ы
        Map<DayOfWeek, BigDecimal> hoursWorkedPerDay = new HashMap<>();
        Map<DayOfWeek, BigDecimal> grossPayPerDay = new HashMap<>();

        for (WorkerAttendance attendance : attendanceList) {
            if (attendance.getCheckInTime() == null) continue;

            DayOfWeek day = attendance.getCheckInTime().getDayOfWeek();

            // Часы
            BigDecimal hours = BigDecimal.valueOf(Optional.ofNullable(attendance.getHoursWorked()).orElse(0.0));
            hoursWorkedPerDay.merge(day, hours, BigDecimal::add);

            // Gross pay
            BigDecimal pay = Optional.ofNullable(attendance.getGrossPayPerDay()).orElse(BigDecimal.ZERO);
            grossPayPerDay.merge(day, pay, BigDecimal::add);
        }

        PayStubDTO stub = PayStubDTO.builder()
                .employeeName(worker.getFirstName() + " " + worker.getLastName())
                .employeeSsn(worker.getSSN_WORKER())
                .employeeAddress(worker.getHomeAddress())
                .employerName(company.getCompanyName())
                .employerAddress(company.getCompanyAddress())
                .periodStart(payroll.getPeriodStart())
                .periodEnd(payroll.getPeriodEnd())
                .totalGrossPay(payroll.getGrossPay())
                .federalTax(payroll.getFederalWithholding())
                .socialSecurityTax(payroll.getSocialSecurityEmployee())
                .medicareTax(payroll.getMedicare())
                .stateTax(payroll.getNyStateWithholding())
                .localTax(payroll.getNyLocalWithholding())
                .netPay(payroll.getNetPay())
                .hoursWorkedPerDay(hoursWorkedPerDay)
                .grossPayPerDay(grossPayPerDay)
                .build();

        byte[] pdf = payStubPdfGeneratorService.generatePayStubPdf(stub);

        String fileName = "paystubs/" + payroll.getWorker().getId() + "/" + payrollId + ".pdf";
        amazonS3Service.uploadPdfToS3(pdf, fileName);

        return pdf;
    }
}
