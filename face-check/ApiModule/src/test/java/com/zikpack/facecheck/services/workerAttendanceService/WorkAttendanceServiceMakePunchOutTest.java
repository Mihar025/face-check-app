package com.zikpack.facecheck.services.workerAttendanceService;

import com.zikpak.facecheck.entity.Company;
import com.zikpak.facecheck.entity.User;
import com.zikpak.facecheck.entity.employee.WorkSite;
import com.zikpak.facecheck.entity.employee.WorkerAttendance;
import com.zikpak.facecheck.entity.employee.WorkerPayroll;
import com.zikpak.facecheck.entity.employee.WorkerSchedule;
import com.zikpak.facecheck.metrics.MetricsService;
import com.zikpak.facecheck.repository.*;
import com.zikpak.facecheck.requestsResponses.attendance.PunchOutRequest;
import com.zikpak.facecheck.requestsResponses.attendance.PunchOutResponse;
import com.zikpak.facecheck.requestsResponses.finance.PayStubResponse;
import com.zikpak.facecheck.services.amazonS3Service.AmazonS3Service;
import com.zikpak.facecheck.services.workAttendanceService.WorkAttendanceService;
import com.zikpak.facecheck.services.workSiteService.WorkSiteService;
import com.zikpak.facecheck.taxesServices.calculators.FinanceCalculator;
import com.zikpak.facecheck.taxesServices.services.sickDayService.SickLeaveService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WorkAttendanceServiceMakePunchOutTest {

    @Mock
    private WorkerAttendanceRepository workerAttendanceRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private WorkSiteService workSiteService;

    @Mock
    private AmazonS3Service amazonS3Service;

    @Mock
    private WorkerSiteRepository workSiteRepository;

    @Mock
    private WorkerPayrollRepository workerPayrollRepository;

    @Mock
    private WorkerScheduleRepository workerScheduleRepository;

    @Mock
    private FinanceCalculator financeCalculator;

    @Mock
    private SickLeaveService sickLeaveService;

    @Mock
    private MetricsService metricsService;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private WorkAttendanceService workAttendanceService;

    private User testUser;
    private WorkSite testWorkSite;
    private WorkerAttendance existingAttendance;
    private WorkerSchedule testSchedule;
    private PunchOutRequest punchOutRequest;

    @BeforeEach
    void setUp() {
        // Создаем тестовые данные
        testUser = new User();
        testUser.setId(1);
        testUser.setEmail("test@example.com");
        testUser.setFirstName("John");
        testUser.setLastName("Doe");
        testUser.setBaseHourlyRate(BigDecimal.valueOf(25.0));

        testWorkSite = new WorkSite();
        testWorkSite.setId(1);
        testWorkSite.setSiteName("Test Site");
        testWorkSite.setAddress("123 Test St");
        testWorkSite.setIsActive(true);

        testSchedule = new WorkerSchedule();
        testSchedule.setExpectedStartTime(LocalTime.of(9, 0));
        testSchedule.setExpectedEndTime(LocalTime.of(17, 0));
        testSchedule.setStartLunch(LocalDateTime.now().withHour(12).withMinute(0));
        testSchedule.setEndLunch(LocalDateTime.now().withHour(13).withMinute(0));
        testSchedule.setIsCompanyPayingLunch(false);

        existingAttendance = new WorkerAttendance();
        existingAttendance.setId(1);
        existingAttendance.setWorker(testUser);
        existingAttendance.setCheckInTime(LocalDateTime.now().withHour(9).withMinute(0));
        existingAttendance.setCheckInPhotoUrl("check-in-photo.jpg");
        existingAttendance.setCheckInLatitude(40.7128);
        existingAttendance.setCheckInLongitude(-74.0060);
        existingAttendance.setCheckInLocation(testWorkSite.getAddress());

        punchOutRequest = new PunchOutRequest();
        punchOutRequest.setWorkSiteId(1);
        punchOutRequest.setLatitude(40.7128);
        punchOutRequest.setLongitude(-74.0060);
        punchOutRequest.setPhotoBase64("base64PhotoData");

        // Настройка mock для метрик
        when(metricsService.startTimer()).thenReturn(mock(io.micrometer.core.instrument.Timer.Sample.class));
    }

    @Test
    void testSuccessfulPunchOut() {
        // Given
        // Создаем Company для теста ПЕРЕД установкой в user
        Company testCompany = new Company();
        testCompany.setCompanyName("Test Company");
        testUser.setCompany(testCompany);

        when(authentication.getPrincipal()).thenReturn(testUser);
        when(userRepository.findByEmail(testUser.getEmail())).thenReturn(Optional.of(testUser));
        when(workerAttendanceRepository.findTodayActivePunchIn(any(), any(), any()))
                .thenReturn(Optional.of(existingAttendance));
        when(workSiteRepository.findById(punchOutRequest.getWorkSiteId()))
                .thenReturn(Optional.of(testWorkSite));
        when(workerScheduleRepository.findByWorkerAndScheduleDate(any(), any()))
                .thenReturn(Optional.of(testSchedule));
        when(workSiteService.isWithinRadiusForPunchInOut(anyInt(), anyDouble(), anyDouble()))
                .thenReturn(true);
        when(amazonS3Service.uploadAttendancePhoto(anyString(), anyString(), anyString()))
                .thenReturn("punch-out-photo-url");

        // Настраиваем mock для save чтобы возвращать attendance с установленными значениями
        when(workerAttendanceRepository.save(any(WorkerAttendance.class)))
                .thenAnswer(invocation -> {
                    WorkerAttendance saved = invocation.getArgument(0);
                    // Устанавливаем значения которые должны быть после сохранения
                    if (saved.getGrossPayPerDay() == null) {
                        saved.setGrossPayPerDay(BigDecimal.valueOf(200));
                    }
                    if (saved.getNetPay() == null) {
                        saved.setNetPay(BigDecimal.valueOf(150));
                    }
                    return saved;
                });

        WorkerPayroll mockPayroll = new WorkerPayroll();
        mockPayroll.setWorker(testUser); // ВАЖНО: устанавливаем worker
        mockPayroll.setGrossPay(BigDecimal.valueOf(200));
        mockPayroll.setNetPay(BigDecimal.valueOf(150));
        mockPayroll.setRegularHours(8.0);
        mockPayroll.setOvertimeHours(0.0);
        mockPayroll.setTotalDeductions(BigDecimal.valueOf(50));
        mockPayroll.setPeriodStart(LocalDate.now());
        mockPayroll.setPeriodEnd(LocalDate.now());
        mockPayroll.setBaseHourlyRate(BigDecimal.valueOf(25.0));
        mockPayroll.setOvertimeRate(BigDecimal.valueOf(37.5));

        when(workerPayrollRepository.findFirstByWorkerAndPeriodStartLessThanEqualAndPeriodEndGreaterThanEqualOrderByPeriodEndDesc(any(), any(), any()))
                .thenReturn(Optional.of(mockPayroll));
        when(workerPayrollRepository.save(any(WorkerPayroll.class)))
                .thenReturn(mockPayroll);

        // Мокаем поиск всех attendance за период
        when(workerAttendanceRepository.findAllByWorkerIdAndCheckInTimeBetween(anyInt(), any(), any()))
                .thenReturn(Collections.singletonList(existingAttendance));

        PayStubResponse mockTaxResponse = new PayStubResponse();
        mockTaxResponse.setGrossPay(BigDecimal.valueOf(200));
        mockTaxResponse.setNetPay(BigDecimal.valueOf(150));
        mockTaxResponse.setTotalDeductions(BigDecimal.valueOf(50));
        mockTaxResponse.setMedicare(BigDecimal.ZERO);
        mockTaxResponse.setSocialSecurity(BigDecimal.ZERO);
        mockTaxResponse.setFederalTax(BigDecimal.ZERO);
        mockTaxResponse.setStateTax(BigDecimal.ZERO);
        mockTaxResponse.setNycTax(BigDecimal.ZERO);
        mockTaxResponse.setDisability(BigDecimal.ZERO);
        mockTaxResponse.setPfl(BigDecimal.ZERO);

        when(financeCalculator.calculateNetPayWithSeparateHours(any(), any(), any(), anyDouble(), anyDouble(), any(), any(), any()))
                .thenReturn(mockTaxResponse);
        when(financeCalculator.calculateGrossPay(any(), any(), anyDouble(), anyDouble()))
                .thenReturn(BigDecimal.valueOf(200));

        // Мокаем YTD методы
        when(workerPayrollRepository.findAllByWorkerIdAndPeriodEndLessThanEqual(anyInt(), any()))
                .thenReturn(Collections.emptyList());

        // When
        PunchOutResponse response = workAttendanceService.makePunchOut(authentication, punchOutRequest);

        // Then
        assertTrue(response.getIsSuccessful());
        assertEquals("Punch out successful", response.getMessage());
        assertEquals(testUser.getId(), response.getWorkerId());
        assertEquals(testWorkSite.getId(), response.getWorkSiteId());
        assertEquals(testWorkSite.getSiteName(), response.getWorkSiteName());
        assertNotNull(response.getCheckOutTime());
        assertNotNull(response.getHoursWorked());

        // Проверяем что save был вызван 3 раза - это нормальное поведение
        verify(workerAttendanceRepository, times(3)).save(any(WorkerAttendance.class));

        // Проверяем остальные вызовы
        verify(sickLeaveService).accrueSickLeave(eq(testUser.getId()), anyDouble());
        verify(metricsService).recordPunchOut(anyString(), anyString(), anyString(), eq(true), anyDouble(), anyDouble());
        verify(metricsService).recordPayrollCalculations(any(), any(), any());
    }

    @Test
    void testPunchOutWithNoActivePunchIn() {
        // Given
        when(authentication.getPrincipal()).thenReturn(testUser);
        when(userRepository.findByEmail(testUser.getEmail())).thenReturn(Optional.of(testUser));
        when(workerAttendanceRepository.findTodayActivePunchIn(any(), any(), any()))
                .thenReturn(Optional.empty());

        // When
        PunchOutResponse response = workAttendanceService.makePunchOut(authentication, punchOutRequest);

        // Then
        assertFalse(response.getIsSuccessful());
        assertTrue(response.getMessage().contains("No active punch in found for today!"));
        verify(metricsService).recordPunchOut(eq("unknown"), eq("unknown"), eq("unknown"), eq(false), eq(0.0), eq(0.0));
    }

    @Test
    void testPunchOutNotInRadius() {
        // Given
        when(authentication.getPrincipal()).thenReturn(testUser);
        when(userRepository.findByEmail(testUser.getEmail())).thenReturn(Optional.of(testUser));
        when(workerAttendanceRepository.findTodayActivePunchIn(any(), any(), any()))
                .thenReturn(Optional.of(existingAttendance));
        when(workSiteRepository.findById(punchOutRequest.getWorkSiteId()))
                .thenReturn(Optional.of(testWorkSite));
        when(workerScheduleRepository.findByWorkerAndScheduleDate(any(), any()))
                .thenReturn(Optional.of(testSchedule));
        when(workSiteService.isWithinRadiusForPunchInOut(anyInt(), anyDouble(), anyDouble()))
                .thenReturn(false);

        // When
        PunchOutResponse response = workAttendanceService.makePunchOut(authentication, punchOutRequest);

        // Then
        assertFalse(response.getIsSuccessful());
        assertTrue(response.getMessage().contains("not in allowed radius"));
    }

    @Test
    void testPunchOutWithLatePunchOut() {
        // Given - настройка для позднего punch out (после 17:00)
        LocalDateTime lateCheckOut = LocalDateTime.now().withHour(18).withMinute(30);

        // Создаем Company для теста
        Company testCompany = new Company();
        testCompany.setCompanyName("Test Company");
        testUser.setCompany(testCompany);

        when(authentication.getPrincipal()).thenReturn(testUser);
        when(userRepository.findByEmail(testUser.getEmail())).thenReturn(Optional.of(testUser));
        when(workerAttendanceRepository.findTodayActivePunchIn(any(), any(), any()))
                .thenReturn(Optional.of(existingAttendance));
        when(workSiteRepository.findById(punchOutRequest.getWorkSiteId()))
                .thenReturn(Optional.of(testWorkSite));
        when(workerScheduleRepository.findByWorkerAndScheduleDate(any(), any()))
                .thenReturn(Optional.of(testSchedule));
        when(workSiteService.isWithinRadiusForPunchInOut(anyInt(), anyDouble(), anyDouble()))
                .thenReturn(true);
        when(amazonS3Service.uploadAttendancePhoto(anyString(), anyString(), anyString()))
                .thenReturn("punch-out-photo-url");

        // Настраиваем mock для сохранения attendance с overtime
        existingAttendance.setCheckOutTime(lateCheckOut);
        existingAttendance.setHoursWorked(8.0);
        existingAttendance.setOvertimeHours(1.5);
        existingAttendance.setGrossPayPerDay(BigDecimal.valueOf(237.5));
        when(workerAttendanceRepository.save(any(WorkerAttendance.class)))
                .thenReturn(existingAttendance);

        WorkerPayroll mockPayroll = new WorkerPayroll();
        mockPayroll.setWorker(testUser); // ВАЖНО: устанавливаем worker
        mockPayroll.setGrossPay(BigDecimal.valueOf(237.5)); // 8 * 25 + 1.5 * 37.5
        mockPayroll.setNetPay(BigDecimal.valueOf(178.125));
        mockPayroll.setRegularHours(8.0);
        mockPayroll.setOvertimeHours(1.5);
        mockPayroll.setTotalDeductions(BigDecimal.valueOf(59.375));
        mockPayroll.setBaseHourlyRate(BigDecimal.valueOf(25.0));
        mockPayroll.setOvertimeRate(BigDecimal.valueOf(37.5));
        mockPayroll.setPeriodStart(LocalDate.now());
        mockPayroll.setPeriodEnd(LocalDate.now());

        when(workerPayrollRepository.findFirstByWorkerAndPeriodStartLessThanEqualAndPeriodEndGreaterThanEqualOrderByPeriodEndDesc(any(), any(), any()))
                .thenReturn(Optional.of(mockPayroll));
        when(workerPayrollRepository.save(any(WorkerPayroll.class)))
                .thenReturn(mockPayroll);

        // Мокаем поиск всех attendance за период
        when(workerAttendanceRepository.findAllByWorkerIdAndCheckInTimeBetween(anyInt(), any(), any()))
                .thenReturn(Collections.singletonList(existingAttendance));

        PayStubResponse mockTaxResponse = new PayStubResponse();
        mockTaxResponse.setGrossPay(BigDecimal.valueOf(237.5));
        mockTaxResponse.setNetPay(BigDecimal.valueOf(178.125));
        mockTaxResponse.setTotalDeductions(BigDecimal.valueOf(59.375));
        mockTaxResponse.setMedicare(BigDecimal.ZERO);
        mockTaxResponse.setSocialSecurity(BigDecimal.ZERO);
        mockTaxResponse.setFederalTax(BigDecimal.ZERO);
        mockTaxResponse.setStateTax(BigDecimal.ZERO);
        mockTaxResponse.setNycTax(BigDecimal.ZERO);
        mockTaxResponse.setDisability(BigDecimal.ZERO);
        mockTaxResponse.setPfl(BigDecimal.ZERO);

        when(financeCalculator.calculateNetPayWithSeparateHours(any(), any(), any(), anyDouble(), anyDouble(), any(), any(), any()))
                .thenReturn(mockTaxResponse);
        when(financeCalculator.calculateGrossPay(any(), any(), anyDouble(), anyDouble()))
                .thenReturn(BigDecimal.valueOf(237.5));

        // When
        PunchOutResponse response = workAttendanceService.makePunchOut(authentication, punchOutRequest);

        // Then
        assertTrue(response.getIsSuccessful());
        assertNotNull(response.getOvertimeHours());
      //  verify(metricsService).recordLatePunchOut(anyString(), anyLong());
    }

    @Test
    void testPunchOutWithPhotoUploadFailure() {
        // Given
        when(authentication.getPrincipal()).thenReturn(testUser);
        when(userRepository.findByEmail(testUser.getEmail())).thenReturn(Optional.of(testUser));
        when(workerAttendanceRepository.findTodayActivePunchIn(any(), any(), any()))
                .thenReturn(Optional.of(existingAttendance));
        when(workSiteRepository.findById(punchOutRequest.getWorkSiteId()))
                .thenReturn(Optional.of(testWorkSite));
        when(workerScheduleRepository.findByWorkerAndScheduleDate(any(), any()))
                .thenReturn(Optional.of(testSchedule));
        when(workSiteService.isWithinRadiusForPunchInOut(anyInt(), anyDouble(), anyDouble()))
                .thenReturn(true);
        when(amazonS3Service.uploadAttendancePhoto(anyString(), anyString(), anyString()))
                .thenThrow(new RuntimeException("Failed to upload photo"));

        // When
        PunchOutResponse response = workAttendanceService.makePunchOut(authentication, punchOutRequest);

        // Then
        assertFalse(response.getIsSuccessful());
        assertTrue(response.getMessage().contains("Failed to upload photo"));
    }

    @Test
    void testPunchOutWithNoSchedule() {
        // Given
        when(authentication.getPrincipal()).thenReturn(testUser);
        when(userRepository.findByEmail(testUser.getEmail())).thenReturn(Optional.of(testUser));
        when(workerAttendanceRepository.findTodayActivePunchIn(any(), any(), any()))
                .thenReturn(Optional.of(existingAttendance));
        when(workSiteRepository.findById(punchOutRequest.getWorkSiteId()))
                .thenReturn(Optional.of(testWorkSite));
        when(workerScheduleRepository.findByWorkerAndScheduleDate(any(), any()))
                .thenReturn(Optional.empty());

        // When
        PunchOutResponse response = workAttendanceService.makePunchOut(authentication, punchOutRequest);

        // Then
        assertFalse(response.getIsSuccessful());
        assertTrue(response.getMessage().contains("No schedule found"));
    }

    @Test
    void testPunchOutWithInactiveWorkSite() {
        // Given
        testWorkSite.setIsActive(false);
        when(authentication.getPrincipal()).thenReturn(testUser);
        when(userRepository.findByEmail(testUser.getEmail())).thenReturn(Optional.of(testUser));
        when(workerAttendanceRepository.findTodayActivePunchIn(any(), any(), any()))
                .thenReturn(Optional.of(existingAttendance));
        when(workSiteRepository.findById(punchOutRequest.getWorkSiteId()))
                .thenReturn(Optional.of(testWorkSite));

        // When
        PunchOutResponse response = workAttendanceService.makePunchOut(authentication, punchOutRequest);

        // Then
        assertFalse(response.getIsSuccessful());
        assertTrue(response.getMessage().contains("Work site is not active"));
    }


}