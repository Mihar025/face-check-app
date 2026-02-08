package com.zikpak.exceptions;

import jakarta.mail.MessagingException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.impl.FileSizeLimitExceededException;
import org.apache.tomcat.util.http.fileupload.impl.SizeLimitExceededException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Global Exception Handler - обрабатывает все исключения в приложении
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * Обработка превышения размера загружаемого файла - Spring
     */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<Map<String, Object>> handleMaxUploadSizeExceeded(
            MaxUploadSizeExceededException ex, HttpServletRequest request) {

        return createPayloadTooLargeResponse(request, "Maximum upload size exceeded");
    }

    /**
     * Обработка Multipart исключений
     */
    @ExceptionHandler(MultipartException.class)
    public ResponseEntity<Map<String, Object>> handleMultipartException(
            MultipartException ex, HttpServletRequest request) {

        // Проверяем, связано ли это с размером
        if (ex.getMessage() != null && ex.getMessage().contains("size")) {
            return createPayloadTooLargeResponse(request, "Request size exceeds limit");
        }

        return createBadRequestResponse(request, "Invalid multipart request");
    }

    /**
     * Обработка Tomcat размерных исключений
     */
    @ExceptionHandler({SizeLimitExceededException.class, FileSizeLimitExceededException.class})
    public ResponseEntity<Map<String, Object>> handleTomcatSizeExceptions(
            Exception ex, HttpServletRequest request) {

        return createPayloadTooLargeResponse(request, "Request size limit exceeded");
    }

    /**
     * Обработка IllegalStateException которые могут быть связаны с размером
     */
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalStateException(
            IllegalStateException ex, HttpServletRequest request) {

        // Проверяем, связано ли это с размером request body
        String message = ex.getMessage();
        if (message != null && (message.contains("Request body too large") ||
                message.contains("payload") ||
                message.contains("size"))) {
            return createPayloadTooLargeResponse(request, "Request payload too large");
        }

        // Иначе обрабатываем как обычную ошибку
        return createInternalErrorResponse(request, "Internal server error");
    }

    /**
     * Обработка 404 ошибок
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNoHandlerFoundException(
            NoHandlerFoundException ex, HttpServletRequest request) {

        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.NOT_FOUND.value());
        response.put("error", "Not Found");
        response.put("message", "The requested resource was not found");
        response.put("path", request.getRequestURI());

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .contentType(MediaType.APPLICATION_JSON)
                .body(response);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEntityNotFound(EntityNotFoundException ex) {
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(MessagingException.class)
    public ResponseEntity<ErrorResponse> handleMessaging(MessagingException ex) {
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Email sending failed.");
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntime(RuntimeException ex) {
        // Проверяем, не связано ли это с размером
        if (ex.getMessage() != null && ex.getMessage().contains("Request size")) {
            return buildResponse(HttpStatus.PAYLOAD_TOO_LARGE, "Request size limit exceeded");
        }
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(AccessDeniedException ex) {
        return buildResponse(HttpStatus.FORBIDDEN, "Access Denied.");
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentials(BadCredentialsException ex) {
        return buildResponse(HttpStatus.UNAUTHORIZED, "Invalid credentials.");
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUsernameNotFound(UsernameNotFoundException ex) {
        return buildResponse(HttpStatus.NOT_FOUND, "User not found.");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        String msg = ex.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        return buildResponse(HttpStatus.BAD_REQUEST, msg);
    }

    @ExceptionHandler({IOException.class, ServletException.class})
    public ResponseEntity<ErrorResponse> handleIOAndServlet(Exception ex) {
        // Проверяем на превышение размера
        if (ex.getMessage() != null && ex.getMessage().contains("size")) {
            return buildResponse(HttpStatus.PAYLOAD_TOO_LARGE, "Payload too large");
        }
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal processing error.");
    }

    /**
     * Обработка всех остальных исключений
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGlobalException(
            Exception ex, HttpServletRequest request) {

        // <-- ВОТ ЭТО ДОБАВЬ
        log.error("Unhandled exception: {} {}",
                request != null ? request.getMethod() : "N/A",
                request != null ? request.getRequestURI() : "N/A",
                ex
        );

        if (ex.getMessage() != null &&
                (ex.getMessage().contains("Request body too large") ||
                        ex.getMessage().contains("maximum") ||
                        ex.getMessage().contains("size limit"))) {
            return createPayloadTooLargeResponse(request, "Request exceeds size limit");
        }

        return createInternalErrorResponse(request, "An error occurred processing your request");
    }

    // Вспомогательные методы для создания ответов
    private ResponseEntity<Map<String, Object>> createPayloadTooLargeResponse(
            HttpServletRequest request, String message) {

        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.PAYLOAD_TOO_LARGE.value());
        response.put("error", "Payload Too Large");
        response.put("message", message);
        if (request != null) {
            response.put("path", request.getRequestURI());
        }

        return ResponseEntity
                .status(HttpStatus.PAYLOAD_TOO_LARGE)
                .contentType(MediaType.APPLICATION_JSON)
                .body(response);
    }

    private ResponseEntity<Map<String, Object>> createBadRequestResponse(
            HttpServletRequest request, String message) {

        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.BAD_REQUEST.value());
        response.put("error", "Bad Request");
        response.put("message", message);
        response.put("path", request.getRequestURI());

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON)
                .body(response);
    }

    private ResponseEntity<Map<String, Object>> createInternalErrorResponse(
            HttpServletRequest request, String message) {

        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        response.put("error", "Internal Server Error");
        response.put("message", message);
        if (request != null) {
            response.put("path", request.getRequestURI());
        }

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .contentType(MediaType.APPLICATION_JSON)
                .body(response);
    }

    private ResponseEntity<ErrorResponse> buildResponse(HttpStatus status, String message) {
        ErrorResponse error = new ErrorResponse(status.value(), message, LocalDateTime.now());
        return new ResponseEntity<>(error, status);
    }
}