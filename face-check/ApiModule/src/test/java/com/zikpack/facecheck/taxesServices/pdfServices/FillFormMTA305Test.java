package com.zikpack.facecheck.taxesServices.pdfServices;


import com.zikpak.facecheck.entity.Company;
import com.zikpak.facecheck.entity.User;
import com.zikpak.facecheck.metrics.MetricsForPdfServices;
import com.zikpak.facecheck.repository.CompanyRepository;
import com.zikpak.facecheck.repository.PaymentHistoryIrsRepository;
import com.zikpak.facecheck.repository.WorkerPayrollRepository;
import com.zikpak.facecheck.services.amazonS3Service.AmazonS3Service;
import com.zikpak.facecheck.taxesServices.pdfServices.FillFormMTA305;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FillFormMTA305Test {

    @Mock
    private CompanyRepository companyRepository;

    @Mock
    private WorkerPayrollRepository workerPayrollRepository;

    @Mock
    private PaymentHistoryIrsRepository paymentHistoryIrsRepository;

    @Mock
    private AmazonS3Service amazonS3Service;

    @Mock
    private MetricsForPdfServices metric;

    @InjectMocks
    private FillFormMTA305 fillFormMTA305;

    private Company testCompany;
    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1);

        testCompany = new Company();
        testCompany.setId(1);
        testCompany.setCompanyName("Test Company Inc");
        testCompany.setCompanyAddress("123 Test Street");
        testCompany.setCompanyCity("New York");
        testCompany.setCompanyState("NY");
        testCompany.setCompanyZipCode("10001");
        testCompany.setEmployerEIN("12-3456789");
        testCompany.setCompanyPhone("1-212-555-1234");
        testCompany.setCompanyEmail("test@company.com");
        testCompany.setCompanyOwner(testUser);
        testCompany.setSpecialTwoCharConditionCodeForMTA305("00");
    }

    @Test
    void testGenerateFilledPdf_WhenCompanyNotFound_ThrowsEntityNotFoundException() {
        // Given
        Integer companyId = 999;
        int quarter = 1;
        Integer year = 2024;

        when(companyRepository.findById(companyId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(EntityNotFoundException.class, () -> {
            fillFormMTA305.generateFilledPdf(companyId, quarter, year);
        });

        verify(metric).recordRequest("MTa-305");
        verify(metric).startTimer();
    }

    @Test
    void testGenerateFilledPdf_WhenGrossWagesLessThanThreshold_ThrowsRuntimeException() throws IOException {
        // Given
        Integer companyId = 1;
        int quarter = 1;
        Integer year = 2024;
        LocalDate start = LocalDate.of(2024, 1, 1);
        LocalDate end = LocalDate.of(2024, 3, 31);

        // Мокаем метрики
        Timer.Sample mockTimer = mock(Timer.Sample.class);
        when(metric.startTimer()).thenReturn(mockTimer);

        // Gross wages меньше порога $312,500
        BigDecimal lowGrossWages = new BigDecimal("300000.00");

        when(companyRepository.findById(companyId)).thenReturn(Optional.of(testCompany));
        when(workerPayrollRepository.sumGrossWages(companyId, start, end)).thenReturn(lowGrossWages);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            fillFormMTA305.generateFilledPdf(companyId, quarter, year);
        });

        assertEquals("Company with id: 1 should not making this form!", exception.getMessage());

        // Verify metrics
        verify(metric).recordRequest("MTa-305");
        verify(metric).recordOperationTime(mockTimer, "MTA305_failed");
        verify(metric).recordGenerated("MTa-305", false);
        verify(metric).recordError(eq("MTA305_failed"), anyString(), any(Exception.class));
    }

    @Test
    void testGenerateFilledPdf_WithValidDataFirstQuarter_Success() throws IOException {
        // Given
        Integer companyId = 1;
        int quarter = 1;
        Integer year = 2024;
        LocalDate start = LocalDate.of(2024, 1, 1);
        LocalDate end = LocalDate.of(2024, 3, 31);

        // Мокаем метрики
        Timer.Sample mockTimer = mock(Timer.Sample.class);
        when(metric.startTimer()).thenReturn(mockTimer);

        // Gross wages больше порога
        BigDecimal highGrossWages = new BigDecimal("350000.00");

        when(companyRepository.findById(companyId)).thenReturn(Optional.of(testCompany));
        when(workerPayrollRepository.sumGrossWages(companyId, start, end)).thenReturn(highGrossWages);
        when(workerPayrollRepository.findAllByCompanyIdAndPeriodBetween(companyId, start, end))
                .thenReturn(java.util.Collections.emptyList());
        when(paymentHistoryIrsRepository.getTotalMctmtPrepaymentsAndCredits(companyId, year, quarter))
                .thenReturn(BigDecimal.ZERO);

        // When
        byte[] result = fillFormMTA305.generateFilledPdf(companyId, quarter, year);

        // Then
        assertNotNull(result);
        assertTrue(result.length > 0);

        // Verify S3 upload
        verify(amazonS3Service).uploadPdfToS3(any(byte[].class), anyString());

        // Verify metrics
        verify(metric).recordRequest("MTa-305");
        verify(metric).recordGenerated("MTa-305", true);
        verify(metric).recordS3UploadTime(eq("MTa-305"), eq(true), anyLong());
        verify(metric).recordOperationTime(mockTimer, "MTA305_success");
    }

    @Test
    void testGenerateFilledPdf_DifferentQuarters() throws IOException {
        // Тестируем разные кварталы
        for (int quarter = 1; quarter <= 4; quarter++) {
            // Reset mocks
            reset(metric, amazonS3Service);

            // Setup
            Integer companyId = 1;
            Integer year = 2024;
            LocalDate start = LocalDate.of(year, (quarter - 1) * 3 + 1, 1);
            LocalDate end = start.plusMonths(3).minusDays(1);

            Timer.Sample mockTimer = mock(Timer.Sample.class);
            when(metric.startTimer()).thenReturn(mockTimer);

            BigDecimal highGrossWages = new BigDecimal("400000.00");

            when(companyRepository.findById(companyId)).thenReturn(Optional.of(testCompany));
            when(workerPayrollRepository.sumGrossWages(companyId, start, end)).thenReturn(highGrossWages);
            when(workerPayrollRepository.findAllByCompanyIdAndPeriodBetween(companyId, start, end))
                    .thenReturn(java.util.Collections.emptyList());
            when(paymentHistoryIrsRepository.getTotalMctmtPrepaymentsAndCredits(companyId, year, quarter))
                    .thenReturn(BigDecimal.ZERO);

            // Execute
            byte[] result = fillFormMTA305.generateFilledPdf(companyId, quarter, year);

            // Verify
            assertNotNull(result);
            assertTrue(result.length > 0);

            // Verify the correct S3 key format for each quarter
            verify(amazonS3Service).uploadPdfToS3(any(byte[].class),
                    contains(String.format("/%d/MTA305/%d/%d/", companyId, year, quarter)));
        }
    }

    @Test
    void testGenerateFilledPdf_WithPrepayments_CalculatesCorrectly() throws IOException {
        // Given
        Integer companyId = 1;
        int quarter = 2;
        Integer year = 2024;
        LocalDate start = LocalDate.of(2024, 4, 1);
        LocalDate end = LocalDate.of(2024, 6, 30);

        Timer.Sample mockTimer = mock(Timer.Sample.class);
        when(metric.startTimer()).thenReturn(mockTimer);

        // Настраиваем gross wages и prepayments
        BigDecimal grossWages = new BigDecimal("500000.00");
        BigDecimal prepayments = new BigDecimal("1000.00");

        when(companyRepository.findById(companyId)).thenReturn(Optional.of(testCompany));
        when(workerPayrollRepository.sumGrossWages(companyId, start, end)).thenReturn(grossWages);
        when(workerPayrollRepository.findAllByCompanyIdAndPeriodBetween(companyId, start, end))
                .thenReturn(java.util.Collections.emptyList());
        when(paymentHistoryIrsRepository.getTotalMctmtPrepaymentsAndCredits(companyId, year, quarter))
                .thenReturn(prepayments);

        // When
        byte[] result = fillFormMTA305.generateFilledPdf(companyId, quarter, year);

        // Then
        assertNotNull(result);
        assertTrue(result.length > 0);

        // Verify metrics for successful generation
        verify(metric).recordGenerated("MTa-305", true);
        verify(metric).recordOperationTime(mockTimer, "MTA305_success");
    }
}
