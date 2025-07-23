package com.zikpack.facecheck.taxesServices.customReportsForCompany;


import com.zikpak.facecheck.entity.Company;
import com.zikpak.facecheck.entity.EmployerTaxRecord;
import com.zikpak.facecheck.entity.User;
import com.zikpak.facecheck.repository.CompanyRepository;
import com.zikpak.facecheck.repository.EmployerTaxRecordRepository;
import com.zikpak.facecheck.repository.PaymentHistoryIrsRepository;
import com.zikpak.facecheck.repository.UserRepository;
import com.zikpak.facecheck.taxesServices.customReportsForCompanys.futaCustomTaxReport.FutaReportDTO;
import com.zikpak.facecheck.taxesServices.customReportsForCompanys.futaCustomTaxReport.FutaReportService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FutaReportServiceTest {

    @Mock
    private CompanyRepository companyRepository;

    @Mock
    private EmployerTaxRecordRepository employerTaxRecordRepository;

    @Mock
    private PaymentHistoryIrsRepository paymentHistoryRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private FutaReportService futaReportService;

    private Company testCompany;
    private User testEmployee1;
    private User testEmployee2;
    private List<EmployerTaxRecord> testTaxRecords;

    @BeforeEach
    void setUp() {
        // Setup test company
        testCompany = new Company();
        testCompany.setId(1);
        testCompany.setCompanyName("Test Company LLC");
        testCompany.setCompanyAddress("123 Main St");
        testCompany.setCompanyCity("New York");
        testCompany.setCompanyState("NY");
        testCompany.setCompanyZipCode("10001");
        testCompany.setCompanyPhone("555-1234");
        testCompany.setEmployerEIN("12-3456789");

        // Setup test employees
        testEmployee1 = new User();
        testEmployee1.setId(101);
        testEmployee1.setFirstName("John");
        testEmployee1.setLastName("Doe");

        testEmployee2 = new User();
        testEmployee2.setId(102);
        testEmployee2.setFirstName("Jane");
        testEmployee2.setLastName("Smith");

        // Setup test tax records
        EmployerTaxRecord record1 = new EmployerTaxRecord();
        record1.setEmployee(testEmployee1);
        record1.setGrossPay(new BigDecimal("3000.00"));
        record1.setPeriodStart(LocalDate.of(2024, 1, 1));
        record1.setPeriodEnd(LocalDate.of(2024, 1, 15));

        EmployerTaxRecord record2 = new EmployerTaxRecord();
        record2.setEmployee(testEmployee1);
        record2.setGrossPay(new BigDecimal("3000.00"));
        record2.setPeriodStart(LocalDate.of(2024, 1, 16));
        record2.setPeriodEnd(LocalDate.of(2024, 1, 31));

        EmployerTaxRecord record3 = new EmployerTaxRecord();
        record3.setEmployee(testEmployee2);
        record3.setGrossPay(new BigDecimal("4000.00"));
        record3.setPeriodStart(LocalDate.of(2024, 1, 1));
        record3.setPeriodEnd(LocalDate.of(2024, 1, 31));

        testTaxRecords = Arrays.asList(record1, record2, record3);
    }

    @Test
    void testGenerateQuarterlyFutaReport_Success() {
        // Given
        Integer companyId = 1;
        Integer year = 2024;
        Integer quarter = 1;

        when(companyRepository.findById(companyId)).thenReturn(Optional.of(testCompany));
        when(employerTaxRecordRepository.findByCompanyIdAndPeriodStartGreaterThanEqualAndPeriodEndLessThanEqual(
                eq(companyId), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(testTaxRecords);
        when(employerTaxRecordRepository.sumGrossPayByEmployeeBeforeDate(anyInt(), any(LocalDate.class)))
                .thenReturn(BigDecimal.ZERO);
        when(paymentHistoryRepository.getTotalPaidForQuarterFUTA(companyId, quarter, year))
                .thenReturn(new BigDecimal("50.00"));

        // When
        FutaReportDTO report = futaReportService.generateQuarterlyFutaReport(companyId, year, quarter);

        // Then
        assertNotNull(report);
        assertEquals("Test Company LLC", report.getCompanyName());
        assertEquals("Quarterly", report.getReportType());
        assertEquals(year, report.getTaxYear());
        assertEquals(quarter, report.getQuarter());

        // Verify calculations
        assertEquals(new BigDecimal("10000.00"), report.getTotalGrossWages()); // 3000 + 3000 + 4000
        assertEquals(new BigDecimal("10000.00"), report.getTotalFutaWageBase()); // All under $7000 limit

        // FUTA tax = 10000 * (0.006 + 0.003) = 90.00
        assertEquals(new BigDecimal("90.00").setScale(2), report.getTotalFutaTaxOwed().setScale(2, RoundingMode.HALF_UP));
        assertEquals(new BigDecimal("50.00"), report.getTotalFutaTaxPaid());
        assertEquals(new BigDecimal("40.00").setScale(2), report.getRemainingFutaLiability().setScale(2, RoundingMode.HALF_UP));

        assertFalse(report.getNeedsPayment()); // $90 < $500 threshold
        assertEquals(2, report.getTotalEmployees());
        assertEquals(2, report.getEmployeesSubjectToFuta());
    }

    @Test
    void testGenerateQuarterlyFutaReport_CompanyNotFound() {
        // Given
        Integer companyId = 999;
        when(companyRepository.findById(companyId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(EntityNotFoundException.class, () ->
                futaReportService.generateQuarterlyFutaReport(companyId, 2024, 1)
        );
    }

    @Test
    void testGenerateQuarterlyFutaReport_NoTaxRecords() {
        // Given
        Integer companyId = 1;
        Integer year = 2024;
        Integer quarter = 1;

        when(companyRepository.findById(companyId)).thenReturn(Optional.of(testCompany));
        when(employerTaxRecordRepository.findByCompanyIdAndPeriodStartGreaterThanEqualAndPeriodEndLessThanEqual(
                eq(companyId), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(Collections.emptyList());
        when(paymentHistoryRepository.getTotalPaidForQuarterFUTA(companyId, quarter, year))
                .thenReturn(BigDecimal.ZERO);

        // When
        FutaReportDTO report = futaReportService.generateQuarterlyFutaReport(companyId, year, quarter);

        // Then
        assertNotNull(report);
        assertEquals(BigDecimal.ZERO, report.getTotalGrossWages());
        assertEquals(BigDecimal.ZERO, report.getTotalFutaWageBase());
        assertEquals(BigDecimal.ZERO, report.getTotalFutaTaxOwed());
        assertEquals(0, report.getTotalEmployees());
        assertFalse(report.getNeedsPayment());
    }

    @Test
    void testGenerateQuarterlyFutaReport_EmployeeExceedsWageLimit() {
        // Given
        Integer companyId = 1;
        Integer year = 2024;
        Integer quarter = 2;

        // Create tax record for employee who already earned $6000 in Q1
        EmployerTaxRecord q2Record = new EmployerTaxRecord();
        q2Record.setEmployee(testEmployee1);
        q2Record.setGrossPay(new BigDecimal("3000.00")); // This would put them at $9000 YTD
        q2Record.setPeriodStart(LocalDate.of(2024, 4, 1));
        q2Record.setPeriodEnd(LocalDate.of(2024, 4, 30));

        when(companyRepository.findById(companyId)).thenReturn(Optional.of(testCompany));
        when(employerTaxRecordRepository.findByCompanyIdAndPeriodStartGreaterThanEqualAndPeriodEndLessThanEqual(
                eq(companyId), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(Collections.singletonList(q2Record));

        // Mock that employee already earned $6000 before Q2
        when(employerTaxRecordRepository.sumGrossPayByEmployeeBeforeDate(
                eq(testEmployee1.getId()), any(LocalDate.class)))
                .thenReturn(new BigDecimal("6000.00"));

        when(paymentHistoryRepository.getTotalPaidForQuarterFUTA(companyId, quarter, year))
                .thenReturn(BigDecimal.ZERO);

        // When
        FutaReportDTO report = futaReportService.generateQuarterlyFutaReport(companyId, year, quarter);

        // Then
        assertNotNull(report);
        assertEquals(new BigDecimal("3000.00"), report.getTotalGrossWages());
        // Only $1000 subject to FUTA (7000 limit - 6000 YTD = 1000)
        assertEquals(new BigDecimal("1000.00"), report.getTotalFutaWageBase());
        // FUTA tax = 1000 * 0.009 = 9.00
        assertEquals(new BigDecimal("9.00").setScale(2), report.getTotalFutaTaxOwed().setScale(2, RoundingMode.HALF_UP));
    }

    @Test
    void testGenerateAnnualFutaReport_Success() {
        // Given
        Integer companyId = 1;
        Integer year = 2024;

        when(companyRepository.findById(companyId)).thenReturn(Optional.of(testCompany));

        // Mock для всех вызовов с any() матчерами
        when(employerTaxRecordRepository.findByCompanyIdAndPeriodStartGreaterThanEqualAndPeriodEndLessThanEqual(
                eq(companyId), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(testTaxRecords) // Для годового расчета
                .thenReturn(testTaxRecords) // Q1
                .thenReturn(Collections.emptyList()) // Q2
                .thenReturn(Collections.emptyList()) // Q3
                .thenReturn(Collections.emptyList()); // Q4

        when(employerTaxRecordRepository.sumGrossPayByEmployeeBeforeDate(anyInt(), any(LocalDate.class)))
                .thenReturn(BigDecimal.ZERO);
        when(paymentHistoryRepository.getTotalPaidForFUTA(companyId, year))
                .thenReturn(new BigDecimal("90.00"));
        when(paymentHistoryRepository.getTotalPaidForQuarterFUTA(eq(companyId), anyInt(), eq(year)))
                .thenReturn(new BigDecimal("22.50")); // $90 / 4 quarters

        // When
        FutaReportDTO report = futaReportService.generateAnnualFutaReport(companyId, year);

        // Then
        assertNotNull(report);
        assertEquals("Annual", report.getReportType());
        assertEquals(year, report.getTaxYear());
        assertNull(report.getQuarter());
        assertEquals(LocalDate.of(2024, 1, 1), report.getPeriodStart());
        assertEquals(LocalDate.of(2024, 12, 31), report.getPeriodEnd());

        // Verify quarterly breakdown exists
        assertNotNull(report.getQuarterlyBreakdown());
        assertEquals(4, report.getQuarterlyBreakdown().size());

        // Verify next payment due is Form 940 deadline
        assertEquals(LocalDate.of(2025, 1, 31), report.getNextPaymentDue());

        // Verify compliance notes
        assertNotNull(report.getNotes());
        assertTrue(report.getNotes().stream().anyMatch(note -> note.contains("Form 940")));
    }

    @Test
    void testQuarterlyPaymentThreshold() {
        // Given - Set up for tax liability over $500
        Integer companyId = 1;
        Integer year = 2024;
        Integer quarter = 1;

        // Create records that will generate > $500 FUTA tax
        EmployerTaxRecord bigRecord = new EmployerTaxRecord();
        bigRecord.setEmployee(testEmployee1);
        bigRecord.setGrossPay(new BigDecimal("60000.00")); // Will be capped at $7000
        bigRecord.setPeriodStart(LocalDate.of(2024, 1, 1));
        bigRecord.setPeriodEnd(LocalDate.of(2024, 3, 31));

        when(companyRepository.findById(companyId)).thenReturn(Optional.of(testCompany));
        when(employerTaxRecordRepository.findByCompanyIdAndPeriodStartGreaterThanEqualAndPeriodEndLessThanEqual(
                eq(companyId), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(Collections.singletonList(bigRecord));
        when(employerTaxRecordRepository.sumGrossPayByEmployeeBeforeDate(anyInt(), any(LocalDate.class)))
                .thenReturn(BigDecimal.ZERO);
        when(paymentHistoryRepository.getTotalPaidForQuarterFUTA(companyId, quarter, year))
                .thenReturn(BigDecimal.ZERO);

        // When
        FutaReportDTO report = futaReportService.generateQuarterlyFutaReport(companyId, year, quarter);

        // Then
        // FUTA tax = $7000 * 0.009 = $63.00
        assertEquals(new BigDecimal("63.00").setScale(2), report.getTotalFutaTaxOwed().setScale(2, RoundingMode.HALF_UP));
        assertFalse(report.getNeedsPayment()); // $63 < $500 threshold

        // Verify compliance note about threshold
        assertTrue(report.getNotes().stream().anyMatch(note ->
                note.contains("No quarterly payment required") && note.contains("under $500")
        ));
    }
}