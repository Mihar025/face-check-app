package com.zikpack.facecheck.taxesServices.customReportsForCompany;

import com.zikpak.facecheck.entity.Company;
import com.zikpak.facecheck.entity.User;
import com.zikpak.facecheck.entity.employee.WorkerAttendance;
import com.zikpak.facecheck.entity.employee.WorkerPayroll;
import com.zikpak.facecheck.repository.CompanyRepository;
import com.zikpak.facecheck.repository.UserRepository;
import com.zikpak.facecheck.repository.WorkerAttendanceRepository;
import com.zikpak.facecheck.repository.WorkerPayrollRepository;
import com.zikpak.facecheck.taxesServices.customReportsForCompanys.payrollReport.PayrollSummaryDataService;
import com.zikpak.facecheck.taxesServices.customReportsForCompanys.payrollReport.PayrollSummaryReportDTO;
import com.zikpak.facecheck.taxesServices.customReportsForCompanys.payrollReport.PayrollTotals;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PayrollSummaryDataServiceTest {

    @Mock
    private WorkerPayrollRepository workerPayrollRepository;

    @Mock
    private WorkerAttendanceRepository attendanceRepository;

    @Mock
    private CompanyRepository companyRepository;

    @Mock
    private UserRepository workerRepository;

    @InjectMocks
    private PayrollSummaryDataService service;

    @Test
    void generatePayrollSummaryData_ShouldReturnCorrectData_WhenValidInput() {
        // Given
        Integer companyId = 1;
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 1, 31);

        Company company = Company.builder()
                .id(companyId)
                .companyName("Test Company")
                .companyAddress("123 Test St")
                .companyCity("Test City")
                .companyState("TS")
                .companyZipCode("12345")
                .companyPhone("555-1234")
                .build();

        User worker1 = User.builder().id(1).build();
        User worker2 = User.builder().id(2).build();

        List<User> workers = Arrays.asList(worker1, worker2);

        // Добавляем все необходимые поля включая даты периода
        List<WorkerPayroll> payrolls = Arrays.asList(
                WorkerPayroll.builder()
                        .id(1)
                        .worker(worker1)
                        .grossPay(new BigDecimal("1000.00"))
                        .netPay(new BigDecimal("800.00"))
                        //.totalTaxes(new BigDecimal("200.00"))
                        .periodStart(startDate)  // Добавляем даты периода
                        .periodEnd(endDate)
                        .build()
        );

        List<WorkerAttendance> attendances = Arrays.asList(
                WorkerAttendance.builder()
                        .worker(worker1)
                        .checkInTime(startDate.atTime(9, 0))
                        .checkOutTime(startDate.atTime(17, 0))
                        .build()
        );

        when(companyRepository.findById(companyId)).thenReturn(Optional.of(company));
        when(workerRepository.findAllByCompanyId(companyId)).thenReturn(workers);
        when(workerPayrollRepository.findAllByCompanyIdAndPeriodOverlap(companyId, startDate, endDate))
                .thenReturn(payrolls);
        when(attendanceRepository.findAllByWorkerIdInAndCheckInTimeBetween(
                anyList(), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(attendances);

        // When
        PayrollSummaryReportDTO result = service.generatePayrollSummaryData(companyId, startDate, endDate);

        // Then
        assertNotNull(result);
        assertEquals(companyId, result.getCompanyId());
        assertEquals("Test Company", result.getCompanyName());
        assertEquals(startDate, result.getPeriodStart());
        assertEquals(endDate, result.getPeriodEnd());

        // Проверяем что данные были правильно обработаны
        assertNotNull(result.getTotalGrossPay());
        assertNotNull(result.getTotalNetPay());
        assertNotNull(result.getEmployeeBreakdown());

        verify(companyRepository).findById(companyId);
        verify(workerPayrollRepository).findAllByCompanyIdAndPeriodOverlap(companyId, startDate, endDate);
        verify(workerRepository).findAllByCompanyId(companyId);
        verify(attendanceRepository).findAllByWorkerIdInAndCheckInTimeBetween(
                Arrays.asList(1, 2),
                startDate.atStartOfDay(),
                endDate.atTime(23, 59, 59)
        );
    }

    @Test
    void generatePayrollSummaryData_ShouldHandleEmptyData_WhenNoPayrollsExist() {
        // Given
        Integer companyId = 1;
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 1, 31);

        Company company = Company.builder()
                .id(companyId)
                .companyName("Test Company")
                .build();

        when(companyRepository.findById(companyId)).thenReturn(Optional.of(company));
        when(workerRepository.findAllByCompanyId(companyId)).thenReturn(Collections.emptyList());
        when(workerPayrollRepository.findAllByCompanyIdAndPeriodOverlap(companyId, startDate, endDate))
                .thenReturn(Collections.emptyList());
        when(attendanceRepository.findAllByWorkerIdInAndCheckInTimeBetween(
                anyList(), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(Collections.emptyList());

        // When
        PayrollSummaryReportDTO result = service.generatePayrollSummaryData(companyId, startDate, endDate);

        // Then
        assertNotNull(result);
        assertEquals(companyId, result.getCompanyId());
        assertEquals(BigDecimal.ZERO, result.getTotalGrossPay());
        assertEquals(BigDecimal.ZERO, result.getTotalNetPay());
        assertEquals(0, result.getTotalEmployees());
        assertTrue(result.getEmployeeBreakdown().isEmpty());
    }

    @Test
    void generatePayrollSummaryData_ShouldThrowException_WhenCompanyNotFound() {
        // Given
        Integer companyId = 999;
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 1, 31);

        when(companyRepository.findById(companyId)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                service.generatePayrollSummaryData(companyId, startDate, endDate)
        );

        assertEquals("Company not found", exception.getMessage());
        verify(companyRepository).findById(companyId);
        verifyNoInteractions(workerPayrollRepository, workerRepository, attendanceRepository);
    }
}