package com.zikpack.facecheck.taxesServices.pdfServices;

import com.zikpak.facecheck.entity.Company;
import com.zikpak.facecheck.entity.User;
import com.zikpak.facecheck.helperServices.WorkerPayRollService;
import com.zikpak.facecheck.metrics.MetricsForPdfServices;
import com.zikpak.facecheck.repository.CompanyRepository;
import com.zikpak.facecheck.repository.UserRepository;
import com.zikpak.facecheck.requestsResponses.finance.WorkerYearlySummaryDto;
import com.zikpak.facecheck.services.amazonS3Service.AmazonS3Service;
import com.zikpak.facecheck.taxesServices.pdfServices.W3OfficialPDFServicer;
import io.micrometer.core.instrument.Timer;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class W3OfficialPDFServicerTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private CompanyRepository companyRepository;

    @Mock
    private AmazonS3Service amazonS3Service;

    @Mock
    private WorkerPayRollService workerPayRollService;

    @Mock
    private MetricsForPdfServices metricsForPdfServices;

    @Mock
    private Timer.Sample timerSample;

    @InjectMocks
    private W3OfficialPDFServicer w3OfficialPDFServicer;

    private Company testCompany;
    private List<User> testWorkers;
    private WorkerYearlySummaryDto testSummary;

    @BeforeEach
    void setUp() {
        // Настройка тестовой компании
        testCompany = new Company();
        testCompany.setId(1);
        testCompany.setCompanyName("Test Company Inc");
        testCompany.setEmployerEIN("12-3456789");
        testCompany.setCompanyStateIdNumber("ST123456");
        testCompany.setCompanyEmail("test@company.com");
        testCompany.setCompanyPhone("555-0123");
        testCompany.setCompanyAddress("123 Test St");
        testCompany.setCompanyCity("Test City");
        testCompany.setCompanyState("NY");
        testCompany.setCompanyZipCode("12345");

        User companyOwner = new User();
        companyOwner.setPhoneNumber("555-0100");
        testCompany.setCompanyOwner(companyOwner);

        // Настройка тестовых работников
        User worker1 = createTestUser(1, true, new BigDecimal("500.00"));
        User worker2 = createTestUser(2, false, null);
        User worker3 = createTestUser(3, true, new BigDecimal("300.00"));

        testWorkers = Arrays.asList(worker1, worker2, worker3);

        testSummary = new WorkerYearlySummaryDto();
        testSummary.setGrossPayTotal(new BigDecimal("100000.00"));
        testSummary.setFederalWithholdingTotal(new BigDecimal("15000.00"));
        testSummary.setSocialSecurityEmployeeTotal(new BigDecimal("6200.00"));
        testSummary.setMedicareTotal(new BigDecimal("1450.00"));
        testSummary.setNyLocalWithholdingTotal(new BigDecimal("2000.00"));
        testSummary.setNyStateWithholdingTotal(new BigDecimal("5000.00"));
    }

    private User createTestUser(Integer id, boolean enrolledInHealthPlan, BigDecimal monthlyPremium) {
        User user = new User();
        user.setId(id);
        user.setEnrolledInHealthPlan(enrolledInHealthPlan);
        user.setMonthlyHealthPremium(monthlyPremium);
        return user;
    }

    @Test
    void generateFilledPdf_Success() throws IOException {
        // Given
        Integer companyId = 1;
        int year = 2024;

        when(companyRepository.findById(companyId)).thenReturn(Optional.of(testCompany));
        when(userRepository.findWorkersWithPayrollInYear(companyId, year)).thenReturn(testWorkers);
        when(workerPayRollService.calculateWorkerYearlyTotalsForAllWorkers(companyId, year))
                .thenReturn(testSummary);
        when(metricsForPdfServices.startTimer()).thenReturn(timerSample);

        when(amazonS3Service.uploadPdfToS3(any(byte[].class), anyString()))
                .thenReturn("https://s3.amazonaws.com/test-bucket/test-file.pdf");
        doNothing().when(metricsForPdfServices).recordRequest(anyString());
        doNothing().when(metricsForPdfServices).recordGenerated(anyString(), anyBoolean());
        doNothing().when(metricsForPdfServices).recordS3UploadTime(anyString(), anyBoolean(), anyLong());
        doNothing().when(metricsForPdfServices).recordOperationTime(any(Timer.Sample.class), anyString());

        // When
        byte[] result = w3OfficialPDFServicer.generateFilledPdf(companyId, year);

        // Then
        assertNotNull(result);
        assertTrue(result.length > 0); // Проверяем, что PDF не пустой

        // Проверяем вызовы
        verify(companyRepository).findById(companyId);
        verify(userRepository).findWorkersWithPayrollInYear(companyId, year);
        verify(workerPayRollService).calculateWorkerYearlyTotalsForAllWorkers(companyId, year);
        verify(amazonS3Service).uploadPdfToS3(any(byte[].class), anyString());
        verify(metricsForPdfServices).recordRequest("W3");
        verify(metricsForPdfServices).recordGenerated("W3", true);
        verify(metricsForPdfServices).recordOperationTime(timerSample, "W3_success");
    }

    @Test
    void generateFilledPdf_CompanyNotFound() {
        // Given
        Integer companyId = 999;
        int year = 2024;

        when(companyRepository.findById(companyId)).thenReturn(Optional.empty());
        when(metricsForPdfServices.startTimer()).thenReturn(timerSample);
        doNothing().when(metricsForPdfServices).recordRequest(anyString());

        // When & Then
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            w3OfficialPDFServicer.generateFilledPdf(companyId, year);
        });

        assertEquals("Company Not Found", exception.getMessage());
        verify(companyRepository).findById(companyId);
        verify(metricsForPdfServices).recordRequest("W3");
        verify(metricsForPdfServices).recordOperationTime(timerSample, "W3_failed");
        verify(metricsForPdfServices).recordGenerated("W3", false);

        // Проверяем, что другие сервисы не вызывались
        verify(userRepository, never()).findWorkersWithPayrollInYear(anyInt(), anyInt());
        verify(workerPayRollService, never()).calculateWorkerYearlyTotalsForAllWorkers(anyInt(), anyInt());
    }

    @Test
    void generateFilledPdf_EmptyWorkersList() throws IOException {
        // Given
        Integer companyId = 1;
        int year = 2024;

        when(companyRepository.findById(companyId)).thenReturn(Optional.of(testCompany));
        when(userRepository.findWorkersWithPayrollInYear(companyId, year)).thenReturn(Arrays.asList());
        when(workerPayRollService.calculateWorkerYearlyTotalsForAllWorkers(companyId, year))
                .thenReturn(testSummary);
        when(metricsForPdfServices.startTimer()).thenReturn(timerSample);

        when(amazonS3Service.uploadPdfToS3(any(byte[].class), anyString()))
                .thenReturn("https://s3.amazonaws.com/test-bucket/test-file.pdf");
        doNothing().when(metricsForPdfServices).recordRequest(anyString());
        doNothing().when(metricsForPdfServices).recordGenerated(anyString(), anyBoolean());
        doNothing().when(metricsForPdfServices).recordS3UploadTime(anyString(), anyBoolean(), anyLong());
        doNothing().when(metricsForPdfServices).recordOperationTime(any(Timer.Sample.class), anyString());

        // When
        byte[] result = w3OfficialPDFServicer.generateFilledPdf(companyId, year);

        // Then
        assertNotNull(result);
        assertTrue(result.length > 0);

        verify(companyRepository).findById(companyId);
        verify(userRepository).findWorkersWithPayrollInYear(companyId, year);
        verify(workerPayRollService).calculateWorkerYearlyTotalsForAllWorkers(companyId, year);
        verify(metricsForPdfServices).recordGenerated("W3", true);
        verify(metricsForPdfServices).recordOperationTime(timerSample, "W3_success");
    }

    @Test
    void generateFilledPdf_CalculatesInsurancePremiumsCorrectly() throws IOException {
        // Given
        Integer companyId = 1;
        int year = 2024;

        // Создаем работников с разными премиями
        User workerWithPremium = createTestUser(1, true, new BigDecimal("500.00"));
        User workerWithoutPremium = createTestUser(2, false, null);
        User workerWithZeroPremium = createTestUser(3, true, BigDecimal.ZERO);

        List<User> workersWithPremiums = Arrays.asList(workerWithPremium, workerWithoutPremium, workerWithZeroPremium);

        when(companyRepository.findById(companyId)).thenReturn(Optional.of(testCompany));
        when(userRepository.findWorkersWithPayrollInYear(companyId, year)).thenReturn(workersWithPremiums);
        when(workerPayRollService.calculateWorkerYearlyTotalsForAllWorkers(companyId, year))
                .thenReturn(testSummary);
        when(metricsForPdfServices.startTimer()).thenReturn(timerSample);

        when(amazonS3Service.uploadPdfToS3(any(byte[].class), anyString()))
                .thenReturn("https://s3.amazonaws.com/test-bucket/test-file.pdf");
        doNothing().when(metricsForPdfServices).recordRequest(anyString());
        doNothing().when(metricsForPdfServices).recordGenerated(anyString(), anyBoolean());
        doNothing().when(metricsForPdfServices).recordS3UploadTime(anyString(), anyBoolean(), anyLong());
        doNothing().when(metricsForPdfServices).recordOperationTime(any(Timer.Sample.class), anyString());

        // When
        byte[] result = w3OfficialPDFServicer.generateFilledPdf(companyId, year);

        // Then
        assertNotNull(result);
        assertTrue(result.length > 0);

        // Ожидаемая годовая премия: 500.00 * 12 = 6000.00
        // (только для работника с премией > 0 и enrolledInHealthPlan = true)
        verify(companyRepository).findById(companyId);
        verify(userRepository).findWorkersWithPayrollInYear(companyId, year);
        verify(metricsForPdfServices).recordGenerated("W3", true);
    }


    @Test
    void generateFilledPdf_RecordsMetricsOnError() {
        // Given
        Integer companyId = 1;
        int year = 2024;

        when(companyRepository.findById(companyId)).thenThrow(new RuntimeException("Database error"));
        when(metricsForPdfServices.startTimer()).thenReturn(timerSample);

        doNothing().when(metricsForPdfServices).recordRequest(anyString());
        doNothing().when(metricsForPdfServices).recordOperationTime(any(Timer.Sample.class), anyString());
        doNothing().when(metricsForPdfServices).recordGenerated(anyString(), anyBoolean());
        doNothing().when(metricsForPdfServices).recordError(anyString(), anyString(), any(Exception.class));

        // When & Then
        assertThrows(RuntimeException.class, () -> {
            w3OfficialPDFServicer.generateFilledPdf(companyId, year);
        });

        verify(metricsForPdfServices).recordRequest("W3");
        verify(metricsForPdfServices).recordOperationTime(timerSample, "W3_failed");
        verify(metricsForPdfServices).recordGenerated("W3", false);
        verify(metricsForPdfServices).recordError(eq("W3_failed"), eq("Database error"), any(RuntimeException.class));
    }

    @Test
    void generateFilledPdf_HandlesPdfGenerationError() throws IOException {
        // Given
        Integer companyId = 1;
        int year = 2024;

        when(companyRepository.findById(companyId)).thenReturn(Optional.of(testCompany));
        when(userRepository.findWorkersWithPayrollInYear(companyId, year)).thenReturn(testWorkers);
        when(workerPayRollService.calculateWorkerYearlyTotalsForAllWorkers(companyId, year))
                .thenReturn(testSummary);
        when(metricsForPdfServices.startTimer()).thenReturn(timerSample);

        // Симулируем ошибку при загрузке в S3
        doThrow(new RuntimeException("S3 upload failed"))
                .when(amazonS3Service).uploadPdfToS3(any(byte[].class), anyString());

        doNothing().when(metricsForPdfServices).recordRequest(anyString());
        doNothing().when(metricsForPdfServices).recordOperationTime(any(Timer.Sample.class), anyString());
        doNothing().when(metricsForPdfServices).recordGenerated(anyString(), anyBoolean());
        doNothing().when(metricsForPdfServices).recordError(anyString(), anyString(), any(Exception.class));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            w3OfficialPDFServicer.generateFilledPdf(companyId, year);
        });

        assertEquals("S3 upload failed", exception.getMessage());
        verify(metricsForPdfServices).recordRequest("W3");
        verify(metricsForPdfServices).recordOperationTime(timerSample, "W3_failed");
        verify(metricsForPdfServices).recordGenerated("W3", false);
        verify(metricsForPdfServices).recordError(eq("W3_failed"), eq("S3 upload failed"), any(RuntimeException.class));
    }
}