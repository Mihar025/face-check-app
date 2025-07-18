package com.zikpak.facecheck.metrics;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.apache.bcel.classfile.Module;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
@Component
@Order(4)
@RequiredArgsConstructor
@Slf4j
public class MetricsFilter extends OncePerRequestFilter {

    private final SLAMetricsService slaMetricsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String path = request.getRequestURI();
        if(path.contains("/actuator")){
            filterChain.doFilter(request, response);
            return;
        }

        long startTime = System.currentTimeMillis();

        try{
            filterChain.doFilter(request, response);
        } finally {
                long duration = System.currentTimeMillis() - startTime;

                String normalizedPath = normalizePath(path);

            slaMetricsService.recordHttpRequest(
                    normalizedPath,
                    request.getMethod(),
                    response.getStatus(),
                    duration
            );
        }
    }

    private String normalizePath(String path) {
        return path.replaceAll("/\\d+", "/{id}")
                .replaceAll("/[a-fA-F0-9-]{36}", "/{uuid}");
    }
}
