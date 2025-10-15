package com.zikpak.interceptor.security;

import com.zikpak.facecheck.annotation.RateLimit;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class RateLimitInterceptor implements HandlerInterceptor {

    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        log.info("🔍 Interceptor processing path: {}", request.getRequestURI());

        // Проверяем, не был ли уже обработан ответ
        if (response.isCommitted()) {
            return false;
        }

        if (!(handler instanceof HandlerMethod)) {
            log.debug("Handler is not a HandlerMethod, skipping rate limiting");
            return true;
        }

        HandlerMethod handlerMethod = (HandlerMethod) handler;
        RateLimit rateLimit = handlerMethod.getMethodAnnotation(RateLimit.class);

        if (rateLimit == null) {
            log.debug("No @RateLimit annotation found on method: {}", handlerMethod.getMethod().getName());
            return true;
        }

        String ip = getClientIp(request);
        String key = ip + ":" + request.getServletPath();

        log.info("📊 Rate limiting check for IP: {} on path: {}", ip, request.getServletPath());
        log.info("📊 Rate limit configuration: {} requests per {} seconds",
                rateLimit.requests(), rateLimit.perSeconds());

        Bucket bucket = buckets.computeIfAbsent(key, k -> {
            log.info("🪣 Creating new bucket for key: {}", k);
            return createBucket(rateLimit);
        });

        boolean consumed = bucket.tryConsume(1);
        long availableTokens = bucket.getAvailableTokens();

        log.info("🎯 Token consumed: {}, Available tokens: {}", consumed, availableTokens);

        if (consumed) {
            // Добавляем заголовки с информацией о лимитах
            response.setHeader("X-RateLimit-Limit", String.valueOf(rateLimit.requests()));
            response.setHeader("X-RateLimit-Remaining", String.valueOf(availableTokens));
            return true;
        } else {
            log.warn("🚫 RATE LIMIT EXCEEDED for key: {}", key);
            sendErrorResponse(response, rateLimit);
            return false;
        }
    }

    private void sendErrorResponse(HttpServletResponse response, RateLimit rateLimit) throws IOException {
        if (!response.isCommitted()) {
            response.setStatus(429);
            response.setContentType("application/json;charset=UTF-8");
            response.setHeader("X-RateLimit-Limit", String.valueOf(rateLimit.requests()));
            response.setHeader("X-RateLimit-Remaining", "0");
            response.setHeader("Retry-After", String.valueOf(rateLimit.perSeconds()));

            String jsonResponse = String.format(
                    "{\"status\":429," +
                            "\"error\":\"Too Many Requests\"," +
                            "\"message\":\"Rate limit exceeded. Maximum %d requests per %d seconds allowed.\"," +
                            "\"timestamp\":\"%s\"," +
                            "\"retryAfter\":%d}",
                    rateLimit.requests(),
                    rateLimit.perSeconds(),
                    java.time.LocalDateTime.now(),
                    rateLimit.perSeconds()
            );

            response.getWriter().write(jsonResponse);
            response.getWriter().flush();
            response.getWriter().close();
        }
    }

    private Bucket createBucket(RateLimit rateLimit) {
        Bandwidth limit = Bandwidth.classic(
                rateLimit.requests(),
                Refill.intervally(
                        rateLimit.requests(),
                        Duration.ofSeconds(rateLimit.perSeconds())
                )
        );
        return Bucket.builder()
                .addLimit(limit)
                .build();
    }

    private String getClientIp(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null || xfHeader.isEmpty()) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0].trim();
    }
}