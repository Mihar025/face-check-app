package com.zikpak.facecheck.metrics;

import com.zaxxer.hikari.HikariPoolMXBean;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.ThreadMXBean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

@Component
@EnableScheduling
@RequiredArgsConstructor
@Slf4j
public class ResourceMetricsService {

    private final MeterRegistry meterRegistry;

    @Autowired(required = false)
    private DataSource dataSource;

    @Autowired(required = false)
    private JdbcTemplate jdbcTemplate;

    private final AtomicInteger activeDbConnections = new AtomicInteger(0);
    private final AtomicLong lastGcTime = new AtomicLong(0);
    private final AtomicInteger threadDeadlocks = new AtomicInteger(0);

    @PostConstruct
    public void init() {
        log.info("Initializing Resource Metrics Service");

        // Регистрируем gauge метрики
        registerMemoryMetrics();
        registerThreadMetrics();
        registerDatabaseMetrics();
        registerCustomBusinessMetrics();
    }

    /**
     * Метрики памяти
     */
    private void registerMemoryMetrics() {
        MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();

        // Heap memory usage percentage
        Gauge.builder("jvm.memory.heap.percentage", () -> {
                    long used = memoryBean.getHeapMemoryUsage().getUsed();
                    long max = memoryBean.getHeapMemoryUsage().getMax();
                    return (max > 0) ? (double) used / max * 100 : 0;
                })
                .description("Heap memory usage percentage")
                .baseUnit("percent")
                .register(meterRegistry);

        // Non-heap memory percentage
        Gauge.builder("jvm.memory.nonheap.percentage", () -> {
                    long used = memoryBean.getNonHeapMemoryUsage().getUsed();
                    long max = memoryBean.getNonHeapMemoryUsage().getMax();
                    return (max > 0 && used > 0) ? (double) used / max * 100 : 0;
                })
                .description("Non-heap memory usage percentage")
                .baseUnit("percent")
                .register(meterRegistry);
    }

    /**
     * Метрики потоков
     */
    private void registerThreadMetrics() {
        ThreadMXBean threadBean = ManagementFactory.getThreadMXBean();

        // Thread deadlock detection
        Gauge.builder("jvm.threads.deadlocked", () -> {
                    long[] deadlockedThreadIds = threadBean.findDeadlockedThreads();
                    return deadlockedThreadIds != null ? deadlockedThreadIds.length : 0;
                })
                .description("Number of deadlocked threads")
                .register(meterRegistry);

        // Thread CPU usage
        Gauge.builder("jvm.threads.cpu.percentage", () -> {
                    long totalCpuTime = 0;
                    long[] threadIds = threadBean.getAllThreadIds();
                    for (long id : threadIds) {
                        totalCpuTime += threadBean.getThreadCpuTime(id);
                    }
                    return totalCpuTime / 1_000_000_000.0; // Convert to seconds
                })
                .description("Total CPU time used by all threads")
                .baseUnit("seconds")
                .register(meterRegistry);
    }

    /**
     * Метрики базы данных
     */
    private void registerDatabaseMetrics() {
        if (dataSource == null) {
            log.warn("DataSource not available, skipping database metrics");
            return;
        }



        // Database health check
        Gauge.builder("database.health", () -> {
                    try {
                        if (jdbcTemplate != null) {
                            jdbcTemplate.queryForObject("SELECT 1", Integer.class);
                            return 1.0;
                        }
                        return 0.0;
                    } catch (Exception e) {
                        log.error("Database health check failed", e);
                        return 0.0;
                    }
                })
                .description("Database health status (1=healthy, 0=unhealthy)")
                .register(meterRegistry);
    }

    /**
     * Кастомные бизнес-метрики
     */
    private void registerCustomBusinessMetrics() {
        // Счетчик активных пользователей (пример)
        Gauge.builder("business.users.active", () -> {
                    // Здесь можно добавить реальную логику подсчета активных пользователей
                    return Math.random() * 100; // Заглушка
                })
                .description("Number of active users in the system")
                .register(meterRegistry);

        // Очередь необработанных задач
        Gauge.builder("business.tasks.pending", () -> {
                    // Здесь можно добавить реальную логику подсчета задач
                    return Math.random() * 50; // Заглушка
                })
                .description("Number of pending tasks")
                .register(meterRegistry);
    }

    /**
     * Периодическая проверка ресурсов (каждые 30 секунд)
     */
    @Scheduled(fixedDelay = 30000)
    public void checkResourceHealth() {
        checkMemoryPressure();
        checkThreadHealth();
        checkDatabaseConnections();
    }

    private void checkMemoryPressure() {
        MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
        long heapUsed = memoryBean.getHeapMemoryUsage().getUsed();
        long heapMax = memoryBean.getHeapMemoryUsage().getMax();

        double heapPercentage = (heapMax > 0) ? (double) heapUsed / heapMax * 100 : 0;

        if (heapPercentage > 90) {
            meterRegistry.counter("resources.alerts", "type", "high_memory").increment();
            log.error("CRITICAL: Heap memory usage is above 90%: {}%", String.format("%.2f", heapPercentage));
        } else if (heapPercentage > 80) {
            meterRegistry.counter("resources.warnings", "type", "memory").increment();
            log.warn("WARNING: Heap memory usage is above 80%: {}%", String.format("%.2f", heapPercentage));
        }
    }

    private void checkThreadHealth() {
        ThreadMXBean threadBean = ManagementFactory.getThreadMXBean();

        // Check for deadlocks
        long[] deadlockedThreadIds = threadBean.findDeadlockedThreads();
        if (deadlockedThreadIds != null && deadlockedThreadIds.length > 0) {
            meterRegistry.counter("resources.alerts", "type", "thread_deadlock").increment();
            log.error("CRITICAL: {} thread deadlocks detected!", deadlockedThreadIds.length);
        }

        // Check thread count
        int threadCount = threadBean.getThreadCount();
        if (threadCount > 1000) {
            meterRegistry.counter("resources.warnings", "type", "high_thread_count").increment();
            log.warn("WARNING: High thread count detected: {}", threadCount);
        }
    }

    private void checkDatabaseConnections() {
        if (dataSource == null) return;

        try {
            // Для HikariCP
            if (dataSource instanceof com.zaxxer.hikari.HikariDataSource) {
                com.zaxxer.hikari.HikariDataSource hikariDataSource =
                        (com.zaxxer.hikari.HikariDataSource) dataSource;

                int active = hikariDataSource.getHikariPoolMXBean().getActiveConnections();
                int total = hikariDataSource.getHikariPoolMXBean().getTotalConnections();

                double usagePercent = (total > 0) ? (double) active / total * 100 : 0;

                if (usagePercent > 90) {
                    meterRegistry.counter("resources.alerts", "type", "db_connections").increment();
                    log.error("CRITICAL: Database connection pool usage above 90%: {}%",
                            String.format("%.2f", usagePercent));
                } else if (usagePercent > 80) {
                    meterRegistry.counter("resources.warnings", "type", "db_connections").increment();
                    log.warn("WARNING: Database connection pool usage above 80%: {}%",
                            String.format("%.2f", usagePercent));
                }
            }
        } catch (Exception e) {
            log.error("Error checking database connections", e);
        }
    }

    /**
     * Метод для отслеживания начала DB операции
     */
    public void recordDatabaseOperationStart() {
        activeDbConnections.incrementAndGet();
    }

    /**
     * Метод для отслеживания окончания DB операции
     */
    public void recordDatabaseOperationEnd() {
        activeDbConnections.decrementAndGet();
    }
}