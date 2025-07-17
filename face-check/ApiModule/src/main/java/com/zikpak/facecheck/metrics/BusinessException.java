package com.zikpak.facecheck.metrics;


import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

@Getter
public class BusinessException extends RuntimeException {

    private final String category;
    private final String operation;
    private final String errorCode;
    private final HttpStatus httpStatus;
    private final Map<String, String> context;

    public BusinessException(String message,
                             String category,
                             String operation,
                             String errorCode,
                             HttpStatus httpStatus) {
        super(message);
        this.category = category;
        this.operation = operation;
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
        this.context = new HashMap<>();
    }

    public BusinessException(String message,
                             String category,
                             String operation,
                             String errorCode,
                             HttpStatus httpStatus,
                             Map<String, String> context) {
        super(message);
        this.category = category;
        this.operation = operation;
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
        this.context = context;
    }

    // Удобные фабричные методы
    public static BusinessException payrollError(String message, String errorCode) {
        return new BusinessException(
                message,
                "payroll",
                "payroll_calculation",
                errorCode,
                HttpStatus.UNPROCESSABLE_ENTITY
        );
    }

    public static BusinessException validationError(String message, String field) {
        Map<String, String> context = new HashMap<>();
        context.put("field", field);
        return new BusinessException(
                message,
                "validation",
                "input_validation",
                "INVALID_INPUT",
                HttpStatus.BAD_REQUEST,
                context
        );
    }

    public static BusinessException notFound(String entity, String id) {
        Map<String, String> context = new HashMap<>();
        context.put("entity", entity);
        context.put("id", id);
        return new BusinessException(
                entity + " not found with id: " + id,
                "data",
                "entity_lookup",
                "NOT_FOUND",
                HttpStatus.NOT_FOUND,
                context
        );
    }

    public static BusinessException accessDenied(String resource) {
        return new BusinessException(
                "Access denied to resource: " + resource,
                "security",
                "authorization",
                "ACCESS_DENIED",
                HttpStatus.FORBIDDEN
        );
    }
}