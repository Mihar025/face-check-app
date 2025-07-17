package com.zikpak.facecheck.metrics;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class DatabaseMetricsAspect {

    private final ResourceMetricsService resourceMetricsService;

    @Around("@within(org.springframework.stereotype.Repository)")
    public Object measureRepositoryMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        resourceMetricsService.recordDatabaseOperationStart();

        try {
            return joinPoint.proceed();
        } finally {
            resourceMetricsService.recordDatabaseOperationEnd();
        }
    }
}