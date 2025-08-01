package com.zikpak.facecheck.security.quartzConfigur;

import lombok.AllArgsConstructor;
import org.quartz.spi.JobFactory;
import org.springframework.context.annotation.Configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.quartz.QuartzProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import javax.sql.DataSource;
import java.util.Properties;


@Configuration
@AllArgsConstructor
public class QuartzConfig {


    private final DataSource dataSource;
    private final ApplicationContext applicationContext;

    @Bean
    public SchedulerFactoryBean schedulerFactoryBean() {
        // Создаем JobFactory правильно
        SchedulerJobFactory jobFactory = new SchedulerJobFactory();
        jobFactory.setApplicationContext(applicationContext);

        Properties properties = new Properties();
        properties.setProperty("org.quartz.scheduler.instanceId", "AUTO");
        properties.setProperty("org.quartz.scheduler.instanceName", "TaxScheduler");

        // Используем RAM store вместо JDBC (так как мы не создаем Quartz таблицы)
        properties.setProperty("org.quartz.jobStore.class", "org.quartz.simpl.RAMJobStore");

        // Thread pool настройки
        properties.setProperty("org.quartz.threadPool.class", "org.quartz.simpl.SimpleThreadPool");
        properties.setProperty("org.quartz.threadPool.threadCount", "10");
        properties.setProperty("org.quartz.threadPool.threadPriority", "5");

        // Misfire настройки
        properties.setProperty("org.quartz.jobStore.misfireThreshold", "60000"); // 1 минута

        // Создаем SchedulerFactoryBean правильно
        SchedulerFactoryBean factory = new SchedulerFactoryBean();
        factory.setJobFactory(jobFactory); // Теперь передаем правильный jobFactory
        factory.setQuartzProperties(properties);
        factory.setWaitForJobsToCompleteOnShutdown(true);
        factory.setOverwriteExistingJobs(true);
        factory.setAutoStartup(true);

        return factory;
    }

}
