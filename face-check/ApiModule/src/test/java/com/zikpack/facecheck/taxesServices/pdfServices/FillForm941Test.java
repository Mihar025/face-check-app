package com.zikpack.facecheck.taxesServices.pdfServices;


import com.zikpak.facecheck.entity.Company;
import com.zikpak.facecheck.entity.CompanyPaymentPosition;
import com.zikpak.facecheck.entity.User;
import com.zikpak.facecheck.entity.employee.WorkerPayroll;
import com.zikpak.facecheck.metrics.MetricsForPdfServices;
import com.zikpak.facecheck.repository.CompanyRepository;
import com.zikpak.facecheck.repository.EmployerTaxRecordRepository;
import com.zikpak.facecheck.repository.UserRepository;
import com.zikpak.facecheck.repository.WorkerPayrollRepository;
import com.zikpak.facecheck.services.amazonS3Service.AmazonS3Service;
import com.zikpak.facecheck.taxesServices.pdfServices.FillForm941;
import com.zikpak.facecheck.taxesServices.services.PaymentHistoryService;
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
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FillForm941Test {

    @Mock
    private UserRepository userRepository;

    @Mock
    private CompanyRepository companyRepository;

    @Mock
    private EmployerTaxRecordRepository taxRecordRepo;

    @Mock
    private PaymentHistoryService paymentHistoryService;

    @Mock
    private AmazonS3Service amazonS3Service;

    @Mock
    private WorkerPayrollRepository payrollRepo;

    @Mock
    private MetricsForPdfServices metric;

    @InjectMocks
    private FillForm941 fillForm941;

    private User testUser;
    private Company testCompany;
    private User testWorker;
    private WorkerPayroll testPayroll;

    @BeforeEach
    void setUp() {
        // Создаем тестовые данные
        testUser = createTestUser();
        testCompany = createTestCompany();
        testWorker = createTestWorker();
        testPayroll = createTestPayroll();
    }

    @Test
    void generateFilledPdf_SuccessfulGeneration_ReturnsValidPdf() throws IOException {
        // Given
        Integer userId = 1;
        Integer companyId = 1;
        int year = 2024;
        int quarter = 1;

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(companyRepository.findById(companyId)).thenReturn(Optional.of(testCompany));

        // Создаем правильный payroll с корректными данными
        WorkerPayroll correctPayroll = new WorkerPayroll();
        correctPayroll.setWorker(testWorker);
        correctPayroll.setGrossPay(new BigDecimal("1500.00"));
        correctPayroll.setFederalWithholding(new BigDecimal("150.00"));
        // ВАЖНО: это должны быть налогооблагаемые базы, а не сами налоги!
        correctPayroll.setSocialSecurityEmployee(new BigDecimal("1500.00")); // База, а не налог
        correctPayroll.setMedicare(new BigDecimal("1500.00")); // База, а не налог
        correctPayroll.setPeriodStart(LocalDate.of(2024, 1, 1));
        correctPayroll.setPeriodEnd(LocalDate.of(2024, 1, 7));

        when(payrollRepo.findAllByCompanyIdAndPeriodBetween(eq(companyId), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(Arrays.asList(correctPayroll));

        when(paymentHistoryService.getTotalPaymentsForQuarter941Form(companyId, quarter, year))
                .thenReturn(new BigDecimal("1000.00"));

        // Mock для квартальных данных
        when(taxRecordRepo.countDistinctEmployeesByCompanyAndYear(eq(companyId), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(1);


        // Данные за предыдущий квартал (Q4 2023)
        when(taxRecordRepo.sumGrossWages(companyId, LocalDate.of(2023, 10, 1), LocalDate.of(2023, 12, 31)))
                .thenReturn(new BigDecimal("1500.00"));
        when(taxRecordRepo.sumFederalWithholding(companyId, LocalDate.of(2023, 10, 1), LocalDate.of(2023, 12, 31)))
                .thenReturn(new BigDecimal("150.00"));
        when(taxRecordRepo.sumSocialSecurityTaxableWages(companyId, LocalDate.of(2023, 10, 1), LocalDate.of(2023, 12, 31)))
                .thenReturn(new BigDecimal("1500.00"));
        when(taxRecordRepo.sumMedicareTaxableWages(companyId, LocalDate.of(2023, 10, 1), LocalDate.of(2023, 12, 31)))
                .thenReturn(new BigDecimal("1500.00"));

        // Месячные данные - должны в сумме давать квартальные
        // Январь 2024
        when(taxRecordRepo.sumGrossWages(companyId, LocalDate.of(2024, 1, 1), LocalDate.of(2024, 1, 31)))
                .thenReturn(new BigDecimal("500.00"));
        when(taxRecordRepo.sumFederalWithholding(companyId, LocalDate.of(2024, 1, 1), LocalDate.of(2024, 1, 31)))
                .thenReturn(new BigDecimal("50.00"));
        when(taxRecordRepo.sumSocialSecurityTaxableWages(companyId, LocalDate.of(2024, 1, 1), LocalDate.of(2024, 1, 31)))
                .thenReturn(new BigDecimal("500.00"));
        when(taxRecordRepo.sumMedicareTaxableWages(companyId, LocalDate.of(2024, 1, 1), LocalDate.of(2024, 1, 31)))
                .thenReturn(new BigDecimal("500.00"));

        // Февраль 2024
        when(taxRecordRepo.sumGrossWages(companyId, LocalDate.of(2024, 2, 1), LocalDate.of(2024, 2, 29)))
                .thenReturn(new BigDecimal("500.00"));
        when(taxRecordRepo.sumFederalWithholding(companyId, LocalDate.of(2024, 2, 1), LocalDate.of(2024, 2, 29)))
                .thenReturn(new BigDecimal("50.00"));
        when(taxRecordRepo.sumSocialSecurityTaxableWages(companyId, LocalDate.of(2024, 2, 1), LocalDate.of(2024, 2, 29)))
                .thenReturn(new BigDecimal("500.00"));
        when(taxRecordRepo.sumMedicareTaxableWages(companyId, LocalDate.of(2024, 2, 1), LocalDate.of(2024, 2, 29)))
                .thenReturn(new BigDecimal("500.00"));

        // Март 2024
        when(taxRecordRepo.sumGrossWages(companyId, LocalDate.of(2024, 3, 1), LocalDate.of(2024, 3, 31)))
                .thenReturn(new BigDecimal("500.00"));
        when(taxRecordRepo.sumFederalWithholding(companyId, LocalDate.of(2024, 3, 1), LocalDate.of(2024, 3, 31)))
                .thenReturn(new BigDecimal("50.00"));
        when(taxRecordRepo.sumSocialSecurityTaxableWages(companyId, LocalDate.of(2024, 3, 1), LocalDate.of(2024, 3, 31)))
                .thenReturn(new BigDecimal("500.00"));
        when(taxRecordRepo.sumMedicareTaxableWages(companyId, LocalDate.of(2024, 3, 1), LocalDate.of(2024, 3, 31)))
                .thenReturn(new BigDecimal("500.00"));

        // Все остальные возможные вызовы возвращают ноль
        when(taxRecordRepo.sumSocialSecurityTips(eq(companyId), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(BigDecimal.ZERO);
        when(taxRecordRepo.sumAdditionalMedicareTaxableWages(eq(companyId), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(BigDecimal.ZERO);

        // Mock metrics
        when(metric.startTimer()).thenReturn(mock(io.micrometer.core.instrument.Timer.Sample.class));

        // When
        byte[] result = fillForm941.generateFilledPdf(userId, companyId, year, quarter);

        // Then
        assertNotNull(result);
        assertTrue(result.length > 0);

        // Verify interactions
        verify(userRepository).findById(userId);
        verify(companyRepository).findById(companyId);
        verify(amazonS3Service).uploadPdfToS3(eq(result), anyString());
        verify(metric).recordGenerated("941", true);
        verify(metric).recordRequest("941");
    }
    @Test
    void generateFilledPdf_UserNotFound_ThrowsEntityNotFoundException() {
        // Given
        Integer userId = 999;
        Integer companyId = 1;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // When & Then
        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> fillForm941.generateFilledPdf(userId, companyId, 2024, 1)
        );

        assertEquals("User Not Found", exception.getMessage());
        verify(metric).recordRequest("941");
    }

    @Test
    void generateFilledPdf_CompanyNotFound_ThrowsEntityNotFoundException() {
        // Given
        Integer userId = 1;
        Integer companyId = 999;

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(companyRepository.findById(companyId)).thenReturn(Optional.empty());

        // When & Then
        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> fillForm941.generateFilledPdf(userId, companyId, 2024, 1)
        );

        assertEquals("Company Not Found", exception.getMessage());
    }

    @Test
    void generateFilledPdf_InvalidEINFormat_ThrowsIllegalStateException() {
        // Given
        testCompany.setEmployerEIN("invalid-ein-format-123");

        when(userRepository.findById(anyInt())).thenReturn(Optional.of(testUser));
        when(companyRepository.findById(anyInt())).thenReturn(Optional.of(testCompany));

        // When & Then
        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> fillForm941.generateFilledPdf(1, 1, 2024, 1)
        );

        assertTrue(exception.getMessage().contains("Некорректный формат EIN"));
    }

    @Test
    void getEmployeeCountForLine1_ReturnsCorrectCount() {
        // Given
        Integer companyId = 1;
        int year = 2024;
        int quarter = 1;

        // Мокируем payroll записи для каждого месяца квартала
        LocalDate jan12 = LocalDate.of(2024, 1, 12);
        LocalDate feb12 = LocalDate.of(2024, 2, 12);
        LocalDate mar12 = LocalDate.of(2024, 3, 12);

        WorkerPayroll payroll1 = createTestPayrollForPeriod(jan12.minusDays(7), jan12.plusDays(7));
        WorkerPayroll payroll2 = createTestPayrollForPeriod(feb12.minusDays(7), feb12.plusDays(7));

        when(payrollRepo.findAllByCompanyIdAndPeriodBetween(eq(companyId), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(Arrays.asList(payroll1, payroll2));

        // When
        int result = fillForm941.getEmployeeCountForLine1(companyId, year, quarter);

        // Then
        assertEquals(1, result); // Один уникальный работник
    }

    @Test
    void splitAmount_CorrectlySplitsDecimal() {
        // Given
        BigDecimal amount = new BigDecimal("1234.56");

        // When
        String[] result = fillForm941.splitAmount(amount);

        // Then
        assertEquals(2, result.length);
        assertEquals("1234", result[0]);
        assertEquals("56", result[1]);
    }

    @Test
    void splitAmount_HandlesZeroAmount() {
        // Given
        BigDecimal amount = BigDecimal.ZERO;

        // When
        String[] result = fillForm941.splitAmount(amount);

        // Then
        assertEquals("0", result[0]);
        assertEquals("00", result[1]);
    }

    @Test
    void splitAmount_HandlesNullAmount() {
        // When
        String[] result = fillForm941.splitAmount(null);

        // Then
        assertEquals("0", result[0]);
        assertEquals("00", result[1]);
    }

    @Test
    void spacedDigits_AddsSpacesBetweenDigits() {
        // Given
        String input = "123";

        // When
        String result = fillForm941.spacedDigits(input);

        // Then
        assertTrue(result.contains("1"));
        assertTrue(result.contains("2"));
        assertTrue(result.contains("3"));
        assertTrue(result.length() > input.length()); // Должно быть длиннее из-за пробелов
    }

    // Helper methods для создания тестовых данных
    private User createTestUser() {
        User user = new User();
        user.setId(1);
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setPhoneNumber("123-456-7890");
        user.setHomeAddress("123 Test St");
        user.setCity("Test City");
        user.setState("NY");
        user.setZipcode("12345");
        return user;
    }

    private Company createTestCompany() {
        Company company = new Company();
        company.setId(1);
        company.setCompanyName("Test Company Inc");
        company.setEmployerEIN("12-3456789");
        company.setCompanyAddress("456 Business Ave");
        company.setCompanyCity("Business City");
        company.setCompanyState("NY");
        company.setCompanyZipCode("54321");
        company.setCompanyPaymentPosition(CompanyPaymentPosition.WEEKLY);
        return company;
    }

    private User createTestWorker() {
        User worker = new User();
        worker.setId(1);
        worker.setFirstName("Jane");
        worker.setLastName("Worker");
        return worker;
    }

    private WorkerPayroll createTestPayroll() {
        WorkerPayroll payroll = new WorkerPayroll();
        payroll.setWorker(testWorker);
        payroll.setGrossPay(new BigDecimal("1000.00"));
        payroll.setFederalWithholding(new BigDecimal("100.00"));
        payroll.setSocialSecurityEmployee(new BigDecimal("62.00"));
        payroll.setMedicare(new BigDecimal("14.50"));
        payroll.setPeriodStart(LocalDate.of(2024, 1, 1));
        payroll.setPeriodEnd(LocalDate.of(2024, 1, 7));
        return payroll;
    }

    private WorkerPayroll createTestPayrollForPeriod(LocalDate start, LocalDate end) {
        WorkerPayroll payroll = createTestPayroll();
        payroll.setPeriodStart(start);
        payroll.setPeriodEnd(end);
        return payroll;
    }
}
