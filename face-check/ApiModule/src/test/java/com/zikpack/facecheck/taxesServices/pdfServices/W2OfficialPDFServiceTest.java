package com.zikpack.facecheck.taxesServices.pdfServices;

import com.zikpak.facecheck.entity.Company;
import com.zikpak.facecheck.entity.User;
import com.zikpak.facecheck.entity.employee.WorkerPayroll;
import com.zikpak.facecheck.metrics.MetricsForPdfServices;
import com.zikpak.facecheck.repository.CompanyRepository;
import com.zikpak.facecheck.repository.UserRepository;
import com.zikpak.facecheck.repository.WorkerPayrollRepository;
import com.zikpak.facecheck.services.amazonS3Service.AmazonS3Service;
import com.zikpak.facecheck.taxesServices.pdfServices.W2OfficialPDFService;
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
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class W2OfficialPDFServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private CompanyRepository companyRepository;

    @Mock
    private AmazonS3Service amazonS3Service;

    @Mock
    private WorkerPayrollRepository workerPayrollRepository;

    @Mock
    private MetricsForPdfServices metricsForPdfServices;

    @Mock
    private Timer.Sample timerSample;

    @InjectMocks
    private W2OfficialPDFService w2OfficialPDFService;

    private User testUser;
    private Company testCompany;
    private WorkerPayroll testPayroll1;
    private WorkerPayroll testPayroll2;

    @BeforeEach
    void setUp() {
        // Создаем тестового пользователя
        testUser = new User();
        testUser.setId(1);
        testUser.setFirstName("John");
        testUser.setLastName("Doe");
        testUser.setMiddleInitial("M");
        testUser.setSSN_WORKER("123-45-6789");
        testUser.setHomeAddress("123 Main St");
        testUser.setApt("2A");
        testUser.setCity("New York");
        testUser.setState("NY");
        testUser.setZipcode("10001");
        testUser.setEnrolledInHealthPlan(true);
        testUser.setMonthlyHealthPremium(new BigDecimal("250.00"));

        // Создаем тестовую компанию
        testCompany = new Company();
        testCompany.setId(1);
        testCompany.setCompanyName("Test Company Inc");
        testCompany.setEmployerEIN("12-3456789");
        testCompany.setCompanyAddress("456 Business Ave");
        testCompany.setCompanyCity("New York");
        testCompany.setCompanyState("NY");
        testCompany.setCompanyZipCode("10002");
        testCompany.setCompanyStateIdNumber("NY-123456");

        // Создаем тестовые записи о зарплате
        testPayroll1 = new WorkerPayroll();
        testPayroll1.setGrossPay(new BigDecimal("5000.00"));
        testPayroll1.setNetPay(new BigDecimal("3800.00"));
        testPayroll1.setFederalWithholding(new BigDecimal("750.00"));
        testPayroll1.setSocialSecurityEmployee(new BigDecimal("310.00"));
        testPayroll1.setMedicare(new BigDecimal("72.50"));
        testPayroll1.setNyStateWithholding(new BigDecimal("350.00"));
        testPayroll1.setNyLocalWithholding(new BigDecimal("200.00"));
        testPayroll1.setNyDisabilityWithholding(new BigDecimal("30.00"));
        testPayroll1.setNyPaidFamilyLeave(new BigDecimal("25.00"));
        testPayroll1.setNyUnemploymentWithholding(new BigDecimal("15.00"));

        testPayroll2 = new WorkerPayroll();
        testPayroll2.setGrossPay(new BigDecimal("5500.00"));
        testPayroll2.setNetPay(new BigDecimal("4200.00"));
        testPayroll2.setFederalWithholding(new BigDecimal("825.00"));
        testPayroll2.setSocialSecurityEmployee(new BigDecimal("341.00"));
        testPayroll2.setMedicare(new BigDecimal("79.75"));
        testPayroll2.setNyStateWithholding(new BigDecimal("385.00"));
        testPayroll2.setNyLocalWithholding(new BigDecimal("220.00"));
        testPayroll2.setNyDisabilityWithholding(new BigDecimal("33.00"));
        testPayroll2.setNyPaidFamilyLeave(new BigDecimal("27.50"));
        testPayroll2.setNyUnemploymentWithholding(new BigDecimal("16.50"));

        // Настройка моков для метрик
        when(metricsForPdfServices.startTimer()).thenReturn(timerSample);
    }

    @Test
    void testGenerateFilledPdf_UserNotFound_ThrowsException() {
        // Arrange
        when(userRepository.findById(1)).thenReturn(Optional.empty());

        // Act & Assert
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            w2OfficialPDFService.generateFilledPdf(1, 1, 2024);
        });

        assertEquals("User Not Found", exception.getMessage());
        verify(metricsForPdfServices).recordRequest("W2");
        verify(metricsForPdfServices).recordOperationTime(timerSample, "W2_failed");
        verify(metricsForPdfServices).recordGenerated("W2", false);
        verify(metricsForPdfServices).recordError(eq("W2_failed"), anyString(), any(Exception.class));
    }

    @Test
    void testGenerateFilledPdf_CompanyNotFound_ThrowsException() {
        // Arrange
        when(userRepository.findById(1)).thenReturn(Optional.of(testUser));
        when(companyRepository.findById(1)).thenReturn(Optional.empty());

        // Act & Assert
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            w2OfficialPDFService.generateFilledPdf(1, 1, 2024);
        });

        assertEquals("Company Not Found", exception.getMessage());
        verify(metricsForPdfServices).recordRequest("W2");
        verify(metricsForPdfServices).recordOperationTime(timerSample, "W2_failed");
        verify(metricsForPdfServices).recordGenerated("W2", false);
    }

    @Test
    void testGenerateFilledPdf_WithValidData_Success() throws IOException {
        // Arrange
        when(userRepository.findById(1)).thenReturn(Optional.of(testUser));
        when(companyRepository.findById(1)).thenReturn(Optional.of(testCompany));
        when(workerPayrollRepository.findAllByWorkerIdAndYear(
                eq(1),
                eq(LocalDate.of(2024, 1, 1)),
                eq(LocalDate.of(2024, 12, 31))
        )).thenReturn(Arrays.asList(testPayroll1, testPayroll2));

        // Act
        byte[] result = w2OfficialPDFService.generateFilledPdf(1, 1, 2024);

        // Assert
        assertNotNull(result);
        assertTrue(result.length > 0);

        verify(metricsForPdfServices).recordRequest("W2");
        verify(metricsForPdfServices).recordGenerated("W2", true);
        verify(metricsForPdfServices).recordOperationTime(timerSample, "W2_success");
        verify(amazonS3Service).uploadPdfToS3(eq(result), anyString());
        verify(metricsForPdfServices).recordS3UploadTime(eq("W2"), eq(true), anyLong());
    }

    @Test
    void testGenerateFilledPdf_WithNullMiddleInitial_Success() throws IOException {
        // Arrange
        testUser.setMiddleInitial(null); // Тест без отчества
        when(userRepository.findById(1)).thenReturn(Optional.of(testUser));
        when(companyRepository.findById(1)).thenReturn(Optional.of(testCompany));
        when(workerPayrollRepository.findAllByWorkerIdAndYear(
                eq(1),
                eq(LocalDate.of(2024, 1, 1)),
                eq(LocalDate.of(2024, 12, 31))
        )).thenReturn(Arrays.asList(testPayroll1));

        // Act
        byte[] result = w2OfficialPDFService.generateFilledPdf(1, 1, 2024);

        // Assert
        assertNotNull(result);
        assertTrue(result.length > 0);
        verify(metricsForPdfServices).recordGenerated("W2", true);
    }

    @Test
    void testGenerateFilledPdf_WithNoHealthPlan_Success() throws IOException {
        // Arrange
        testUser.setEnrolledInHealthPlan(false);
        testUser.setMonthlyHealthPremium(null);
        when(userRepository.findById(1)).thenReturn(Optional.of(testUser));
        when(companyRepository.findById(1)).thenReturn(Optional.of(testCompany));
        when(workerPayrollRepository.findAllByWorkerIdAndYear(
                eq(1),
                eq(LocalDate.of(2024, 1, 1)),
                eq(LocalDate.of(2024, 12, 31))
        )).thenReturn(Arrays.asList(testPayroll1));

        // Act
        byte[] result = w2OfficialPDFService.generateFilledPdf(1, 1, 2024);

        // Assert
        assertNotNull(result);
        assertTrue(result.length > 0);
        verify(metricsForPdfServices).recordGenerated("W2", true);
    }

    @Test
    void testGenerateFilledPdf_WithEmptyPayrolls_Success() throws IOException {
        // Arrange
        when(userRepository.findById(1)).thenReturn(Optional.of(testUser));
        when(companyRepository.findById(1)).thenReturn(Optional.of(testCompany));
        when(workerPayrollRepository.findAllByWorkerIdAndYear(
                eq(1),
                eq(LocalDate.of(2024, 1, 1)),
                eq(LocalDate.of(2024, 12, 31))
        )).thenReturn(Arrays.asList()); // Пустой список

        // Act
        byte[] result = w2OfficialPDFService.generateFilledPdf(1, 1, 2024);

        // Assert
        assertNotNull(result);
        assertTrue(result.length > 0);
        verify(metricsForPdfServices).recordGenerated("W2", true);
    }

    @Test
    void testGenerateFilledPdf_WithNullApartment_Success() throws IOException {
        // Arrange
        testUser.setApt(null); // Без номера квартиры
        when(userRepository.findById(1)).thenReturn(Optional.of(testUser));
        when(companyRepository.findById(1)).thenReturn(Optional.of(testCompany));
        when(workerPayrollRepository.findAllByWorkerIdAndYear(
                eq(1),
                eq(LocalDate.of(2024, 1, 1)),
                eq(LocalDate.of(2024, 12, 31))
        )).thenReturn(Arrays.asList(testPayroll1));

        // Act
        byte[] result = w2OfficialPDFService.generateFilledPdf(1, 1, 2024);

        // Assert
        assertNotNull(result);
        assertTrue(result.length > 0);
        verify(metricsForPdfServices).recordGenerated("W2", true);
    }

    @Test
    void testGenerateFilledPdf_S3UploadFailure_StillReturnsBytes() throws IOException {
        // Arrange
        when(userRepository.findById(1)).thenReturn(Optional.of(testUser));
        when(companyRepository.findById(1)).thenReturn(Optional.of(testCompany));
        when(workerPayrollRepository.findAllByWorkerIdAndYear(
                eq(1),
                eq(LocalDate.of(2024, 1, 1)),
                eq(LocalDate.of(2024, 12, 31))
        )).thenReturn(Arrays.asList(testPayroll1));

        // Мокаем исключение при загрузке в S3
        doThrow(new RuntimeException("S3 Upload Failed"))
                .when(amazonS3Service).uploadPdfToS3(any(byte[].class), anyString());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            w2OfficialPDFService.generateFilledPdf(1, 1, 2024);
        });

        assertEquals("S3 Upload Failed", exception.getMessage());
        verify(metricsForPdfServices).recordOperationTime(timerSample, "W2_failed");
        verify(metricsForPdfServices).recordGenerated("W2", false);
    }
}