package com.zikpack.facecheck.taxesServices.customReportsForCompany;

import com.zikpak.facecheck.entity.Company;
import com.zikpak.facecheck.entity.User;
import com.zikpak.facecheck.entity.employee.WorkerAttendance;
import com.zikpak.facecheck.repository.CompanyRepository;
import com.zikpak.facecheck.repository.UserRepository;
import com.zikpak.facecheck.repository.WorkerAttendanceRepository;
import com.zikpak.facecheck.taxesServices.customReportsForCompanys.hoursReport.HoursReportDTO;
import com.zikpak.facecheck.taxesServices.customReportsForCompanys.hoursReport.HoursReportDataService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@ExtendWith(MockitoExtension.class)
class HoursReportDataServiceTest {

    @Mock
    private WorkerAttendanceRepository attendanceRepository;

    @Mock
    private CompanyRepository companyRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private HoursReportDataService hoursReportDataService;

    private Company testCompany;
    private List<WorkerAttendance> testAttendances;
    private List<User> testWorkers;

    @BeforeEach
    void setUp() {
        testCompany = Company.builder()
                .id(1)
                .companyName("Test Company")
                .companyAddress("123 Test St")
                .companyCity("Test City")
                .companyState("Test State")
                .companyZipCode("12345")
                .companyPhone("555-1234")
                .build();

        User worker1 = User.builder()
                .id(1)
                .firstName("John")
                .lastName("Doe")
                .baseHourlyRate(new BigDecimal("25.00"))
                .build();

        User worker2 = User.builder()
                .id(2)
                .firstName("Jane")
                .lastName("Smith")
                .baseHourlyRate(new BigDecimal("30.00"))
                .build();

        testWorkers = Arrays.asList(worker1, worker2);

        WorkerAttendance attendance1 = WorkerAttendance.builder()
                .id(1)
                .worker(worker1)
                .checkInTime(LocalDateTime.of(2024, 1, 15, 8, 0))
                .checkOutTime(LocalDateTime.of(2024, 1, 15, 17, 0))
                .build();

        WorkerAttendance attendance2 = WorkerAttendance.builder()
                .id(2)
                .worker(worker2)
                .checkInTime(LocalDateTime.of(2024, 1, 15, 9, 0))
                .checkOutTime(LocalDateTime.of(2024, 1, 15, 18, 0))
                .build();

        testAttendances = Arrays.asList(attendance1, attendance2);
    }

    @Test
    @DisplayName("Успешное генерирование отчета с корректными данными")
    void generateHoursReportData_Success() {
        // Given
        Integer companyId = 1;
        LocalDate startDate = LocalDate.of(2024, 1, 15);
        LocalDate endDate = LocalDate.of(2024, 1, 15);

        when(companyRepository.findById(companyId)).thenReturn(Optional.of(testCompany));
        when(attendanceRepository.findAllByCompanyIdAndCheckInTimeBetween(
                eq(companyId),
                eq(startDate.atStartOfDay()),
                eq(endDate.atTime(23, 59, 59))
        )).thenReturn(testAttendances);
        when(userRepository.findAllById(any(Set.class))).thenReturn(testWorkers);

        // When
        HoursReportDTO result = hoursReportDataService.generateHoursReportData(
                companyId, startDate, endDate);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getCompanyName()).isEqualTo("Test Company");
        assertThat(result.getCompanyAddress()).isEqualTo("123 Test St");
        assertThat(result.getPeriodStart()).isEqualTo(startDate);
        assertThat(result.getPeriodEnd()).isEqualTo(endDate);
        assertThat(result.getTotalEmployees()).isEqualTo(2);

