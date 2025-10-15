package com.zikpak.facecheck.security.filters;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Фильтр для добавления security headers
 * ВАЖНО: Должен выполняться ПЕРВЫМ в цепочке фильтров
 */
@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class SecurityHeadersFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        log.info("Initializing Security Headers Filter");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // Применяем заголовки для ВСЕХ запросов
        addSecurityHeaders(httpResponse);

        // Логируем для отладки
        String path = httpRequest.getRequestURI();
        log.debug("Adding security headers for path: {}", path);

        chain.doFilter(request, response);
    }

    private void addSecurityHeaders(HttpServletResponse response) {
        // Основные security headers - ВСЕГДА добавляем
        response.setHeader("X-Content-Type-Options", "nosniff");
        response.setHeader("X-Frame-Options", "DENY");
        response.setHeader("X-XSS-Protection", "1; mode=block");

        // Дополнительные headers для безопасности
        response.setHeader("Referrer-Policy", "strict-origin-when-cross-origin");
        response.setHeader("Permissions-Policy", "geolocation=(), microphone=(), camera=()");
        response.setHeader("X-Permitted-Cross-Domain-Policies", "none");

        // CSP - упрощенная версия для совместимости
        response.setHeader("Content-Security-Policy",
                "default-src 'self'; " +
                        "script-src 'self' 'unsafe-inline' 'unsafe-eval' https://cdnjs.cloudflare.com; " +
                        "style-src 'self' 'unsafe-inline' https://fonts.googleapis.com; " +
                        "font-src 'self' data: https://fonts.gstatic.com; " +
                        "img-src 'self' data: https:; " +
                        "connect-src 'self' http://localhost:4200;"
        );

        // Скрываем информацию о сервере
        response.setHeader("Server", "");
        response.setHeader("X-Powered-By", "");

        // Cache control для API
        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate, private");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Expires", "0");
    }

    @Override
    public void destroy() {
        log.info("Destroying Security Headers Filter");
    }
}