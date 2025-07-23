package com.zikpack.facecheck.taxesServices.pdfServices;

import com.zikpak.facecheck.entity.Company;
import com.zikpak.facecheck.metrics.MetricsForPdfServices;
import com.zikpak.facecheck.repository.CompanyRepository;
import com.zikpak.facecheck.repository.EmployerTaxRecordRepository;
import com.zikpak.facecheck.services.amazonS3Service.AmazonS3Service;
import com.zikpak.facecheck.taxesServices.pdfServices.FillForm940SA;
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
class FillForm940SATest {

    @Mock
    private AmazonS3Service amazonS3Service;

    @Mock
    private CompanyRepository companyRepository;

    @Mock
    private EmployerTaxRecordRepository employerTaxRecordRepository;

    @Mock
    private MetricsForPdfServices metric;

    @Mock
    private Timer.Sample timerSample;

    @InjectMocks
    private FillForm940SA fillForm940SA;

    private Company testCompany;
    private static final Integer COMPANY_ID = 1;
    private static final int YEAR = 2024;

    @BeforeEach
    void setUp() {
        testCompany = new Company();
        testCompany.setId(COMPANY_ID);
        testCompany.setCompanyName("Test Company LLC");
        testCompany.setEmployerEIN("12-3456789");
    }

    @Test
    void testGenerateFilledPdf_Success() throws IOException {
        // Given
        when(companyRepository.findById(COMPANY_ID)).thenReturn(Optional.of(testCompany));
        when(metric.startTimer()).thenReturn(timerSample);

        // Mock FUTA taxable wages calculation
        BigDecimal grossWages = new BigDecimal("100000.00");
        when(employerTaxRecordRepository.sumGrossPayByAllEmployeeAndYear(COMPANY_ID, YEAR))
                .thenReturn(grossWages);

        // Mock employees with wages over $7000
        List<Object[]> employeesOver7000 = Arrays.asList(
                new Object[]{"Employee1", new BigDecimal("15000.00")},
                new Object[]{"Employee2", new BigDecimal("12000.00")}
        );
        when(employerTaxRecordRepository.findEmployeesWithYearlyGrossOver7000(COMPANY_ID, YEAR))
                .thenReturn(employeesOver7000);

        // When
        byte[] result = fillForm940SA.generateFilledPdf(COMPANY_ID, YEAR);

        // Then
        assertNotNull(result);
        assertTrue(result.length > 0);

        // Verify metrics were recorded
        verify(metric).recordRequest("940SA");
        verify(metric).recordGenerated("940SA", true);
        verify(metric).recordS3UploadTime(eq("940SA"), eq(true), anyLong());
        verify(metric).recordOperationTime(timerSample, "940_SA_success");

        // Verify S3 upload was called
        verify(amazonS3Service).uploadPdfToS3(any(byte[].class), anyString());
    }

    @Test
    void testGenerateFilledPdf_CompanyNotFound() {
        // Given
        when(companyRepository.findById(COMPANY_ID)).thenReturn(Optional.empty());
        when(metric.startTimer()).thenReturn(timerSample);

        // When & Then
        assertThrows(EntityNotFoundException.class, () -> {
            fillForm940SA.generateFilledPdf(COMPANY_ID, YEAR);
        });

        // Verify error metrics
        verify(metric).recordRequest("940SA");
        verify(metric).recordGenerated("940SA", false);
        verify(metric).recordOperationTime(timerSample, "940_SA_failed");
        verify(metric).recordError(eq("940_SA_failed"), anyString(), any(EntityNotFoundException.class));
    }

    @Test
    void testGetNYCreditReduction_CalculatesCorrectly() {
        // Given
        BigDecimal grossWages = new BigDecimal("50000.00");
        when(employerTaxRecordRepository.sumGrossPayByAllEmployeeAndYear(COMPANY_ID, YEAR))
                .thenReturn(grossWages);

        // Two employees with wages over $7000
        List<Object[]> employeesOver7000 = Arrays.asList(
                new Object[]{"Employee1", new BigDecimal("10000.00")}, // $3000 over
                new Object[]{"Employee2", new BigDecimal("8000.00")}    // $1000 over
        );
        when(employerTaxRecordRepository.findEmployeesWithYearlyGrossOver7000(COMPANY_ID, YEAR))
                .thenReturn(employeesOver7000);

        // When
        BigDecimal creditReduction = fillForm940SA.getNYCreditReduction(COMPANY_ID, YEAR);

        // Then
        // FUTA taxable wages = $50,000 - $4,000 = $46,000
        // Credit reduction = $46,000 * 0.009 = $414.00
        assertEquals(new BigDecimal("414.00"), creditReduction);
    }

    @Test
    void testGetNYCreditReduction_NoEmployeesOver7000() {
        // Given
        BigDecimal grossWages = new BigDecimal("5000.00");
        when(employerTaxRecordRepository.sumGrossPayByAllEmployeeAndYear(COMPANY_ID, YEAR))
                .thenReturn(grossWages);

        // No employees over $7000
        when(employerTaxRecordRepository.findEmployeesWithYearlyGrossOver7000(COMPANY_ID, YEAR))
                .thenReturn(Arrays.asList());

        // When
        BigDecimal creditReduction = fillForm940SA.getNYCreditReduction(COMPANY_ID, YEAR);

        // Then
        // FUTA taxable wages = $5,000 - $0 = $5,000
        // Credit reduction = $5,000 * 0.009 = $45.00
        assertEquals(new BigDecimal("45.00"), creditReduction);
    }

    @Test
    void testGenerateFilledPdf_HandlesIOException() throws IOException {
        // Given
        when(companyRepository.findById(COMPANY_ID)).thenReturn(Optional.of(testCompany));
        when(metric.startTimer()).thenReturn(timerSample);

        // Mock exception during PDF processing
        when(employerTaxRecordRepository.sumGrossPayByAllEmployeeAndYear(COMPANY_ID, YEAR))
                .thenThrow(new RuntimeException("Database error"));

        // When & Then
        assertThrows(RuntimeException.class, () -> {
            fillForm940SA.generateFilledPdf(COMPANY_ID, YEAR);
        });

        // Verify error handling
        verify(metric).recordRequest("940SA");
        verify(metric).recordGenerated("940SA", false);
        verify(metric).recordOperationTime(timerSample, "940_SA_failed");
        verify(metric).recordError(eq("940_SA_failed"), anyString(), any(RuntimeException.class));
    }
}