        verify(companyRepository).findById(companyId);
        verify(attendanceRepository).findAllByCompanyIdAndCheckInTimeBetween(
                eq(companyId),
                eq(startDate.atStartOfDay()),
                eq(endDate.atTime(23, 59, 59))
        );
        verify(userRepository).findAllById(Set.of(1, 2));
    }

    @Test
    @DisplayName("Исключение при отсутствии компании")
    void generateHoursReportData_CompanyNotFound_ThrowsException() {
        // Given
        Integer companyId = 999;
        LocalDate startDate = LocalDate.of(2024, 1, 15);
        LocalDate endDate = LocalDate.of(2024, 1, 15);

        when(companyRepository.findById(companyId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> hoursReportDataService.generateHoursReportData(
                companyId, startDate, endDate))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Company not found");

        verify(companyRepository).findById(companyId);
        verifyNoInteractions(attendanceRepository);
        verifyNoInteractions(userRepository);
    }

    @Test
    @DisplayName("Генерирование отчета с пустыми данными посещаемости")
    void generateHoursReportData_EmptyAttendances_Success() {
        // Given
        Integer companyId = 1;
        LocalDate startDate = LocalDate.of(2024, 1, 15);
        LocalDate endDate = LocalDate.of(2024, 1, 15);

        when(companyRepository.findById(companyId)).thenReturn(Optional.of(testCompany));
        when(attendanceRepository.findAllByCompanyIdAndCheckInTimeBetween(
                eq(companyId),
                eq(startDate.atStartOfDay()),
                eq(endDate.atTime(23, 59, 59))
        )).thenReturn(Collections.emptyList());
        when(userRepository.findAllById(any(Set.class))).thenReturn(Collections.emptyList());

        // When
        HoursReportDTO result = hoursReportDataService.generateHoursReportData(
                companyId, startDate, endDate);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getCompanyName()).isEqualTo("Test Company");
        assertThat(result.getTotalEmployees()).isZero();
        assertThat(result.getTotalHours()).isZero();
        assertThat(result.getEmployeeHours()).isEmpty();

        verify(companyRepository).findById(companyId);
        verify(attendanceRepository).findAllByCompanyIdAndCheckInTimeBetween(
                eq(companyId),
                eq(startDate.atStartOfDay()),
                eq(endDate.atTime(23, 59, 59))
        );
        verify(userRepository).findAllById(Collections.emptySet());
    }

    @Test
    @DisplayName("Корректное вычисление временного периода запроса")
    void generateHoursReportData_CorrectDateTimeRange() {
        // Given
        Integer companyId = 1;
        LocalDate startDate = LocalDate.of(2024, 1, 10);
        LocalDate endDate = LocalDate.of(2024, 1, 20);

        when(companyRepository.findById(companyId)).thenReturn(Optional.of(testCompany));
        when(attendanceRepository.findAllByCompanyIdAndCheckInTimeBetween(
                any(), any(), any()
        )).thenReturn(testAttendances);
        when(userRepository.findAllById(any(Set.class))).thenReturn(testWorkers);

        // When
        hoursReportDataService.generateHoursReportData(companyId, startDate, endDate);

        // Then
        verify(attendanceRepository).findAllByCompanyIdAndCheckInTimeBetween(
                eq(companyId),
                eq(LocalDateTime.of(2024, 1, 10, 0, 0, 0)),
                eq(LocalDateTime.of(2024, 1, 20, 23, 59, 59))
        );
    }

    @Test
    @DisplayName("Обработка пользователей с null hourlyRate")
    void generateHoursReportData_WithNullHourlyRate() {
        // Given
        Integer companyId = 1;
        LocalDate startDate = LocalDate.of(2024, 1, 15);
        LocalDate endDate = LocalDate.of(2024, 1, 15);

        // Создаем пользователя с null hourlyRate
        User workerWithNullRate = User.builder()
                .id(3)
                .firstName("Bob")
                .lastName("Johnson")
                .baseHourlyRate(null)
                .build();

        List<User> workersWithNullRate = Arrays.asList(workerWithNullRate);

        WorkerAttendance attendanceWithNullRate = WorkerAttendance.builder()
                .id(3)
                .worker(workerWithNullRate)
                .checkInTime(LocalDateTime.of(2024, 1, 15, 8, 0))
                .checkOutTime(LocalDateTime.of(2024, 1, 15, 17, 0))
                .build();

        when(companyRepository.findById(companyId)).thenReturn(Optional.of(testCompany));
        when(attendanceRepository.findAllByCompanyIdAndCheckInTimeBetween(
                eq(companyId),
                eq(startDate.atStartOfDay()),
                eq(endDate.atTime(23, 59, 59))
        )).thenReturn(Arrays.asList(attendanceWithNullRate));
        when(userRepository.findAllById(any(Set.class))).thenReturn(workersWithNullRate);

        // When & Then
        assertThatThrownBy(() -> hoursReportDataService.generateHoursReportData(
                companyId, startDate, endDate))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("hourlyRate");
    }
}