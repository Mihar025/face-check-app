package com.zikpack.facecheck.taxesServices.pdfServices;

import com.zikpak.facecheck.entity.Company;
import com.zikpak.facecheck.entity.User;
import com.zikpak.facecheck.entity.employee.WorkerPayroll;
import com.zikpak.facecheck.entity.CompanyPaymentPosition;
import com.zikpak.facecheck.repository.WorkerAttendanceRepository;
import com.zikpak.facecheck.repository.WorkerPayrollRepository;
import com.zikpak.facecheck.services.amazonS3Service.AmazonS3Service;
import com.zikpak.facecheck.taxesServices.dto.PayStubFileDTO;
import com.zikpak.facecheck.taxesServices.pdfServices.PayStubPdfGeneratorService;
import com.zikpak.facecheck.taxesServices.services.PayStubService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PayStubServiceTest {

    @Mock
    private WorkerPayrollRepository workerPayrollRepository;

    @Mock
    private WorkerAttendanceRepository attendanceRepository;

    @Mock
    private PayStubPdfGeneratorService payStubPdfGeneratorService;

    @Mock
    private AmazonS3Service amazonS3Service;

    @InjectMocks
    private PayStubService payStubService;

    private WorkerPayroll mockPayroll;
    private User mockWorker;
    private Company mockCompany;

    @BeforeEach
    void setUp() {
        mockCompany = new Company();
        mockCompany.setId(1);
        mockCompany.setCompanyName("Test Company");
        mockCompany.setCompanyAddress("123 Test St");
        mockCompany.setCompanyCity("Test City");
        mockCompany.setCompanyState("NY");
        mockCompany.setCompanyZipCode("12345");
        mockCompany.setCompanyPhone("555-1234");
        // Добавляем CompanyPaymentPosition - это важно!
        mockCompany.setCompanyPaymentPosition(CompanyPaymentPosition.BIWEEKLY);

        mockWorker = new User();
        mockWorker.setId(1);
        mockWorker.setFirstName("John");
        mockWorker.setLastName("Doe");
        mockWorker.setSSN_WORKER("123-45-6789");
        mockWorker.setHomeAddress("456 Worker St");
        mockWorker.setCity("Worker City");
        mockWorker.setState("NY");
        mockWorker.setZipcode("54321");
        mockWorker.setPhoneNumber("555-5678");
        mockWorker.setBaseHourlyRate(BigDecimal.valueOf(20.00));
        mockWorker.setCompany(mockCompany);

        mockPayroll = new WorkerPayroll();
        mockPayroll.setId(1);
        mockPayroll.setWorker(mockWorker);
        mockPayroll.setPeriodStart(LocalDate.of(2024, 1, 1));
        mockPayroll.setPeriodEnd(LocalDate.of(2024, 1, 7));
        mockPayroll.setGrossPay(BigDecimal.valueOf(800.00));
        mockPayroll.setNetPay(BigDecimal.valueOf(600.00));
        mockPayroll.setTotalHours(40.0);
    }

    @Test
    void generatePayStubPdf_ShouldReturnPdfBytes_WhenValidPayrollId() {
        // Given
        byte[] expectedPdf = "fake pdf content".getBytes();

        when(workerPayrollRepository.findById(1)).thenReturn(Optional.of(mockPayroll));
        when(attendanceRepository.findAllByWorkerIdAndCheckInTimeBetween(any(), any(), any()))
                .thenReturn(Arrays.asList());
        when(workerPayrollRepository.findAllByWorkerIdAndPeriodEndBetween(any(), any(), any()))
                .thenReturn(Arrays.asList(mockPayroll));
        when(payStubPdfGeneratorService.generatePayStubPdf(any())).thenReturn(expectedPdf);

        // When
        byte[] result = payStubService.generatePayStubPdf(1);

        // Then
        assertNotNull(result);
        assertEquals(expectedPdf, result);
        verify(amazonS3Service).uploadPdfToS3(eq(expectedPdf), anyString());
    }

    @Test
    void generatePayStubPdf_ShouldThrowException_WhenPayrollNotFound() {
        // Given
        when(workerPayrollRepository.findById(999)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> payStubService.generatePayStubPdf(999));

        assertEquals("Payroll not found", exception.getMessage());
    }

    @Test
    void getPayStubFilesList_ShouldReturnListOfFiles_WhenPayrollsExist() {
        // Given
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 1, 31);

        when(workerPayrollRepository.findAllByCompanyIdAndPeriodBetween(1, startDate, endDate))
                .thenReturn(Arrays.asList(mockPayroll));

        // When
        List<PayStubFileDTO> result = payStubService.getPayStubFilesList(1, startDate, endDate);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());

        PayStubFileDTO file = result.get(0);
        assertEquals("PayStub_John_Doe_2024-01-01_to_2024-01-07.pdf", file.getFileName());
        assertEquals("John Doe", file.getEmployeeName());
        assertEquals("/taxes-forms/download-paystub/1", file.getDownloadUrl());
    }

    @Test
    void getWorkerPayStubFilesList_ShouldReturnFilteredList_WhenWorkerIdMatches() {
        // Given
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 1, 31);

        when(workerPayrollRepository.findAllByCompanyIdAndPeriodBetween(1, startDate, endDate))
                .thenReturn(Arrays.asList(mockPayroll));

        // When
        List<PayStubFileDTO> result = payStubService.getWorkerPayStubFilesList(1, 1, startDate, endDate);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("John Doe", result.get(0).getEmployeeName());
    }

    @Test
    void getWorkerPayStubFilesList_ShouldReturnEmptyList_WhenWorkerIdDoesNotMatch() {
        // Given
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 1, 31);

        when(workerPayrollRepository.findAllByCompanyIdAndPeriodBetween(1, startDate, endDate))
                .thenReturn(Arrays.asList(mockPayroll));

        // When
        List<PayStubFileDTO> result = payStubService.getWorkerPayStubFilesList(1, 999, startDate, endDate);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void getGroupedPayStubFilesList_ShouldGroupByEmployeeName() {
        // Given
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 1, 31);

        when(workerPayrollRepository.findAllByCompanyIdAndPeriodBetween(1, startDate, endDate))
                .thenReturn(Arrays.asList(mockPayroll));

        // When
        var result = payStubService.getGroupedPayStubFilesList(1, startDate, endDate);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.containsKey("John Doe"));
        assertEquals(1, result.get("John Doe").size());
    }
}