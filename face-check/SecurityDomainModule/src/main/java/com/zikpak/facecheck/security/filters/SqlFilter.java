package com.zikpak.facecheck.security.filters;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Pattern;

@Slf4j
@Component
@Order(2)
public class SqlFilter implements Filter {

    private static final Pattern[] SQL_PATTERNS = {
            // DDL (Data Definition Language) команды
            Pattern.compile("(?i)(.*)(\\b)+(CREATE|DROP|ALTER|TRUNCATE)(\\b)+\\s.*"),

            // DML (Data Manipulation Language) команды
            Pattern.compile("(?i)(.*)(\\b)+(SELECT|INSERT|UPDATE|DELETE|MERGE)(\\b)+\\s.*"),

            // DCL (Data Control Language) команды
            Pattern.compile("(?i)(.*)(\\b)+(GRANT|REVOKE)(\\b)+\\s.*"),

            // TCL (Transaction Control Language) команды
            Pattern.compile("(?i)(.*)(\\b)+(COMMIT|ROLLBACK|SAVEPOINT)(\\b)+\\s.*"),

            // Специальные операторы и комментарии
            Pattern.compile("(?i)(.*)(\\b)+(UNION|INTERSECT|MINUS)(\\b)+\\s.*"),
            Pattern.compile("(?i)(.*)(\\b)+(EXECUTE|EXEC)(\\b)+\\s.*"),
            Pattern.compile("--.*"),            // SQL комментарии
            Pattern.compile("/\\*.*?\\*/")      // Многострочные комментарии
    };

    private static final String[] SUSPICIOUS_CHARS = {
            "'", "\"", "=", "||", "&&", "\\", ";",
            "/*", "*/", "@@", "@", "+", "^",
            "|", "&", "~", "<", ">", "!",
    };

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        log.info("Initializing SQL Injection Filter");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        try {
            // 1. Сначала проверяем URI и параметры
            if (isSqlInjectionDetected(httpRequest)) {
                log.warn("SQL Injection attempt detected in URI or parameters");
                sendErrorResponse(httpResponse);
                return;
            }

            // 2. Проверяем JSON тело для POST/PUT запросов
            if (isPostOrPutRequest(httpRequest) && hasJsonContent(httpRequest)) {
                String jsonBody = readJsonBody(httpRequest);
                if (containsSqlInjectionInJson(jsonBody)) {
                    log.warn("SQL Injection attempt detected in JSON body");
                    sendErrorResponse(httpResponse);
                    return;
                }
                // Восстанавливаем тело запроса для дальнейшего использования
                request = new RequestWrapper(httpRequest, jsonBody);
            }

            chain.doFilter(request, response);
        } catch (Exception e) {
            log.error("Error in SQL Injection Filter", e);
            httpResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private boolean isSqlInjectionDetected(HttpServletRequest request) {
        // 1. Проверяем URI
        if (checkForSqlInjection(request.getRequestURI())) {
            log.warn("SQL Injection pattern found in URI");
            return true;
        }

        // 2. Проверяем параметры запроса
        Map<String, String[]> params = request.getParameterMap();
        for (Map.Entry<String, String[]> entry : params.entrySet()) {
            String paramName = entry.getKey();
            String[] paramValues = entry.getValue();

            // Проверяем имя параметра
            if (checkForSqlInjection(paramName)) {
                log.warn("SQL Injection pattern found in parameter name: {}", paramName);
                return true;
            }

            // Проверяем значения параметра
            for (String value : paramValues) {
                if (checkForSqlInjection(value)) {
                    log.warn("SQL Injection pattern found in parameter value: {}", value);
                    return true;
                }
            }
        }

        return false;
    }

    private boolean isPostOrPutRequest(HttpServletRequest request) {
        return "POST".equalsIgnoreCase(request.getMethod()) ||
                "PUT".equalsIgnoreCase(request.getMethod());
    }

    private boolean hasJsonContent(HttpServletRequest request) {
        String contentType = request.getContentType();
        return contentType != null && contentType.contains("application/json");
    }

    private String readJsonBody(HttpServletRequest request) throws IOException {
        StringBuilder sb = new StringBuilder();
        BufferedReader reader = request.getReader();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        return sb.toString();
    }

    private boolean containsSqlInjectionInJson(String jsonBody) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(jsonBody);
            return checkJsonNodeForSqlInjection(rootNode);
        } catch (Exception e) {
            log.error("Error parsing JSON body", e);
            return false;
        }
    }

    private boolean checkJsonNodeForSqlInjection(JsonNode node) {
        if (node.isObject()) {
            Iterator<Map.Entry<String, JsonNode>> fields = node.fields();
            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> field = fields.next();
                JsonNode value = field.getValue();
                if (value.isTextual() && checkForSqlInjection(value.asText())) {
                    log.warn("SQL Injection found in field: {}", field.getKey());
                    return true;
                }
            }
        }
        return false;
    }

    private boolean checkForSqlInjection(String value) {
        if (value == null || value.trim().isEmpty()) {
            return false;
        }

        // Декодируем URL-encoded строку
        String decodedValue = urlDecode(value);

        // 1. Проверяем на SQL паттерны
        for (Pattern pattern : SQL_PATTERNS) {
            if (pattern.matcher(decodedValue).matches()) {
                log.debug("SQL pattern matched: {}", pattern.pattern());
                return true;
            }
        }

        // 2. Проверяем на подозрительные символы
        for (String suspect : SUSPICIOUS_CHARS) {
            if (decodedValue.contains(suspect)) {
                // Проверяем контекст использования подозрительного символа
                if (isSymbolInSuspiciousContext(decodedValue, suspect)) {
                    log.debug("Suspicious character found in suspicious context: {}", suspect);
                    return true;
                }
            }
        }

        return false;
    }

    private String urlDecode(String value) {
        try {
            return URLDecoder.decode(value, StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            log.error("Error decoding URL parameter", e);
            return value;
        }
    }

    private boolean isSymbolInSuspiciousContext(String value, String symbol) {
        if ("'".equals(symbol) || "\"".equals(symbol)) {
            String[] parts = value.split(symbol);
            for (String part : parts) {
                if (part.toLowerCase().contains("select") ||
                        part.toLowerCase().contains("union") ||
                        part.toLowerCase().contains("delete")) {
                    return true;
                }
            }
        }
        return false;
    }

    private void sendErrorResponse(HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        response.setContentType("application/json");
        response.getWriter().write("{\"error\": \"SQL injection detected\", \"status\": 400}");
    }

    // Вспомогательный класс для сохранения тела запроса
    private static class RequestWrapper extends HttpServletRequestWrapper {
        private final String body;

        public RequestWrapper(HttpServletRequest request, String body) {
            super(request);
            this.body = body;
        }

        @Override
        public ServletInputStream getInputStream() throws IOException {
            final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(body.getBytes());
            return new ServletInputStream() {
                @Override
                public boolean isFinished() { return false; }
                @Override
                public boolean isReady() { return true; }
                @Override
                public void setReadListener(ReadListener readListener) {}
                @Override
                public int read() { return byteArrayInputStream.read(); }
            };
        }

        @Override
        public BufferedReader getReader() {
            return new BufferedReader(new StringReader(body));
        }
    }

    @Override
    public void destroy() {
        log.info("Destroying SQL Injection Filter");
    }
}