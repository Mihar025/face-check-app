package com.zikpak.facecheck.security.filters;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;

/**
 * Фильтр для ограничения размера запросов
 * Блокирует слишком большие payload
 */
@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 1)
public class RequestSizeLimitFilter implements Filter {

    private static final long MAX_REQUEST_SIZE = 1 * 1024 * 1024; // 1MB для JSON
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB для файлов

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        log.info("Initializing Request Size Limit Filter");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // Получаем размер контента
        long contentLength = httpRequest.getContentLengthLong();
        String contentType = httpRequest.getContentType();

        // Проверяем размер для разных типов контента
        if (contentLength > 0) {
            boolean isFileUpload = contentType != null && contentType.contains("multipart/form-data");
            long maxSize = isFileUpload ? MAX_FILE_SIZE : MAX_REQUEST_SIZE;

            if (contentLength > maxSize) {
                log.warn("Request size {} exceeds maximum allowed size of {} for path: {}",
                        contentLength, maxSize, httpRequest.getRequestURI());

                sendErrorResponse(httpResponse,
                        "Payload too large. Maximum size is " + (maxSize / (1024 * 1024)) + "MB");
                return;
            }
        }

        chain.doFilter(request, response);
    }

    private void sendErrorResponse(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpStatus.PAYLOAD_TOO_LARGE.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        String jsonResponse = String.format(
                "{\"timestamp\":\"%s\",\"status\":%d,\"error\":\"%s\",\"message\":\"%s\"}",
                LocalDateTime.now(),
                HttpStatus.PAYLOAD_TOO_LARGE.value(),
                HttpStatus.PAYLOAD_TOO_LARGE.getReasonPhrase(),
                message
        );

        response.getWriter().write(jsonResponse);
        response.getWriter().flush();
    }

    @Override
    public void destroy() {
        log.info("Destroying Request Size Limit Filter");
    }
}