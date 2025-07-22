package com.zikpack.facecheck.taxesServices.calculators;


import com.zikpak.facecheck.entity.User;
import com.zikpak.facecheck.entity.W4.FilingStatus;
import com.zikpak.facecheck.entity.W4.PayFrequency;
import com.zikpak.facecheck.entity.W4.TaxRates;
import com.zikpak.facecheck.metrics.MetricsFinanceCalculator;
import com.zikpak.facecheck.repository.UserRepository;
import com.zikpak.facecheck.requestsResponses.finance.PayStubResponse;
import com.zikpak.facecheck.taxesServices.calculators.FinanceCalculator;
import io.micrometer.core.instrument.Timer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FinanceCalculatorTest {

    @InjectMocks
    private FinanceCalculator financeCalculator;

    @Mock
    private UserRepository userRepository;

    @Mock
    private MetricsFinanceCalculator metricsFinanceCalculator;

    @Mock
    private Timer.Sample timerSample;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1);
        testUser.setEmail("test@example.com");
        testUser.setFilingStatus(FilingStatus.SINGLE);
        testUser.setPayFrequency(PayFrequency.BIWEEKLY);
        testUser.setExemptFromWithholding(false);
        testUser.setMultipleJobsOrSpouseWorks(false);
        testUser.setEnrolledInHealthPlan(false);

        // Мокаем метрики
        when(metricsFinanceCalculator.startTimer()).thenReturn(timerSample);
    }

    @Nested
    @DisplayName("Social Security Calculation Tests")
    class SocialSecurityTests {

        @Test
        @DisplayName("Нормальный расчет Social Security")
        void calculateSocialSecurity_Normal() {
            // Arrange
            BigDecimal grossPay = new BigDecimal("2000.00");
            BigDecimal ytdWages = new BigDecimal("10000.00");
            BigDecimal expectedSS = grossPay.multiply(TaxRates.SOCIAL_SECURITY_RATE);

            // Act
            BigDecimal result = financeCalculator.calculateSocialSecurity(testUser, grossPay, ytdWages);

            // Assert
            assertThat(result).isEqualTo(TaxRates.round(expectedSS));
            verify(metricsFinanceCalculator).recordCalculationCall("social_security");
        }

        @Test
        @DisplayName("Social Security с превышением лимита")
        void calculateSocialSecurity_ExceedsLimit() {
            // Arrange
            BigDecimal grossPay = new BigDecimal("5000.00");
            BigDecimal ytdWages = TaxRates.SOCIAL_SECURITY_WAGE_LIMIT; // Уже достиг лимита

            // Act
            BigDecimal result = financeCalculator.calculateSocialSecurity(testUser, grossPay, ytdWages);

            // Assert
            assertThat(result).isEqualTo(BigDecimal.ZERO);
        }

        @Test
        @DisplayName("Social Security с частичным лимитом")
        void calculateSocialSecurity_PartialLimit() {
            // Arrange
            BigDecimal grossPay = new BigDecimal("5000.00");
            BigDecimal ytdWages = TaxRates.SOCIAL_SECURITY_WAGE_LIMIT.subtract(new BigDecimal("1000.00"));
            BigDecimal expectedTaxableWages = new BigDecimal("1000.00");
            BigDecimal expectedSS = expectedTaxableWages.multiply(TaxRates.SOCIAL_SECURITY_RATE);

            // Act
            BigDecimal result = financeCalculator.calculateSocialSecurity(testUser, grossPay, ytdWages);

            // Assert
            assertThat(result).isEqualTo(TaxRates.round(expectedSS));
        }

        @Test
        @DisplayName("Social Security с null gross pay")
        void calculateSocialSecurity_NullGrossPay() {
            // Act
            BigDecimal result = financeCalculator.calculateSocialSecurity(testUser, null, BigDecimal.ZERO);

            // Assert
            assertThat(result).isEqualTo(BigDecimal.ZERO);
        }
    }

    @Nested
    @DisplayName("Medicare Calculation Tests")
    class MedicareTests {

        @Test
        @DisplayName("Обычный Medicare без дополнительного налога")
        void calculateMedicare_Normal() {
            // Arrange
            BigDecimal grossPay = new BigDecimal("2000.00");
            BigDecimal ytdWages = new BigDecimal("50000.00");
            BigDecimal expectedMedicare = grossPay.multiply(TaxRates.MEDICARE_RATE);

            // Act
            BigDecimal result = financeCalculator.calculateMedicare(testUser, grossPay, ytdWages);

            // Assert
            assertThat(result).isEqualTo(TaxRates.round(expectedMedicare));
        }

        @Test
        @DisplayName("Medicare с дополнительным налогом")
        void calculateMedicare_WithAdditional() {
            // Arrange
            BigDecimal grossPay = new BigDecimal("10000.00");
            BigDecimal ytdWages = new BigDecimal("195000.00"); // Близко к порогу $200,000

            // Act
            BigDecimal result = financeCalculator.calculateMedicare(testUser, grossPay, ytdWages);

            // Assert
            assertThat(result).isGreaterThan(grossPay.multiply(TaxRates.MEDICARE_RATE));
        }

        @Test
        @DisplayName("Medicare с null YTD")
        void calculateMedicare_NullYtd() {
            // Arrange
            BigDecimal grossPay = new BigDecimal("2000.00");

            // Act
            BigDecimal result = financeCalculator.calculateMedicare(testUser, grossPay, null);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result).isPositive();
        }
    }

    @Nested
    @DisplayName("NY Disability Tests")
    class NYDisabilityTests {

        @ParameterizedTest
        @EnumSource(PayFrequency.class)
        @DisplayName("NY Disability для разных частот выплат")
        void calculateNYDisability_DifferentPayFrequencies(PayFrequency payFrequency) {
            // Arrange
            testUser.setPayFrequency(payFrequency);

            // Act
            BigDecimal result = financeCalculator.calculateNYDisability(testUser);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result).isPositive();

            // Проверяем, что результат логичен для частоты выплат
            switch (payFrequency) {
                case WEEKLY -> assertThat(result).isEqualTo(TaxRates.NY_DISABILITY_WEEKLY_MAX);
                case BIWEEKLY -> assertThat(result).isEqualTo(
                        TaxRates.round(TaxRates.NY_DISABILITY_WEEKLY_MAX.multiply(new BigDecimal("2")))
                );
                case MONTHLY -> assertThat(result).isGreaterThan(
                        TaxRates.NY_DISABILITY_WEEKLY_MAX.multiply(new BigDecimal("4"))
                );
            }
        }
    }

    @Nested
    @DisplayName("Federal Tax Calculation Tests")
    class FederalTaxTests {

        @Test
        @DisplayName("Federal Tax для холостого")
        void calculateFederalTax_Single() {
            // Arrange
            testUser.setFilingStatus(FilingStatus.SINGLE);
            testUser.setPayFrequency(PayFrequency.BIWEEKLY);
            BigDecimal grossPay = new BigDecimal("2000.00");

            // Act
            BigDecimal result = financeCalculator.calculateFederalTax(testUser, grossPay);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result).isGreaterThanOrEqualTo(BigDecimal.ZERO);
        }

        @Test
        @DisplayName("Federal Tax с освобождением от удержания")
        void calculateFederalTax_Exempt() {
            // Arrange
            testUser.setExemptFromWithholding(true);
            BigDecimal grossPay = new BigDecimal("2000.00");

            // Act
            BigDecimal result = financeCalculator.calculateFederalTax(testUser, grossPay);

            // Assert
            assertThat(result).isEqualTo(BigDecimal.ZERO);
        }

        @Test
        @DisplayName("Federal Tax с дополнительными удержаниями")
        void calculateFederalTax_WithExtraWithholdings() {
            // Arrange
            testUser.setExtraWithHoldings(new BigDecimal("100.00"));
            BigDecimal grossPay = new BigDecimal("2000.00");

            // Act
            BigDecimal result = financeCalculator.calculateFederalTax(testUser, grossPay);

            // Assert
            assertThat(result).isGreaterThan(new BigDecimal("100.00"));
        }

        @ParameterizedTest
        @EnumSource(FilingStatus.class)
        @DisplayName("Federal Tax для всех статусов подачи")
        void calculateFederalTax_AllFilingStatuses(FilingStatus filingStatus) {
            // Arrange
            testUser.setFilingStatus(filingStatus);
            BigDecimal grossPay = new BigDecimal("3000.00");

            // Act
            BigDecimal result = financeCalculator.calculateFederalTax(testUser, grossPay);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result).isGreaterThanOrEqualTo(BigDecimal.ZERO);
        }
    }

    @Nested
    @DisplayName("NY State Tax Tests")
    class NYStateTaxTests {

        @Test
        @DisplayName("NY State Tax нормальный расчет")
        void calculateNYStateTax_Normal() {
            // Arrange
            testUser.setFilingStatus(FilingStatus.SINGLE);
            BigDecimal grossPay = new BigDecimal("2500.00");

            // Act
            BigDecimal result = financeCalculator.calculateNYStateTax(testUser, grossPay);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result).isGreaterThanOrEqualTo(BigDecimal.ZERO);
        }

        @Test
        @DisplayName("NY State Tax с низким доходом")
        void calculateNYStateTax_LowIncome() {
            // Arrange
            testUser.setFilingStatus(FilingStatus.SINGLE);
            BigDecimal grossPay = new BigDecimal("500.00");

            // Act
            BigDecimal result = financeCalculator.calculateNYStateTax(testUser, grossPay);

            // Assert
            assertThat(result).isGreaterThanOrEqualTo(BigDecimal.ZERO);
        }
    }

    @Nested
    @DisplayName("Gross Pay Calculation Tests")
    class GrossPayTests {

        @Test
        @DisplayName("Gross Pay только обычные часы")
        void calculateGrossPay_RegularHoursOnly() {
            // Arrange
            BigDecimal hourlyRate = new BigDecimal("25.00");
            BigDecimal overtimeRate = new BigDecimal("37.50");
            double regularHours = 40.0;
            double overtimeHours = 0.0;

            // Act
            BigDecimal result = financeCalculator.calculateGrossPay(
                    hourlyRate, overtimeRate, regularHours, overtimeHours
            );

            // Assert
            BigDecimal expected = hourlyRate.multiply(new BigDecimal("40.00"));
            assertThat(result).isEqualTo(TaxRates.round(expected));
        }

        @Test
        @DisplayName("Gross Pay с переработкой")
        void calculateGrossPay_WithOvertime() {
            // Arrange
            BigDecimal hourlyRate = new BigDecimal("20.00");
            BigDecimal overtimeRate = new BigDecimal("30.00");
            double regularHours = 40.0;
            double overtimeHours = 10.0;

            // Act
            BigDecimal result = financeCalculator.calculateGrossPay(
                    hourlyRate, overtimeRate, regularHours, overtimeHours
            );

            // Assert
            BigDecimal expectedRegular = hourlyRate.multiply(new BigDecimal("40.00"));
            BigDecimal expectedOvertime = overtimeRate.multiply(new BigDecimal("10.00"));
            BigDecimal expected = expectedRegular.add(expectedOvertime);

            assertThat(result).isEqualTo(TaxRates.round(expected));
        }

        @ParameterizedTest
        @ValueSource(doubles = {0.0, 5.5, 40.0, 45.5})
        @DisplayName("Gross Pay с различными часами")
        void calculateGrossPay_VariousHours(double regularHours) {
            // Arrange
            BigDecimal hourlyRate = new BigDecimal("15.00");
            BigDecimal overtimeRate = new BigDecimal("22.50");
            double overtimeHours = 5.0;

            // Act
            BigDecimal result = financeCalculator.calculateGrossPay(
                    hourlyRate, overtimeRate, regularHours, overtimeHours
            );

            // Assert
            assertThat(result).isGreaterThanOrEqualTo(BigDecimal.ZERO);
        }
    }

    @Nested
    @DisplayName("Complete PayStub Calculation Tests")
    class PayStubTests {

        @Test
        @DisplayName("Полный расчет PayStub")
        void calculateNetPayWithSeparateHours_Complete() {
            // Arrange
            BigDecimal hourlyRate = new BigDecimal("25.00");
            BigDecimal overtimeRate = new BigDecimal("37.50");
            double regularHours = 40.0;
            double overtimeHours = 5.0;
            BigDecimal ytdPFL = BigDecimal.ZERO;
            BigDecimal ytdSS = new BigDecimal("5000.00");
            BigDecimal ytdMedicare = new BigDecimal("5000.00");

            // Act
            PayStubResponse result = financeCalculator.calculateNetPayWithSeparateHours(
                    testUser, hourlyRate, overtimeRate, regularHours, overtimeHours,
                    ytdPFL, ytdSS, ytdMedicare
            );

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getGrossPay()).isPositive();
            assertThat(result.getNetPay()).isPositive();
            assertThat(result.getTotalDeductions()).isPositive();
            assertThat(result.getSocialSecurity()).isGreaterThanOrEqualTo(BigDecimal.ZERO);
            assertThat(result.getMedicare()).isPositive();
            assertThat(result.getFederalTax()).isGreaterThanOrEqualTo(BigDecimal.ZERO);
            assertThat(result.getStateTax()).isGreaterThanOrEqualTo(BigDecimal.ZERO);

            // Проверяем математику
            BigDecimal calculatedNet = result.getGrossPay().subtract(result.getTotalDeductions());
            assertThat(result.getNetPay()).isEqualTo(calculatedNet);
        }

        @Test
        @DisplayName("PayStub с медицинским планом")
        void calculateNetPayWithSeparateHours_WithHealthPlan() {
            // Arrange
            testUser.setEnrolledInHealthPlan(true);
            testUser.setMonthlyHealthPremium(new BigDecimal("300.00"));
            testUser.setCoverageStartDate(LocalDate.now()); // ✅ Добавляем дату начала покрытия

            BigDecimal hourlyRate = new BigDecimal("30.00");
            BigDecimal overtimeRate = new BigDecimal("45.00");

            // Act
            PayStubResponse result = financeCalculator.calculateNetPayWithSeparateHours(
                    testUser, hourlyRate, overtimeRate, 40.0, 0.0,
                    BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO
            );

            // Assert
            assertThat(result.getHealthDeduction()).isPositive();
            assertThat(result.getNetPay()).isLessThan(result.getGrossPay());
        }

        @Test
        @DisplayName("PayStub с освобождением от федерального налога")
        void calculateNetPayWithSeparateHours_FederalExempt() {
            // Arrange
            testUser.setExemptFromWithholding(true);

            // Act
            PayStubResponse result = financeCalculator.calculateNetPayWithSeparateHours(
                    testUser, new BigDecimal("20.00"), new BigDecimal("30.00"),
                    40.0, 0.0, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO
            );

            // Assert
            assertThat(result.getFederalTax()).isEqualByComparingTo(BigDecimal.ZERO);
        }
    }

    @Nested
    @DisplayName("Edge Cases and Error Handling")
    class EdgeCasesTests {

        @Test
        @DisplayName("Все расчеты с null gross pay")
        void calculations_WithNullGrossPay() {
            // Act & Assert
            assertThat(financeCalculator.calculateSocialSecurity(testUser, null, BigDecimal.ZERO))
                    .isEqualTo(BigDecimal.ZERO);

            assertThat(financeCalculator.calculateMedicare(testUser, null, BigDecimal.ZERO))
                    .isEqualTo(BigDecimal.ZERO);

            assertThat(financeCalculator.calculateNYPaidFamilyLeave(null, BigDecimal.ZERO))
                    .isEqualTo(BigDecimal.ZERO);

            assertThat(financeCalculator.calculateFederalTax(testUser, null))
                    .isEqualTo(BigDecimal.ZERO);

            assertThat(financeCalculator.calculateNYStateTax(testUser, null))
                    .isEqualTo(BigDecimal.ZERO);

            assertThat(financeCalculator.calculateNYCLocalTax(testUser, null))
                    .isEqualTo(BigDecimal.ZERO);
        }

        @Test
        @DisplayName("Очень высокий доход")
        void calculations_HighIncome() {
            // Arrange
            BigDecimal highGrossPay = new BigDecimal("50000.00"); // Per period

            // Act
            BigDecimal federalTax = financeCalculator.calculateFederalTax(testUser, highGrossPay);
            BigDecimal stateTax = financeCalculator.calculateNYStateTax(testUser, highGrossPay);

            // Assert
            assertThat(federalTax).isPositive();
            assertThat(stateTax).isPositive();
        }

        @Test
        @DisplayName("Округление результатов")
        void calculations_Rounding() {
            // Arrange
            BigDecimal grossPay = new BigDecimal("1234.567"); // Много знаков после запятой

            // Act
            BigDecimal ss = financeCalculator.calculateSocialSecurity(testUser, grossPay, BigDecimal.ZERO);
            BigDecimal medicare = financeCalculator.calculateMedicare(testUser, grossPay, BigDecimal.ZERO);

            // Assert - проверяем, что результаты округлены до 2 знаков
            assertThat(ss.scale()).isLessThanOrEqualTo(2);
            assertThat(medicare.scale()).isLessThanOrEqualTo(2);
        }
    }

    @Test
    @DisplayName("Проверка вызовов метрик")
    void verifyMetricsCalls() {
        // Arrange
        BigDecimal grossPay = new BigDecimal("2000.00");

        // Act
        financeCalculator.calculateSocialSecurity(testUser, grossPay, BigDecimal.ZERO);

        // Assert
        verify(metricsFinanceCalculator).startTimer();
        verify(metricsFinanceCalculator).recordCalculationCall("social_security");
        verify(metricsFinanceCalculator).recordTaxAmount(eq("social_security"), anyDouble());
        verify(metricsFinanceCalculator).recordOperationTime(timerSample, "calculate_ss_success");
    }
}