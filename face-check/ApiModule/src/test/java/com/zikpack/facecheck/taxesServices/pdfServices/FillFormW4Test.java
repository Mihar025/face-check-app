package com.zikpack.facecheck.taxesServices.pdfServices;


import com.zikpak.facecheck.entity.User;
import com.zikpak.facecheck.entity.Company;
import com.zikpak.facecheck.metrics.MetricsForPdfServices;
import com.zikpak.facecheck.repository.CompanyRepository;
import com.zikpak.facecheck.repository.DependentRepository;
import com.zikpak.facecheck.repository.UserRepository;
import com.zikpak.facecheck.services.amazonS3Service.AmazonS3Service;
import com.zikpak.facecheck.taxesServices.pdfServices.FillFormW4;
import io.micrometer.core.instrument.Timer;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

import static com.zikpak.facecheck.entity.W4.FilingStatus.SINGLE;
import static com.zikpak.facecheck.entity.W4.PayFrequency.WEEKLY;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FillFormW4Test {

    @Mock
    UserRepository userRepository;
    @Mock
    DependentRepository dependentRepository;
    @Mock
    CompanyRepository companyRepository;
    @Mock
    AmazonS3Service amazonS3Service;
    @Mock
    MetricsForPdfServices metricsForPdfServices;
    @Mock
    Timer.Sample timerSample;

    @InjectMocks
    FillFormW4 fillFormW4;

    @BeforeEach
    void setUp() {

    }

    @Test
    void testGenerateW4Pdf_Success() throws IOException {

        User testUser = new User();
        testUser.setId(1);
        testUser.setFirstName("John");
        testUser.setMiddleInitial(null);
        testUser.setLastName("Doe");
        testUser.setHomeAddress("123 Main St");
        testUser.setCity("Springfield");
        testUser.setState("IL");
        testUser.setZipcode("62704");
        testUser.setSSN_WORKER("123-45-6789");
        testUser.setFilingStatus(SINGLE);
        testUser.setTwoJobsCheckBox(false);
        testUser.setMultipleJobsAdditionalWithholding(BigDecimal.ZERO);
        testUser.setExtraWithHoldings(BigDecimal.ZERO);
        testUser.setMultipleJobsWorksheetLine2a(BigDecimal.ZERO);
        testUser.setMultipleJobsWorksheetLine2b(BigDecimal.ZERO);
        testUser.setPayFrequency(WEEKLY);
        testUser.setEstimatedItemizedDeductions(BigDecimal.ZERO);
        testUser.setAdjustmentsSchedule1(BigDecimal.ZERO);
        testUser.setCreatedDate(LocalDateTime.of(2025, 1, 1, 1, 1));

        when(userRepository.findById(1)).thenReturn(Optional.of(testUser));
        when(dependentRepository.findAllByUser_Id(1)).thenReturn(Collections.emptyList());

        // 2) Тестовая компания
        Company testCompany = new Company();
        testCompany.setId(1);
        testCompany.setCompanyName("TestCo");
        testCompany.setCompanyAddress("456 Elm St");
        testCompany.setEmployerEIN("12-3456789");
        when(companyRepository.findById(1)).thenReturn(Optional.of(testCompany));

        // 3) Метрики
        when(metricsForPdfServices.startTimer()).thenReturn(timerSample);
        doNothing().when(metricsForPdfServices).recordRequest("W4");
        doNothing().when(metricsForPdfServices).recordGenerated(anyString(), anyBoolean());
        doNothing().when(metricsForPdfServices).recordS3UploadTime(anyString(), anyBoolean(), anyLong());
        doNothing().when(metricsForPdfServices).recordOperationTime(any(), anyString());

        // 4) S3
        when(amazonS3Service.uploadPdfToS3(any(byte[].class), anyString()))
                .thenReturn("http://fake-s3-url/test.pdf");

        // --- Act ---
        byte[] pdfBytes = fillFormW4.generateW4Pdf(testUser.getId(), testCompany.getId());

        // --- Assert ---
    //    assertNotNull(pdfBytes, "PDF не должен быть null");
        assertTrue(pdfBytes.length > 0, "PDF должен содержать данные");
        verify(metricsForPdfServices).recordRequest("W4");
        verify(amazonS3Service).uploadPdfToS3(any(byte[].class), contains("TestCo"));
    }

    @Test
    void testGenerateW4Pdf_UserNotFound() {
        // --- Arrange ---
        when(userRepository.findById(2)).thenReturn(Optional.empty());
        when(metricsForPdfServices.startTimer()).thenReturn(timerSample);
        doNothing().when(metricsForPdfServices).recordRequest("W4");
        doNothing().when(metricsForPdfServices).recordGenerated(anyString(), anyBoolean());
        doNothing().when(metricsForPdfServices).recordOperationTime(any(), anyString());
        doNothing().when(metricsForPdfServices).recordError(anyString(), anyString(), any());

        // --- Act & Assert ---
        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                () -> fillFormW4.generateW4Pdf(2, 1),
                "Ожидаем EntityNotFoundException, если пользователь не найден"
        );
        assertTrue(ex.getMessage().contains("User with id 2"), "Сообщение об ошибке должно содержать id пользователя");
        verify(metricsForPdfServices).recordRequest("W4");
        verify(metricsForPdfServices).recordGenerated("W4", false);
    }
}
