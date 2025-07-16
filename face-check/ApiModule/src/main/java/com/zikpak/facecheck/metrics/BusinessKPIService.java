package com.zikpak.facecheck.metrics;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.DoubleAdder;

@Service
@RequiredArgsConstructor
@Slf4j
public class BusinessKPIService {

    private final MeterRegistry meterRegistry;

    // Счетчики для разных типов операций
    private final Map<String, AtomicInteger> dailyOperations = new ConcurrentHashMap<>();
    private final Map<String, DoubleAdder> revenueByType = new ConcurrentHashMap<>();
    private final Map<String, AtomicInteger> errorsByCategory = new ConcurrentHashMap<>();

    // Для отслеживания уникальных пользователей
    private final Map<String, Map<String, Long>> uniqueUsersPerDay = new ConcurrentHashMap<>();

    /**
     * Записывает успешную бизнес-транзакцию
     */
    public void recordBusinessTransaction(String transactionType,
                                          String companyId,
                                          BigDecimal amount,
                                          Map<String, String> metadata) {

        // Основная метрика транзакций
        meterRegistry.counter("business.transactions",
                "type", transactionType,
                "company", companyId,
                "status", "success"
        ).increment();

        // Сумма транзакций
        if (amount != null) {
            meterRegistry.summary("business.transaction.amount",
                    "type", transactionType,
                    "company", companyId
            ).record(amount.doubleValue());

            // Добавляем в revenue tracker
            revenueByType.computeIfAbsent(transactionType, k -> new DoubleAdder())
                    .add(amount.doubleValue());
        }

        // Счетчик дневных операций
        String dateKey = LocalDate.now().toString();
        dailyOperations.computeIfAbsent(dateKey + "_" + transactionType, k -> new AtomicInteger())
                .incrementAndGet();

        // Логирование важных транзакций
        if (amount != null && amount.compareTo(new BigDecimal("10000")) > 0) {
            log.info("Large transaction recorded: {} - ${} for company {}",
                    transactionType, amount, companyId);
            meterRegistry.counter("business.large_transactions", "type", transactionType).increment();
        }
    }

    /**
     * Записывает активность пользователя (для DAU/MAU)
     */
    public void recordUserActivity(String userId, String activityType, String companyId) {
        String today = LocalDate.now().toString();

        // Записываем уникальных пользователей за день
        uniqueUsersPerDay.computeIfAbsent(today, k -> new ConcurrentHashMap<>())
                .put(userId, System.currentTimeMillis());

        // Счетчик активностей
        meterRegistry.counter("business.user.activities",
                "type", activityType,
                "company", companyId
        ).increment();

        // Gauge для активных пользователей (обновляется в scheduled методе)
    }

    /**
     * Записывает бизнес-ошибку с контекстом
     */
    public void recordBusinessError(String errorCategory,
                                    String operation,
                                    String errorCode,
                                    Map<String, String> context) {

        // Основной счетчик ошибок
        meterRegistry.counter("business.errors",
                "category", errorCategory,
                "operation", operation,
                "code", errorCode
        ).increment();

        // Трекинг по категориям
        errorsByCategory.computeIfAbsent(errorCategory, k -> new AtomicInteger())
                .incrementAndGet();

        // Критические ошибки
        if (isCriticalError(errorCategory, errorCode)) {
            meterRegistry.counter("business.critical_errors",
                    "category", errorCategory,
                    "operation", operation
            ).increment();

            log.error("CRITICAL BUSINESS ERROR: {} in {} - Code: {}, Context: {}",
                    errorCategory, operation, errorCode, context);
        }
    }

    /**
     * Записывает метрики производительности операций
     */
    public void recordOperationPerformance(String operationType,
                                           int itemsProcessed,
                                           long durationMs,
                                           boolean success) {

        // Throughput метрика
        if (itemsProcessed > 0 && durationMs > 0) {
            double throughput = (itemsProcessed * 1000.0) / durationMs; // items per second

            meterRegistry.summary("business.operation.throughput",
                    "type", operationType,
                    "status", success ? "success" : "failed"
            ).record(throughput);
        }

        // Batch size метрика
        meterRegistry.summary("business.operation.batch_size",
                "type", operationType
        ).record(itemsProcessed);

        // Efficiency метрика (успешность больших операций)
        if (itemsProcessed > 100) {
            meterRegistry.counter("business.bulk_operations",
                    "type", operationType,
                    "status", success ? "success" : "failed"
            ).increment();
        }
    }

    /**
     * Записывает метрики compliance и SLA
     */
    public void recordComplianceMetric(String complianceType, boolean compliant, String details) {
        meterRegistry.counter("business.compliance",
                "type", complianceType,
                "status", compliant ? "compliant" : "violation"
        ).increment();

        if (!compliant) {
            log.warn("Compliance violation detected: {} - {}", complianceType, details);
            meterRegistry.counter("business.compliance.violations", "type", complianceType).increment();
        }
    }

    /**
     * Периодический расчет и публикация агрегированных метрик (каждую минуту)
     */
    @Scheduled(fixedDelay = 60000)
    public void calculateAndPublishKPIs() {
        try {
            // Daily Active Users (DAU)
            String today = LocalDate.now().toString();
            int dau = uniqueUsersPerDay.getOrDefault(today, Map.of()).size();

            meterRegistry.gauge("business.kpi.dau", Tags.empty(), dau);

            // Revenue metrics
            revenueByType.forEach((type, adder) -> {
                meterRegistry.gauge("business.kpi.daily_revenue",
                        Tags.of("type", type),
                        adder.doubleValue()
                );
            });

            // Error rate
            int totalErrors = errorsByCategory.values().stream()
                    .mapToInt(AtomicInteger::get)
                    .sum();

            meterRegistry.gauge("business.kpi.error_count", Tags.empty(), totalErrors);

            // Operations count
            int todayOperations = dailyOperations.entrySet().stream()
                    .filter(e -> e.getKey().startsWith(today))
                    .mapToInt(e -> e.getValue().get())
                    .sum();

            meterRegistry.gauge("business.kpi.daily_operations", Tags.empty(), todayOperations);

            log.debug("KPIs updated - DAU: {}, Operations: {}, Errors: {}",
                    dau, todayOperations, totalErrors);

        } catch (Exception e) {
            log.error("Error calculating KPIs", e);
        }
    }

    /**
     * Сброс дневных метрик (запускается в полночь)
     */
    @Scheduled(cron = "0 0 0 * * *")
    public void resetDailyMetrics() {
        log.info("Resetting daily metrics");

        // Архивируем старые данные (older than 7 days)
        LocalDate weekAgo = LocalDate.now().minusDays(7);

        uniqueUsersPerDay.entrySet().removeIf(entry -> {
            LocalDate date = LocalDate.parse(entry.getKey());
            return date.isBefore(weekAgo);
        });

        dailyOperations.entrySet().removeIf(entry -> {
            String dateStr = entry.getKey().split("_")[0];
            LocalDate date = LocalDate.parse(dateStr);
            return date.isBefore(weekAgo);
        });

        // Сбрасываем revenue для нового дня
        revenueByType.clear();
        errorsByCategory.clear();
    }

    private boolean isCriticalError(String category, String code) {
        return category.contains("payment") ||
                category.contains("security") ||
                code.contains("CRITICAL") ||
                code.contains("FATAL");
    }
}