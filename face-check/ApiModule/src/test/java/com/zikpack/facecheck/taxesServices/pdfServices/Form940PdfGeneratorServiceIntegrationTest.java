package com.zikpack.facecheck.taxesServices.pdfServices;

import com.zikpak.facecheck.entity.Company;
import com.zikpak.facecheck.entity.EmployerTaxRecord;

import com.zikpak.facecheck.entity.User;
import com.zikpak.facecheck.metrics.MetricsForPdfServices;
import com.zikpak.facecheck.repository.CompanyRepository;
import com.zikpak.facecheck.repository.EmployerTaxRecordRepository;
import com.zikpak.facecheck.repository.PaymentHistoryIrsRepository;
import com.zikpak.facecheck.services.amazonS3Service.AmazonS3Service;
import com.zikpak.facecheck.taxesServices.pdfServices.FillForm940SA;
import com.zikpak.facecheck.taxesServices.pdfServices.Form940PdfGeneratorService;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class Form940PdfGeneratorServiceTest {

    @Mock
    private CompanyRepository companyRepository;

    @Mock
    private EmployerTaxRecordRepository employerTaxRecordRepository;

    @Mock
    private PaymentHistoryIrsRepository paymentHistoryIrsRepository;

    @Mock
    private AmazonS3Service amazonS3Service;

    @Mock
    private FillForm940SA fillForm940SA;

    @Mock
    private MetricsForPdfServices metric;

    @InjectMocks
    private Form940PdfGeneratorService form940PdfGeneratorService;

    private Company testCompany;
    private final Integer companyId = 1;
    private final int year = 2024;

    @BeforeEach
    void setUp() {
        // Создаем тестовую компанию
        testCompany = new Company();
        testCompany.setId(companyId);
        testCompany.setCompanyName("Test Company LLC");
        testCompany.setEmployerEIN("12-3456789");
        testCompany.setCompanyAddress("123 Test Street");
        testCompany.setCompanyCity("New York");
        testCompany.setCompanyState("NY");
        testCompany.setCompanyZipCode("10001");
        testCompany.setCompanyPhone("+1-212-555-0123");

        // Создаем владельца компании
        User owner = new User();
        owner.setFirstName("John");
        owner.setLastName("Doe");
        testCompany.setCompanyOwner(owner);

        // Настраиваем мок для Timer.Sample
        Timer.Sample mockTimerSample = mock(Timer.Sample.class);
        when(metric.startTimer()).thenReturn(mockTimerSample);
    }

    @Test
    void testGenerate940Pdf_Success() throws IOException {
        // Given
        when(companyRepository.findById(companyId)).thenReturn(Optional.of(testCompany));
        when(employerTaxRecordRepository.sumGrossPayByAllEmployeeAndYear(companyId, year))
                .thenReturn(new BigDecimal("150000.00"));
        when(employerTaxRecordRepository.findEmployeesWithYearlyGrossOver7000(companyId, year))
                .thenReturn(createMockEmployeeData());
        when(paymentHistoryIrsRepository.getTotalPaidForFUTA(companyId, year))
                .thenReturn(new BigDecimal("800.00"));
        when(fillForm940SA.getNYCreditReduction(companyId, year))
                .thenReturn(new BigDecimal("100.00"));
        when(employerTaxRecordRepository.findByCompanyIdAndPeriodStartGreaterThanEqualAndPeriodEndLessThanEqual(
                eq(companyId), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(createMockEmployerTaxRecords());

        // When
        byte[] result = form940PdfGeneratorService.generate940Pdf(companyId, year);

        // Then
        assertNotNull(result);
        assertTrue(result.length > 0);

        // Verify metrics
        verify(metric).recordRequest("940");
        verify(metric).startTimer();
        verify(metric).recordGenerated("940", true);
        verify(metric).recordS3UploadTime(eq("940"), eq(true), anyLong());
        verify(metric).recordOperationTime(any(Timer.Sample.class), eq("940_success"));

        // Verify S3 upload
        verify(amazonS3Service).uploadPdfToS3(any(byte[].class), contains("Test_Company_LLC/1/940pdf/"));
    }

    @Test
    void testGenerate940Pdf_CompanyNotFound() {
        // Given
        when(companyRepository.findById(companyId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(EntityNotFoundException.class, () -> {
            form940PdfGeneratorService.generate940Pdf(companyId, year);
        });

        // Verify error metrics
        verify(metric).recordRequest("940");
        verify(metric).recordGenerated("940", false);
        verify(metric).recordError(eq("940_failed"), anyString(), any(Exception.class));
        verify(metric).recordOperationTime(any(Timer.Sample.class), eq("940_failed"));
    }

    @Test
    void testGenerate940Pdf_InvalidEIN() {
        // Given
        testCompany.setEmployerEIN("123456789"); // EIN без дефиса
        when(companyRepository.findById(companyId)).thenReturn(Optional.of(testCompany));

        // When & Then
        assertThrows(IllegalStateException.class, () -> {
            form940PdfGeneratorService.generate940Pdf(companyId, year);
        });

        // Verify error metrics
        verify(metric).recordRequest("940");
        verify(metric).recordGenerated("940", false);
        verify(metric).recordError(eq("940_failed"), anyString(), any(Exception.class));
    }

    @Test
    void testGenerate940Pdf_WithZeroPayments() throws IOException {
        // Given
        when(companyRepository.findById(companyId)).thenReturn(Optional.of(testCompany));
        when(employerTaxRecordRepository.sumGrossPayByAllEmployeeAndYear(companyId, year))
                .thenReturn(BigDecimal.ZERO);
        when(employerTaxRecordRepository.findEmployeesWithYearlyGrossOver7000(companyId, year))
                .thenReturn(new ArrayList<>());
        when(paymentHistoryIrsRepository.getTotalPaidForFUTA(companyId, year))
                .thenReturn(BigDecimal.ZERO);
        when(fillForm940SA.getNYCreditReduction(companyId, year))
                .thenReturn(BigDecimal.ZERO);
        when(employerTaxRecordRepository.findByCompanyIdAndPeriodStartGreaterThanEqualAndPeriodEndLessThanEqual(
                eq(companyId), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(new ArrayList<>());

        // When
        byte[] result = form940PdfGeneratorService.generate940Pdf(companyId, year);

        // Then
        assertNotNull(result);
        assertTrue(result.length > 0);

        // Verify successful generation
        verify(metric).recordGenerated("940", true);
        verify(amazonS3Service).uploadPdfToS3(any(byte[].class), anyString());
    }

    // Вспомогательные методы для создания тестовых данных
    private List<Object[]> createMockEmployeeData() {
        List<Object[]> data = new ArrayList<>();
        data.add(new Object[]{1, new BigDecimal("50000.00")});
        data.add(new Object[]{2, new BigDecimal("45000.00")});
        data.add(new Object[]{3, new BigDecimal("35000.00")});
        return data;
    }

    private List<EmployerTaxRecord> createMockEmployerTaxRecords() {
        List<EmployerTaxRecord> records = new ArrayList<>();

        // Создаем записи для трех сотрудников по кварталам
        for (int empId = 1; empId <= 3; empId++) {
            User employee = new User();
            employee.setId(empId);

            // Q1
            EmployerTaxRecord q1Record = new EmployerTaxRecord();
            q1Record.setEmployee(employee);
            q1Record.setPeriodStart(LocalDate.of(2024, 1, 1));
            q1Record.setPeriodEnd(LocalDate.of(2024, 3, 31));
            q1Record.setGrossPay(new BigDecimal("12500.00"));
            records.add(q1Record);

            // Q2
            EmployerTaxRecord q2Record = new EmployerTaxRecord();
            q2Record.setEmployee(employee);
            q2Record.setPeriodStart(LocalDate.of(2024, 4, 1));
            q2Record.setPeriodEnd(LocalDate.of(2024, 6, 30));
            q2Record.setGrossPay(new BigDecimal("12500.00"));
            records.add(q2Record);

            // Q3
            EmployerTaxRecord q3Record = new EmployerTaxRecord();
            q3Record.setEmployee(employee);
            q3Record.setPeriodStart(LocalDate.of(2024, 7, 1));
            q3Record.setPeriodEnd(LocalDate.of(2024, 9, 30));
            q3Record.setGrossPay(new BigDecimal("12500.00"));
            records.add(q3Record);

            // Q4
            EmployerTaxRecord q4Record = new EmployerTaxRecord();
            q4Record.setEmployee(employee);
            q4Record.setPeriodStart(LocalDate.of(2024, 10, 1));
            q4Record.setPeriodEnd(LocalDate.of(2024, 12, 31));
            q4Record.setGrossPay(new BigDecimal("12500.00"));
            records.add(q4Record);
        }

        return records;
    }
}