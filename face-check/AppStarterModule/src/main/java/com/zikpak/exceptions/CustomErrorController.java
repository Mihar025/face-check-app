package com.zikpak.exceptions;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Кастомный Error Controller для возврата JSON ответов вместо HTML
 * Обрабатывает все ошибки, включая 404
 */
@RestController
public class CustomErrorController implements ErrorController {

    @RequestMapping(value = "/error", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> handleError(HttpServletRequest request) {

        // Получаем информацию об ошибке из атрибутов запроса
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        Object error = request.getAttribute(RequestDispatcher.ERROR_EXCEPTION);
        Object message = request.getAttribute(RequestDispatcher.ERROR_MESSAGE);
        Object path = request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI);

        Integer statusCode = HttpStatus.INTERNAL_SERVER_ERROR.value();
        String errorMessage = "An error occurred";

        if (status != null) {
            try {
                statusCode = Integer.valueOf(status.toString());
            } catch (NumberFormatException e) {
                statusCode = HttpStatus.INTERNAL_SERVER_ERROR.value();
            }
        }

        // Определяем сообщение на основе кода статуса
        HttpStatus httpStatus;
        try {
            httpStatus = HttpStatus.valueOf(statusCode);
        } catch (IllegalArgumentException e) {
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
            statusCode = httpStatus.value();
        }

        // Формируем сообщение об ошибке
        if (statusCode == HttpStatus.NOT_FOUND.value()) {
            errorMessage = "The requested resource was not found";
        } else if (statusCode == HttpStatus.UNAUTHORIZED.value()) {
            errorMessage = "Authentication required";
        } else if (statusCode == HttpStatus.FORBIDDEN.value()) {
            errorMessage = "Access denied";
        } else if (statusCode == HttpStatus.BAD_REQUEST.value()) {
            errorMessage = "Bad request";
        } else if (statusCode == HttpStatus.METHOD_NOT_ALLOWED.value()) {
            errorMessage = "Method not allowed";
        } else if (statusCode == HttpStatus.TOO_MANY_REQUESTS.value()) {
            errorMessage = "Too many requests";
        } else if (statusCode == HttpStatus.PAYLOAD_TOO_LARGE.value()) {
            errorMessage = "Payload too large";
        } else if (message != null && !message.toString().isEmpty()) {
            errorMessage = message.toString();
        }

        // Создаем JSON ответ
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("timestamp", LocalDateTime.now().toString());
        errorResponse.put("status", statusCode);
        errorResponse.put("error", httpStatus.getReasonPhrase());
        errorResponse.put("message", errorMessage);
        errorResponse.put("path", path != null ? path.toString() : request.getRequestURI());

        return ResponseEntity
                .status(statusCode)
                .contentType(MediaType.APPLICATION_JSON)
                .body(errorResponse);
    }
}