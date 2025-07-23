package com.zikpack.facecheck.taxesServices.customReportsForCompany;

import com.zikpak.facecheck.entity.Company;
import com.zikpak.facecheck.entity.EmployerTaxRecord;
import com.zikpak.facecheck.entity.User;
import com.zikpak.facecheck.repository.CompanyRepository;
import com.zikpak.facecheck.repository.EmployerTaxRecordRepository;
import com.zikpak.facecheck.repository.PaymentHistoryIrsRepository;
import com.zikpak.facecheck.repository.UserRepository;
import com.zikpak.facecheck.taxesServices.customReportsForCompanys.sutaCustomTaxReturn.QuarterlySutaDTO;
import com.zikpak.facecheck.taxesServices.customReportsForCompanys.sutaCustomTaxReturn.SutaReportDTO;
import com.zikpak.facecheck.taxesServices.customReportsForCompanys.sutaCustomTaxReturn.SutaReportService;
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
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SutaReportServiceTest {

    @Mock
    private CompanyRepository companyRepository;

    @Mock
    private EmployerTaxRecordRepository employerTaxRecordRepository;

    @Mock
    private PaymentHistoryIrsRepository paymentHistoryRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private SutaReportService sutaReportService;

    private Company testCompany;
    private User testEmployee;
    private EmployerTaxRecord testTaxRecord;

    @BeforeEach
    void setUp() {
        testCompany = createTestCompany();
        testEmployee = createTestEmployee();
        testTaxRecord = createTestTaxRecord();
    }

    // ========== generateQuarterlySutaReport Tests ==========

    @Test
    void generateQuarterlySutaReport_ValidData_ReturnsReport() {
        // Given
        Integer companyId = 1;
        Integer year = 2024;
        Integer quarter = 1;
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 3, 31);

        List<EmployerTaxRecord> taxRecords = Arrays.asList(testTaxRecord);
        BigDecimal totalPaid = new BigDecimal("100.00");

        when(companyRepository.findById(companyId)).thenReturn(Optional.of(testCompany));
        when(employerTaxRecordRepository.findByCompanyIdAndPeriodStartGreaterThanEqualAndPeriodEndLessThanEqual(
                companyId, startDate, endDate)).thenReturn(taxRecords);
        when(paymentHistoryRepository.getTotalPaidForQuarterSUTA(companyId, quarter, year)).thenReturn(totalPaid);
        when(employerTaxRecordRepository.sumSutaTaxableWagesByEmployeeBeforeDate(any(), any())).thenReturn(BigDecimal.ZERO);

        // When
        SutaReportDTO result = sutaReportService.generateQuarterlySutaReport(companyId, year, quarter);

        // Then
        assertNotNull(result);
        assertEquals("Quarterly", result.getReportType());
        assertEquals(companyId, result.getCompanyId());
        assertEquals("Test Company", result.getCompanyName());
        assertEquals(year, result.getTaxYear());
        assertEquals(quarter, result.getQuarter());
        assertEquals(startDate, result.getPeriodStart());
        assertEquals(endDate, result.getPeriodEnd());
        assertNotNull(result.getEmployeeDetails());
    }

    @Test
    void generateQuarterlySutaReport_CompanyNotFound_ThrowsException() {
        // Given
        Integer companyId = 999;
        when(companyRepository.findById(companyId)).thenReturn(Optional.empty());

        // When & Then
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> sutaReportService.generateQuarterlySutaReport(companyId, 2024, 1));
        assertTrue(exception.getMessage().contains("Компания не найдена: 999"));
    }

    @Test
    void generateQuarterlySutaReport_NoTaxRecords_ReturnsZeroReport() {
        // Given
        Integer companyId = 1;
        Integer year = 2024;
        Integer quarter = 2;

        when(companyRepository.findById(companyId)).thenReturn(Optional.of(testCompany));
        when(employerTaxRecordRepository.findByCompanyIdAndPeriodStartGreaterThanEqualAndPeriodEndLessThanEqual(
                anyInt(), any(), any())).thenReturn(Collections.emptyList());
        when(paymentHistoryRepository.getTotalPaidForQuarterSUTA(companyId, quarter, year)).thenReturn(BigDecimal.ZERO);

        // When
        SutaReportDTO result = sutaReportService.generateQuarterlySutaReport(companyId, year, quarter);

        // Then
        assertNotNull(result);
        assertEquals(BigDecimal.ZERO, result.getTotalGrossWages());
        assertEquals(BigDecimal.ZERO, result.getTotalSutaWageBase());
        assertEquals(BigDecimal.ZERO, result.getTotalSutaTaxOwed());
        assertEquals(0, result.getTotalEmployees());
        assertEquals(0, result.getEmployeesSubjectToSuta());
    }

    @Test
    void generateQuarterlySutaReport_HighTaxOwed_SetsNeedsPaymentTrue() {
        // Given
        Integer companyId = 1;
        EmployerTaxRecord highWageRecord = createTestTaxRecord();
        highWageRecord.setGrossPay(new BigDecimal("20000.00")); // High wages to trigger need for payment

        when(companyRepository.findById(companyId)).thenReturn(Optional.of(testCompany));
        when(employerTaxRecordRepository.findByCompanyIdAndPeriodStartGreaterThanEqualAndPeriodEndLessThanEqual(
                anyInt(), any(), any())).thenReturn(Arrays.asList(highWageRecord));
        when(paymentHistoryRepository.getTotalPaidForQuarterSUTA(anyInt(), anyInt(), anyInt())).thenReturn(BigDecimal.ZERO);
        when(employerTaxRecordRepository.sumSutaTaxableWagesByEmployeeBeforeDate(any(), any())).thenReturn(BigDecimal.ZERO);

        // When
        SutaReportDTO result = sutaReportService.generateQuarterlySutaReport(companyId, 2024, 1);

        // Then
        assertTrue(result.getNeedsPayment()); // Should need payment when tax > $500 threshold
    }

    // ========== generateAnnualSutaReport Tests ==========

    @Test
    void generateAnnualSutaReport_ValidData_ReturnsAnnualReport() {
        // Given
        Integer companyId = 1;
        Integer year = 2024;
        List<EmployerTaxRecord> taxRecords = Arrays.asList(testTaxRecord);

        when(companyRepository.findById(companyId)).thenReturn(Optional.of(testCompany));
        when(employerTaxRecordRepository.findByCompanyIdAndPeriodStartGreaterThanEqualAndPeriodEndLessThanEqual(
                eq(companyId), any(LocalDate.class), any(LocalDate.class))).thenReturn(taxRecords);
        when(paymentHistoryRepository.getTotalPaidForQuarterSUTA(anyInt(), anyInt(), anyInt())).thenReturn(new BigDecimal("50.00"));
        when(employerTaxRecordRepository.sumSutaTaxableWagesByEmployeeBeforeDate(any(), any())).thenReturn(BigDecimal.ZERO);

        // When
        SutaReportDTO result = sutaReportService.generateAnnualSutaReport(companyId, year);

        // Then
        assertNotNull(result);
        assertEquals("Annual", result.getReportType());
        assertEquals(companyId, result.getCompanyId());
        assertEquals(year, result.getTaxYear());
        assertNull(result.getQuarter());
        assertEquals(LocalDate.of(year, 1, 1), result.getPeriodStart());
        assertEquals(LocalDate.of(year, 12, 31), result.getPeriodEnd());
        assertNotNull(result.getQuarterlyBreakdown());
        assertEquals(4, result.getQuarterlyBreakdown().size()); // Should have all 4 quarters
    }

    @Test
    void generateAnnualSutaReport_CompanyNotFound_ThrowsException() {
        // Given
        Integer companyId = 999;
        when(companyRepository.findById(companyId)).thenReturn(Optional.empty());

        // When & Then
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> sutaReportService.generateAnnualSutaReport(companyId, 2024));
        assertTrue(exception.getMessage().contains("Компания не найдена: 999"));
    }

    @Test
    void generateAnnualSutaReport_NextPaymentDueCalculatedCorrectly() {
        // Given
        Integer companyId = 1;
        Integer year = 2024;

        when(companyRepository.findById(companyId)).thenReturn(Optional.of(testCompany));
        when(employerTaxRecordRepository.findByCompanyIdAndPeriodStartGreaterThanEqualAndPeriodEndLessThanEqual(
                anyInt(), any(), any())).thenReturn(Collections.emptyList());
        when(paymentHistoryRepository.getTotalPaidForQuarterSUTA(anyInt(), anyInt(), anyInt())).thenReturn(BigDecimal.ZERO);

        // When
        SutaReportDTO result = sutaReportService.generateAnnualSutaReport(companyId, year);

        // Then
        assertEquals(LocalDate.of(year + 1, 1, 31), result.getNextPaymentDue()); // NYS-45 due date
    }

    @Test
    void generateAnnualSutaReport_QuarterlyBreakdownContainsAllQuarters() {
        // Given
        Integer companyId = 1;
        Integer year = 2024;

        when(companyRepository.findById(companyId)).thenReturn(Optional.of(testCompany));
        when(employerTaxRecordRepository.findByCompanyIdAndPeriodStartGreaterThanEqualAndPeriodEndLessThanEqual(
                anyInt(), any(), any())).thenReturn(Arrays.asList(testTaxRecord));
        when(paymentHistoryRepository.getTotalPaidForQuarterSUTA(anyInt(), anyInt(), anyInt())).thenReturn(BigDecimal.ZERO);
        when(employerTaxRecordRepository.sumSutaTaxableWagesByEmployeeBeforeDate(any(), any())).thenReturn(BigDecimal.ZERO);

        // When
        SutaReportDTO result = sutaReportService.generateAnnualSutaReport(companyId, year);

        // Then
        List<QuarterlySutaDTO> breakdown = result.getQuarterlyBreakdown();
        assertEquals(4, breakdown.size());

        // Check that all quarters are present
        for (int i = 1; i <= 4; i++) {
            final int quarter = i;
            assertTrue(breakdown.stream().anyMatch(q -> q.getQuarter() == quarter));
        }
    }

    // ========== Edge Case Tests ==========

    @Test
    void generateQuarterlySutaReport_InvalidQuarter_ThrowsException() {
        // Given
        when(companyRepository.findById(anyInt())).thenReturn(Optional.of(testCompany));

        // When & Then
        assertThrows(IllegalArgumentException.class,
                () -> sutaReportService.generateQuarterlySutaReport(1, 2024, 5)); // Invalid quarter
        assertThrows(IllegalArgumentException.class,
                () -> sutaReportService.generateQuarterlySutaReport(1, 2024, 0)); // Invalid quarter
    }

    @Test
    void generateQuarterlySutaReport_EmployeeExceededWageLimit_CorrectCalculation() {
        // Given
        Integer companyId = 1;
        EmployerTaxRecord highYtdRecord = createTestTaxRecord();
        highYtdRecord.setGrossPay(new BigDecimal("5000.00"));

        when(companyRepository.findById(companyId)).thenReturn(Optional.of(testCompany));
        when(employerTaxRecordRepository.findByCompanyIdAndPeriodStartGreaterThanEqualAndPeriodEndLessThanEqual(
                anyInt(), any(), any())).thenReturn(Arrays.asList(highYtdRecord));
        when(paymentHistoryRepository.getTotalPaidForQuarterSUTA(anyInt(), anyInt(), anyInt())).thenReturn(BigDecimal.ZERO);
        // Mock employee already exceeded limit
        when(employerTaxRecordRepository.sumSutaTaxableWagesByEmployeeBeforeDate(any(), any()))
                .thenReturn(new BigDecimal("15000.00")); // Exceeded $13,000 limit

        // When
        SutaReportDTO result = sutaReportService.generateQuarterlySutaReport(companyId, 2024, 2);

        // Then
        assertEquals(1, result.getEmployeeDetails().size());
        assertTrue(result.getEmployeeDetails().get(0).getExceededLimit());
        assertEquals(BigDecimal.ZERO, result.getEmployeeDetails().get(0).getSutaWageBase()); // Should be zero since exceeded
    }

    @Test
    void generateQuarterlySutaReport_NullCompanySutaRate_UsesDefault() {
        // Given
        testCompany.setSocialSecurityTaxForCompany(null); // Null rate
        when(companyRepository.findById(anyInt())).thenReturn(Optional.of(testCompany));
        when(employerTaxRecordRepository.findByCompanyIdAndPeriodStartGreaterThanEqualAndPeriodEndLessThanEqual(
                anyInt(), any(), any())).thenReturn(Arrays.asList(testTaxRecord));
        when(paymentHistoryRepository.getTotalPaidForQuarterSUTA(anyInt(), anyInt(), anyInt())).thenReturn(BigDecimal.ZERO);
        when(employerTaxRecordRepository.sumSutaTaxableWagesByEmployeeBeforeDate(any(), any())).thenReturn(BigDecimal.ZERO);

        // When
        SutaReportDTO result = sutaReportService.generateQuarterlySutaReport(1, 2024, 1);

        // Then
        assertEquals(new BigDecimal("0.041"), result.getSutaRate()); // Default 4.1% converted to decimal
    }

    @Test
    void generateAnnualSutaReport_MultipleEmployeesWithDifferentWagesBases() {
        // Given
        User employee2 = createTestEmployee();
        employee2.setId(2);
        employee2.setFirstName("Jane");
        employee2.setLastName("Smith");

        EmployerTaxRecord record1 = createTestTaxRecord();
        record1.setGrossPay(new BigDecimal("8000.00"));

        EmployerTaxRecord record2 = createTestTaxRecord();
        record2.setEmployee(employee2);
        record2.setGrossPay(new BigDecimal("15000.00"));

        when(companyRepository.findById(anyInt())).thenReturn(Optional.of(testCompany));
        when(employerTaxRecordRepository.findByCompanyIdAndPeriodStartGreaterThanEqualAndPeriodEndLessThanEqual(
                anyInt(), any(), any())).thenReturn(Arrays.asList(record1, record2));
        when(paymentHistoryRepository.getTotalPaidForQuarterSUTA(anyInt(), anyInt(), anyInt())).thenReturn(BigDecimal.ZERO);
        when(employerTaxRecordRepository.sumSutaTaxableWagesByEmployeeBeforeDate(any(), any())).thenReturn(BigDecimal.ZERO);

        // When
        SutaReportDTO result = sutaReportService.generateAnnualSutaReport(1, 2024);

        // Then
        assertEquals(2, result.getTotalEmployees());
        assertEquals(2, result.getEmployeesSubjectToSuta());
        assertEquals(2, result.getEmployeeDetails().size());

        // Check total calculations
        assertEquals(new BigDecimal("23000.00"), result.getTotalGrossWages());
        assertEquals(new BigDecimal("21000.00"), result.getTotalSutaWageBase()); // 8000 + 13000 (capped)
    }

    // ========== Helper Methods ==========

    private Company createTestCompany() {
        Company company = new Company();
        company.setId(1);
        company.setCompanyName("Test Company");
        company.setCompanyAddress("123 Test St");
        company.setCompanyCity("Test City");
        company.setCompanyState("NY");
        company.setCompanyZipCode("12345");
        company.setCompanyPhone("555-0123");
        company.setEmployerEIN("12-3456789");
        company.setSocialSecurityTaxForCompany(new BigDecimal("4.1"));
        return company;
    }

    private User createTestEmployee() {
        User employee = new User();
        employee.setId(1);
        employee.setFirstName("John");
        employee.setLastName("Doe");
        return employee;
    }

    private EmployerTaxRecord createTestTaxRecord() {
        EmployerTaxRecord record = new EmployerTaxRecord();
        record.setId(1);
        record.setEmployee(testEmployee);
        record.setGrossPay(new BigDecimal("5000.00"));
        record.setPeriodStart(LocalDate.of(2024, 1, 1));
        record.setPeriodEnd(LocalDate.of(2024, 1, 31));
        return record;
    }
}