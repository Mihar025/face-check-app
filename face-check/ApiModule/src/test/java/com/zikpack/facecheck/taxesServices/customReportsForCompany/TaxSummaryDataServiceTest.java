package com.zikpack.facecheck.taxesServices.customReportsForCompany;

import com.zikpak.facecheck.entity.Company;
import com.zikpak.facecheck.entity.EmployerTaxRecord;
import com.zikpak.facecheck.entity.User;
import com.zikpak.facecheck.entity.employee.WorkerPayroll;
import com.zikpak.facecheck.repository.CompanyRepository;
import com.zikpak.facecheck.repository.EmployerTaxRecordRepository;
import com.zikpak.facecheck.repository.PaymentHistoryIrsRepository;
import com.zikpak.facecheck.repository.WorkerPayrollRepository;
import com.zikpak.facecheck.taxesServices.customReportsForCompanys.taxSummary.TaxSummaryDataService;
import com.zikpak.facecheck.taxesServices.customReportsForCompanys.taxSummary.TaxSummaryReportDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaxSummaryDataServiceTest {

    @Mock
    private EmployerTaxRecordRepository employerTaxRecordRepository;

    @Mock
    private PaymentHistoryIrsRepository paymentHistoryRepository;

    @Mock
    private CompanyRepository companyRepository;

    @Mock
    private WorkerPayrollRepository workerPayrollRepository;

    @InjectMocks
    private TaxSummaryDataService taxSummaryDataService;

    private Company testCompany;
    private List<EmployerTaxRecord> testTaxRecords;
    private List<WorkerPayroll> testPayrolls;

    @BeforeEach
    void setUp() {
        testCompany = Company.builder()
                .id(1)
                .companyName("Test Company")
                .companyAddress("123 Test Street")
                .companyCity("Test City")
                .companyState("CA")
                .companyZipCode("12345")
                .companyPhone("555-1234")
                .employerEIN("12-3456789")
                .build();

        // Создаем тестового пользователя/сотрудника
        User testEmployee = User.builder()
                .id(1)
                .firstName("John")
                .lastName("Doe")
                .build();

        testTaxRecords = List.of(
                EmployerTaxRecord.builder()
                        .id(1)
                        .company(testCompany)  // Используем company объект, а не companyId
                        .employee(testEmployee)
                        .federalWithholding(BigDecimal.valueOf(1000))  // federalWithholding, не federalTax
                        .socialSecurityTax(BigDecimal.valueOf(500))
                        .medicareTax(BigDecimal.valueOf(200))
                        .futaTax(BigDecimal.valueOf(50))
                        .sutaTax(BigDecimal.valueOf(75))
                        .grossPay(BigDecimal.valueOf(5000))  // добавляем grossPay
                        .socialSecurityTaxableWages(BigDecimal.valueOf(4800))
                        .medicareTaxableWages(BigDecimal.valueOf(4800))
                        .futaTaxableWages(BigDecimal.valueOf(4800))
                        .sutaTaxableWages(BigDecimal.valueOf(4800))
                        .totalEmployerTax(BigDecimal.valueOf(825))  // сумма employer taxes
                        .periodStart(LocalDate.of(2024, 1, 1))
                        .periodEnd(LocalDate.of(2024, 1, 31))
                        .build()
        );

        testPayrolls = List.of(
                WorkerPayroll.builder()
                        .id(1)
                        .company(testCompany)  // Используем company объект
                        .worker(testEmployee)  // worker, а не employee
                        .grossPay(BigDecimal.valueOf(5000))  // grossPay, а не grossWages
                        .regularPay(BigDecimal.valueOf(4800))
                        .totalHours(40.0)
                        .regularHours(40.0)
                        .overtimeHours(0.0)
                        .baseHourlyRate(BigDecimal.valueOf(25.00))
                        .medicare(BigDecimal.valueOf(72.50))
                        .socialSecurityEmployee(BigDecimal.valueOf(310.00))
                        .federalWithholding(BigDecimal.valueOf(1000))
                        .totalDeductions(BigDecimal.valueOf(1382.50))
                        .netPay(BigDecimal.valueOf(3617.50))
                        .periodStart(LocalDate.of(2024, 1, 1))
                        .periodEnd(LocalDate.of(2024, 1, 31))
                        .build()
        );
    }



    @Test
    void shouldThrowException_WhenCompanyNotFound() {
        // Given
        Integer companyId = 999;
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 3, 31);

        when(companyRepository.findById(companyId)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                taxSummaryDataService.generateTaxSummaryReport(companyId, startDate, endDate));

        assertThat(exception.getMessage()).isEqualTo("Company not found");
        verify(companyRepository).findById(companyId);
        verifyNoInteractions(employerTaxRecordRepository, workerPayrollRepository);
    }


}