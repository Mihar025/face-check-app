package com.zikpack.facecheck.taxesServices.customReportsForCompany;

import com.zikpak.facecheck.entity.Company;
import com.zikpak.facecheck.entity.employee.WorkerPayroll;
import com.zikpak.facecheck.repository.CompanyRepository;
import com.zikpak.facecheck.repository.EmployerTaxRecordRepository;
import com.zikpak.facecheck.repository.WorkerPayrollRepository;
import com.zikpak.facecheck.services.amazonS3Service.AmazonS3Service;
import com.zikpak.facecheck.taxesServices.customReportsForCompanys.yearToDateReportTaxes.YearToDateCustomReportPdf;
import com.zikpak.facecheck.taxesServices.customReportsForCompanys.yearToDateReportTaxes.YearToDateCustomReportService;
import com.zikpak.facecheck.taxesServices.customReportsForCompanys.yearToDateReportTaxes.YearToDateDTO;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class YearToDateCustomReportServiceTest {

    @Mock
    private CompanyRepository companyRepository;

    @Mock
    private WorkerPayrollRepository workerPayrollRepository;

    @Mock
    private YearToDateCustomReportPdf yearToDateCustomReportPdf;

    @Mock
    private EmployerTaxRecordRepository employerTaxRecordRepository;

    @Mock
    private AmazonS3Service amazonS3Service;

    @InjectMocks
    private YearToDateCustomReportService service;

    private Company testCompany;
    private List<WorkerPayroll> testPayrolls;
    private LocalDate startDate;
    private LocalDate endDate;

    @BeforeEach
    void setUp() {
        startDate = LocalDate.of(2024, 1, 1);
        endDate = LocalDate.of(2024, 12, 31);

        testCompany = new Company();
        testCompany.setId(1);
        testCompany.setCompanyName("Test Company Inc");

        // Создаем тестовые данные для payroll
        WorkerPayroll payroll1 = new WorkerPayroll();
        payroll1.setRegularHours(40.0);
        payroll1.setOvertimeHours(5.0);
        payroll1.setGrossPay(new BigDecimal("1500.00"));
        payroll1.setRegularPay(new BigDecimal("1200.00"));
        payroll1.setOvertimePay(new BigDecimal("300.00"));
        payroll1.setFederalWithholding(new BigDecimal("200.00"));
        payroll1.setSocialSecurityEmployee(new BigDecimal("93.00"));
        payroll1.setMedicare(new BigDecimal("21.75"));
        payroll1.setNyStateWithholding(new BigDecimal("80.00"));
        payroll1.setNyLocalWithholding(new BigDecimal("40.00"));
        payroll1.setNyDisabilityWithholding(new BigDecimal("1.20"));
        payroll1.setNyPaidFamilyLeave(new BigDecimal("5.00"));
        payroll1.setRetirement401kContribution(new BigDecimal("100.00"));
        payroll1.setHealthInsuranceCost(new BigDecimal("150.00"));
        payroll1.setTotalDeductions(new BigDecimal("690.95"));
        payroll1.setNetPay(new BigDecimal("809.05"));

        WorkerPayroll payroll2 = new WorkerPayroll();
        payroll2.setRegularHours(35.0);
        payroll2.setOvertimeHours(0.0);
        payroll2.setGrossPay(new BigDecimal("1300.00"));
        payroll2.setRegularPay(new BigDecimal("1300.00"));
        payroll2.setOvertimePay(new BigDecimal("0.00"));
        payroll2.setFederalWithholding(new BigDecimal("180.00"));
        payroll2.setSocialSecurityEmployee(new BigDecimal("80.60"));
        payroll2.setMedicare(new BigDecimal("18.85"));
        payroll2.setNyStateWithholding(new BigDecimal("70.00"));
        payroll2.setNyLocalWithholding(new BigDecimal("35.00"));
        payroll2.setNyDisabilityWithholding(new BigDecimal("1.04"));
        payroll2.setNyPaidFamilyLeave(new BigDecimal("4.50"));
        payroll2.setRetirement401kContribution(new BigDecimal("80.00"));
        payroll2.setHealthInsuranceCost(new BigDecimal("150.00"));
        payroll2.setTotalDeductions(new BigDecimal("619.99"));
        payroll2.setNetPay(new BigDecimal("680.01"));

        testPayrolls = Arrays.asList(payroll1, payroll2);
    }

    @Test
    void testGenerateYearToDateReport_Success() {
        // Given
        byte[] expectedPdf = "test pdf content".getBytes();
        when(companyRepository.findById(1)).thenReturn(Optional.of(testCompany));
        when(workerPayrollRepository.findAllByCompanyIdAndPeriodBetween(1, startDate, endDate))
                .thenReturn(testPayrolls);
        when(employerTaxRecordRepository.sumFutaForPeriodStartEnd(1, startDate, endDate))
                .thenReturn(new BigDecimal("150.00"));
        when(employerTaxRecordRepository.sumSutaForPeriodStartEnd(1, startDate, endDate))
                .thenReturn(new BigDecimal("200.00"));
        when(yearToDateCustomReportPdf.generatePdf(any(YearToDateDTO.class)))
                .thenReturn(expectedPdf);

        // When
        byte[] result = service.generateYearToDateReport(1, startDate, endDate);

        // Then
        assertNotNull(result);
        assertEquals(expectedPdf, result);
        verify(amazonS3Service).uploadPdfToS3(eq(expectedPdf), anyString());
    }

    @Test
    void testYearToDateReport_InvalidDateRange() {
        // Given
        LocalDate invalidStartDate = LocalDate.of(2024, 12, 31);
        LocalDate invalidEndDate = LocalDate.of(2024, 1, 1);

        // When & Then
        assertThrows(IllegalArgumentException.class, () ->
                service.yearToDateReport(1, invalidStartDate, invalidEndDate)
        );
    }

    @Test
    void testYearToDateReport_CompanyNotFound() {
        // Given
        when(companyRepository.findById(999)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(EntityNotFoundException.class, () ->
                service.yearToDateReport(999, startDate, endDate)
        );
    }

    @Test
    void testYearToDateReport_CalculationsCorrect() {
        // Given
        when(companyRepository.findById(1)).thenReturn(Optional.of(testCompany));
        when(workerPayrollRepository.findAllByCompanyIdAndPeriodBetween(1, startDate, endDate))
                .thenReturn(testPayrolls);
        when(employerTaxRecordRepository.sumFutaForPeriodStartEnd(1, startDate, endDate))
                .thenReturn(new BigDecimal("150.00"));
        when(employerTaxRecordRepository.sumSutaForPeriodStartEnd(1, startDate, endDate))
                .thenReturn(new BigDecimal("200.00"));

        // When
        YearToDateDTO result = service.yearToDateReport(1, startDate, endDate);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getCompanyId());
        assertEquals("Test Company Inc", result.getCompanyName());
        assertEquals(startDate, result.getPeriodStart());
        assertEquals(endDate, result.getPeriodEnd());

        // Проверяем суммы
        assertEquals(75.0, result.getTotalRegularHours()); // 40 + 35
        assertEquals(5.0, result.getTotalOvertimeHours()); // 5 + 0
        assertEquals(new BigDecimal("2800.00"), result.getTotalGross()); // 1500 + 1300
        assertEquals(new BigDecimal("2500.00"), result.getTotalRegularPay()); // 1200 + 1300
        assertEquals(new BigDecimal("300.00"), result.getTotalOvertimePay()); // 300 + 0
        assertEquals(new BigDecimal("380.00"), result.getTotalFederalWithholding()); // 200 + 180
        assertEquals(new BigDecimal("173.60"), result.getTotalSocialSecurity()); // 93 + 80.60
        assertEquals(new BigDecimal("40.60"), result.getTotalMedicare()); // 21.75 + 18.85
        assertEquals(new BigDecimal("150.00"), result.getTotalStateWithHolding()); // 80 + 70
        assertEquals(new BigDecimal("75.00"), result.getTotalLocalWithholding()); // 40 + 35
        assertEquals(new BigDecimal("150.00"), result.getTotalFutaWithholding());
        assertEquals(new BigDecimal("200.00"), result.getTotalSutaWithholding());
        assertEquals(new BigDecimal("2.24"), result.getTotalDisabilityWithholding()); // 1.20 + 1.04
        assertEquals(new BigDecimal("9.50"), result.getTotalPaidFamilyLeave()); // 5 + 4.50
        assertEquals(new BigDecimal("180.00"), result.getTotalRetirement401kContribution()); // 100 + 80
        assertEquals(new BigDecimal("300.00"), result.getTotalHealthInsuranceCost()); // 150 + 150
        assertEquals(new BigDecimal("1310.94"), result.getTotalDeductions()); // 690.95 + 619.99
        assertEquals(new BigDecimal("1489.06"), result.getTotalNet()); // 809.05 + 680.01
    }

    @Test
    void testYearToDateReport_WithNullValues() {
        // Given
        WorkerPayroll payrollWithNulls = new WorkerPayroll();
        payrollWithNulls.setRegularHours(null);
        payrollWithNulls.setOvertimeHours(null);
        payrollWithNulls.setGrossPay(new BigDecimal("1000.00"));
        payrollWithNulls.setNetPay(new BigDecimal("800.00"));
        // Остальные поля оставляем null

        when(companyRepository.findById(1)).thenReturn(Optional.of(testCompany));
        when(workerPayrollRepository.findAllByCompanyIdAndPeriodBetween(1, startDate, endDate))
                .thenReturn(Arrays.asList(payrollWithNulls));
        when(employerTaxRecordRepository.sumFutaForPeriodStartEnd(1, startDate, endDate))
                .thenReturn(new BigDecimal("0.00"));
        when(employerTaxRecordRepository.sumSutaForPeriodStartEnd(1, startDate, endDate))
                .thenReturn(new BigDecimal("0.00"));

        // When
        YearToDateDTO result = service.yearToDateReport(1, startDate, endDate);

        // Then
        assertNotNull(result);
        assertEquals(0.0, result.getTotalRegularHours());
        assertEquals(0.0, result.getTotalOvertimeHours());
        assertEquals(new BigDecimal("1000.00"), result.getTotalGross());
        assertEquals(new BigDecimal("800.00"), result.getTotalNet());
        assertEquals(BigDecimal.ZERO, result.getTotalRegularPay());
        assertEquals(BigDecimal.ZERO, result.getTotalOvertimePay());
    }

    @Test
    void testGenerateYearToDateReport_S3UploadFails() {
        // Given
        byte[] expectedPdf = "test pdf content".getBytes();
        when(companyRepository.findById(1)).thenReturn(Optional.of(testCompany));
        when(workerPayrollRepository.findAllByCompanyIdAndPeriodBetween(1, startDate, endDate))
                .thenReturn(testPayrolls);
        when(employerTaxRecordRepository.sumFutaForPeriodStartEnd(1, startDate, endDate))
                .thenReturn(new BigDecimal("150.00"));
        when(employerTaxRecordRepository.sumSutaForPeriodStartEnd(1, startDate, endDate))
                .thenReturn(new BigDecimal("200.00"));
        when(yearToDateCustomReportPdf.generatePdf(any(YearToDateDTO.class)))
                .thenReturn(expectedPdf);
        doThrow(new RuntimeException("S3 upload failed"))
                .when(amazonS3Service).uploadPdfToS3(any(byte[].class), anyString());

        // When
        byte[] result = service.generateYearToDateReport(1, startDate, endDate);

        // Then
        assertNotNull(result);
        assertEquals(expectedPdf, result);
        // Проверяем, что метод все равно возвращает PDF, даже если загрузка в S3 не удалась
        verify(amazonS3Service).uploadPdfToS3(eq(expectedPdf), anyString());
    }
}