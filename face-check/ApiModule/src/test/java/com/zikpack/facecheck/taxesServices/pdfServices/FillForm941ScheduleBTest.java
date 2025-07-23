package com.zikpack.facecheck.taxesServices.pdfServices;

import com.zikpak.facecheck.entity.Company;
import com.zikpak.facecheck.entity.PaymentHistoryIrs;
import com.zikpak.facecheck.entity.User;
import com.zikpak.facecheck.metrics.MetricsForPdfServices;
import com.zikpak.facecheck.repository.CompanyRepository;
import com.zikpak.facecheck.repository.PaymentHistoryIrsRepository;
import com.zikpak.facecheck.repository.UserRepository;
import com.zikpak.facecheck.services.amazonS3Service.AmazonS3Service;
import com.zikpak.facecheck.taxesServices.pdfServices.FillForm941ScheduleB;
import io.micrometer.core.instrument.Timer;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FillForm941ScheduleBTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private CompanyRepository companyRepository;

    @Mock
    private PaymentHistoryIrsRepository paymentHistoryIrsRepository;

    @Mock
    private AmazonS3Service amazonS3Service;

    @Mock
    private MetricsForPdfServices metric;

    @Mock
    private Timer.Sample timerSample;

    @InjectMocks
    private FillForm941ScheduleB fillForm941ScheduleB;

    private User testUser;
    private Company testCompany;

    @BeforeEach
    void setUp() {
        // Настройка тестовых данных
        testUser = new User();
        testUser.setId(1);
        testUser.setFirstName("Test User");

        testCompany = new Company();
        testCompany.setId(1);
        testCompany.setCompanyName("Test Company");
        testCompany.setEmployerEIN("123456789");

        // Настройка метрик
        when(metric.startTimer()).thenReturn(timerSample);
    }

    @Test
    void testGenerateFilledPdf_Success() throws IOException {
        // Given
        Integer userId = 1;
        Integer companyId = 1;
        int year = 2024;
        int quarter = 1;

        // Создаем тестовые платежи
        PaymentHistoryIrs payment1 = new PaymentHistoryIrs();
        payment1.setPaymentDate(LocalDate.of(2024, 1, 15));
        payment1.setAmount(new BigDecimal("1000.50"));

        PaymentHistoryIrs payment2 = new PaymentHistoryIrs();
        payment2.setPaymentDate(LocalDate.of(2024, 2, 20));
        payment2.setAmount(new BigDecimal("2000.00"));

        Page<PaymentHistoryIrs> paymentsPage = new PageImpl<>(Arrays.asList(payment1, payment2));

        // Настройка моков
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(companyRepository.findById(companyId)).thenReturn(Optional.of(testCompany));
        when(paymentHistoryIrsRepository.findAllByCompany_IdAndYearAndQuarter(
                eq(companyId), eq(year), eq(quarter), any(Pageable.class)))
                .thenReturn(paymentsPage);

        // When
        byte[] result = fillForm941ScheduleB.generateFilledPdf(userId, companyId, year, quarter);

        // Then
        assertNotNull(result);
        assertTrue(result.length > 0);

        // Проверяем вызовы
        verify(metric).recordRequest("941SB");
        verify(metric).recordGenerated("941SB", true);
        verify(amazonS3Service).uploadPdfToS3(any(byte[].class), anyString());
        verify(metric).recordOperationTime(timerSample, "941_SB_success");
    }

    @Test
    void testGenerateFilledPdf_UserNotFound() {
        // Given
        Integer userId = 999;
        Integer companyId = 1;
        int year = 2024;
        int quarter = 1;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(EntityNotFoundException.class, () ->
                fillForm941ScheduleB.generateFilledPdf(userId, companyId, year, quarter)
        );

        verify(metric).recordRequest("941SB");
        verify(metric).recordGenerated("941SB", false);
        verify(metric).recordOperationTime(timerSample, "941_SB_failed");
    }

    @Test
    void testGenerateFilledPdf_CompanyNotFound() {
        // Given
        Integer userId = 1;
        Integer companyId = 999;
        int year = 2024;
        int quarter = 1;

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(companyRepository.findById(companyId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(EntityNotFoundException.class, () ->
                fillForm941ScheduleB.generateFilledPdf(userId, companyId, year, quarter)
        );

        verify(metric).recordRequest("941SB");
        verify(metric).recordGenerated("941SB", false);
    }

    @Test
    void testGenerateFilledPdf_InvalidEIN() {
        // Given
        Integer userId = 1;
        Integer companyId = 1;
        int year = 2024;
        int quarter = 1;

        testCompany.setEmployerEIN("12345"); // Недостаточно цифр

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(companyRepository.findById(companyId)).thenReturn(Optional.of(testCompany));

        // When & Then
        assertThrows(IllegalArgumentException.class, () ->
                fillForm941ScheduleB.generateFilledPdf(userId, companyId, year, quarter)
        );
    }

    @Test
    void testGenerateFilledPdf_InvalidQuarter() {
        // Given
        Integer userId = 1;
        Integer companyId = 1;
        int year = 2024;
        int quarter = 5; // Неверный квартал

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(companyRepository.findById(companyId)).thenReturn(Optional.of(testCompany));

        // When & Then
        assertThrows(IllegalArgumentException.class, () ->
                fillForm941ScheduleB.generateFilledPdf(userId, companyId, year, quarter)
        );
    }

    @Test
    void testGenerateFilledPdf_NoPayments() throws IOException {
        // Given
        Integer userId = 1;
        Integer companyId = 1;
        int year = 2024;
        int quarter = 1;

        Page<PaymentHistoryIrs> emptyPage = new PageImpl<>(Collections.emptyList());

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(companyRepository.findById(companyId)).thenReturn(Optional.of(testCompany));
        when(paymentHistoryIrsRepository.findAllByCompany_IdAndYearAndQuarter(
                eq(companyId), eq(year), eq(quarter), any(Pageable.class)))
                .thenReturn(emptyPage);

        // When
        byte[] result = fillForm941ScheduleB.generateFilledPdf(userId, companyId, year, quarter);

        // Then
        assertNotNull(result);
        assertTrue(result.length > 0);
        verify(amazonS3Service).uploadPdfToS3(any(byte[].class), anyString());
    }

    @Test
    void testGenerateFilledPdf_EINWithDashes() throws IOException {
        // Given
        Integer userId = 1;
        Integer companyId = 1;
        int year = 2024;
        int quarter = 1;

        testCompany.setEmployerEIN("12-3456789"); // EIN с дефисом

        Page<PaymentHistoryIrs> emptyPage = new PageImpl<>(Collections.emptyList());

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(companyRepository.findById(companyId)).thenReturn(Optional.of(testCompany));
        when(paymentHistoryIrsRepository.findAllByCompany_IdAndYearAndQuarter(
                eq(companyId), eq(year), eq(quarter), any(Pageable.class)))
                .thenReturn(emptyPage);

        // When
        byte[] result = fillForm941ScheduleB.generateFilledPdf(userId, companyId, year, quarter);

        // Then
        assertNotNull(result);
        assertTrue(result.length > 0);
    }
}