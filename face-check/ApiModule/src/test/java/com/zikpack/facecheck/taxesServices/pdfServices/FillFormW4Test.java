package com.zikpack.facecheck.taxesServices.pdfServices;

import com.zikpak.facecheck.entity.Company;
import com.zikpak.facecheck.entity.Dependents;
import com.zikpak.facecheck.entity.User;
import com.zikpak.facecheck.entity.W4.FilingStatus;
import com.zikpak.facecheck.entity.W4.PayFrequency;
import com.zikpak.facecheck.metrics.MetricsForPdfServices;
import com.zikpak.facecheck.repository.CompanyRepository;
import com.zikpak.facecheck.repository.DependentRepository;
import com.zikpak.facecheck.repository.UserRepository;
import com.zikpak.facecheck.services.amazonS3Service.AmazonS3Service;
import com.zikpak.facecheck.taxesServices.pdfServices.FillFormW4;
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
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FillFormW4Test {

    @Mock
    private UserRepository userRepository;

    @Mock
    private DependentRepository dependentRepository;

    @Mock
    private CompanyRepository companyRepository;

    @Mock
    private AmazonS3Service amazonS3Service;

    @Mock
    private MetricsForPdfServices metricsForPdfServices;

    @InjectMocks
    private FillFormW4 fillFormW4;

    private User testUser;
    private Company testCompany;

    @BeforeEach
    void setUp() {
        // Создаем тестового пользователя
        testUser = new User();
        testUser.setId(1);
        testUser.setFirstName("John");
        testUser.setLastName("Doe");
        testUser.setMiddleInitial("M");
        testUser.setHomeAddress("123 Main St");
        testUser.setCity("New York");
        testUser.setState("NY");
        testUser.setZipcode("10001");
        testUser.setSSN_WORKER("123-45-6789");
        testUser.setFilingStatus(FilingStatus.SINGLE);
        testUser.setTwoJobsCheckBox(false);
        testUser.setMultipleJobsAdditionalWithholding(new BigDecimal("100"));
        testUser.setExtraWithHoldings(new BigDecimal("50"));
        testUser.setOtherIncome(new BigDecimal("1000"));
        testUser.setDeductions(new BigDecimal("500"));
        testUser.setPayFrequency(PayFrequency.BIWEEKLY);
        testUser.setCreatedDate(LocalDateTime.now());

        // Создаем тестовую компанию
        testCompany = new Company();
        testCompany.setId(1);
        testCompany.setCompanyName("Test Company Inc");
        testCompany.setCompanyAddress("456 Business Ave");
        testCompany.setEmployerEIN("12-3456789");
    }

    @Test
    void testGenerateW4Pdf_Success() throws IOException {
        // Given
        when(userRepository.findById(1)).thenReturn(Optional.of(testUser));
        when(companyRepository.findById(1)).thenReturn(Optional.of(testCompany));
        when(dependentRepository.findAllByUser_Id(1)).thenReturn(Collections.emptyList());
        when(metricsForPdfServices.startTimer()).thenReturn(mock(io.micrometer.core.instrument.Timer.Sample.class));

        // When
        byte[] result = fillFormW4.generateW4Pdf(1, 1);

        // Then
        assertNotNull(result);
        assertTrue(result.length > 0);

        // Проверяем вызовы метрик
        verify(metricsForPdfServices).recordRequest("W4");
        verify(metricsForPdfServices).recordGenerated("W4", true);
        verify(metricsForPdfServices).recordS3UploadTime(eq("W4"), eq(true), anyLong());

        // Проверяем загрузку в S3
        verify(amazonS3Service).uploadPdfToS3(any(byte[].class), contains("Test_Company_Inc/W4/John_Doe.pdf"));
    }

    @Test
    void testGenerateW4Pdf_UserNotFound() {
        // Given
        when(userRepository.findById(999)).thenReturn(Optional.empty());

        // When & Then
        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> fillFormW4.generateW4Pdf(999, 1)
        );

        assertEquals("User with id 999 not found", exception.getMessage());
        verify(metricsForPdfServices).recordRequest("W4");
        verify(metricsForPdfServices).recordGenerated("W4", false);
    }

    @Test
    void testGenerateW4Pdf_CompanyNotFound() {
        // Given
        when(userRepository.findById(1)).thenReturn(Optional.of(testUser));
        when(companyRepository.findById(999)).thenReturn(Optional.empty());

        // When & Then
        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> fillFormW4.generateW4Pdf(1, 999)
        );

        assertEquals("Company with id 999 not found", exception.getMessage());
    }

    @Test
    void testGenerateW4Pdf_WithDependents() throws IOException {
        // Given
        Dependents child = new Dependents();
        child.setBirthDate(LocalDate.now().minusYears(10)); // 10 лет - младше 17

        Dependents adult = new Dependents();
        adult.setBirthDate(LocalDate.now().minusYears(20)); // 20 лет - старше 17

        when(userRepository.findById(1)).thenReturn(Optional.of(testUser));
        when(companyRepository.findById(1)).thenReturn(Optional.of(testCompany));
        when(dependentRepository.findAllByUser_Id(1)).thenReturn(Arrays.asList(child, adult));
        when(metricsForPdfServices.startTimer()).thenReturn(mock(io.micrometer.core.instrument.Timer.Sample.class));

        // When
        byte[] result = fillFormW4.generateW4Pdf(1, 1);

        // Then
        assertNotNull(result);
        assertTrue(result.length > 0);
        verify(metricsForPdfServices).recordGenerated("W4", true);
    }

    @Test
    void testGenerateW4Pdf_MarriedFilingJointly() throws IOException {
        // Given
        testUser.setFilingStatus(FilingStatus.MARRIED_FILLING_JOINTLY);
        testUser.setEstimatedItemizedDeductions(new BigDecimal("35000"));

        when(userRepository.findById(1)).thenReturn(Optional.of(testUser));
        when(companyRepository.findById(1)).thenReturn(Optional.of(testCompany));
        when(dependentRepository.findAllByUser_Id(1)).thenReturn(Collections.emptyList());
        when(metricsForPdfServices.startTimer()).thenReturn(mock(io.micrometer.core.instrument.Timer.Sample.class));

        // When
        byte[] result = fillFormW4.generateW4Pdf(1, 1);

        // Then
        assertNotNull(result);
        assertTrue(result.length > 0);
    }

    @Test
    void testGenerateW4Pdf_WithTwoJobsCheckBox() throws IOException {
        // Given
        testUser.setTwoJobsCheckBox(true);
        testUser.setMultipleJobsAdditionalWithholding(new BigDecimal("200"));

        when(userRepository.findById(1)).thenReturn(Optional.of(testUser));
        when(companyRepository.findById(1)).thenReturn(Optional.of(testCompany));
        when(dependentRepository.findAllByUser_Id(1)).thenReturn(Collections.emptyList());
        when(metricsForPdfServices.startTimer()).thenReturn(mock(io.micrometer.core.instrument.Timer.Sample.class));

        // When
        byte[] result = fillFormW4.generateW4Pdf(1, 1);

        // Then
        assertNotNull(result);
        assertTrue(result.length > 0);
    }

    @Test
    void testGenerateW4Pdf_S3UploadThrowsException() throws IOException {
        // Given
        when(userRepository.findById(1)).thenReturn(Optional.of(testUser));
        when(companyRepository.findById(1)).thenReturn(Optional.of(testCompany));
        when(dependentRepository.findAllByUser_Id(1)).thenReturn(Collections.emptyList());
        when(metricsForPdfServices.startTimer()).thenReturn(mock(io.micrometer.core.instrument.Timer.Sample.class));

        doThrow(new RuntimeException("S3 upload failed"))
                .when(amazonS3Service).uploadPdfToS3(any(byte[].class), anyString());

        // When & Then
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> fillFormW4.generateW4Pdf(1, 1)
        );

        assertEquals("S3 upload failed", exception.getMessage());
        verify(metricsForPdfServices).recordGenerated("W4", false);
        verify(metricsForPdfServices).recordError(eq("w4_failed"), anyString(), any(Exception.class));
    }

    @Test
    void testGenerateW4Pdf_DifferentPayFrequencies() throws IOException {
        // Given
        testUser.setPayFrequency(PayFrequency.WEEKLY);

        when(userRepository.findById(1)).thenReturn(Optional.of(testUser));
        when(companyRepository.findById(1)).thenReturn(Optional.of(testCompany));
        when(dependentRepository.findAllByUser_Id(1)).thenReturn(Collections.emptyList());
        when(metricsForPdfServices.startTimer()).thenReturn(mock(io.micrometer.core.instrument.Timer.Sample.class));

        // When
        byte[] result = fillFormW4.generateW4Pdf(1, 1);

        // Then
        assertNotNull(result);
        assertTrue(result.length > 0);

        // Проверяем для MONTHLY
        testUser.setPayFrequency(PayFrequency.MONTHLY);
        result = fillFormW4.generateW4Pdf(1, 1);
        assertNotNull(result);
        assertTrue(result.length > 0);
    }
}