package com.productmanagement.exception;

import com.productmanagement.dto.ApiResponse;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse> handleResourceNotFoundException(ResourceNotFoundException ex) {
        logger.warn("Resource not found: {}", ex.getMessage());
        ApiResponse response = new ApiResponse(false, ex.getMessage(), null);
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InsufficientInventoryException.class)
    public ResponseEntity<ApiResponse> handleInsufficientInventoryException(InsufficientInventoryException ex) {
        logger.warn("Insufficient inventory: {}", ex.getMessage());
        ApiResponse response = new ApiResponse(false, ex.getMessage(), null);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ApiResponse> handleDuplicateResourceException(DuplicateResourceException ex) {
        logger.warn("Duplicate resource: {}", ex.getMessage());
        ApiResponse response = new ApiResponse(false, ex.getMessage(), null);
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(InvalidOperationException.class)
    public ResponseEntity<ApiResponse> handleInvalidOperationException(InvalidOperationException ex) {
        logger.warn("Invalid operation: {}", ex.getMessage());
        ApiResponse response = new ApiResponse(false, ex.getMessage(), null);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        logger.warn("Validation failed: {}", ex.getMessage());
        Map<String, String> errors = new HashMap<>();
        
        // Handle field errors
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            String fieldName = error.getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        
        // Handle global errors (non-field errors)
        ex.getBindingResult().getGlobalErrors().forEach(error -> {
            String objectName = error.getObjectName();
            String errorMessage = error.getDefaultMessage();
            errors.put(objectName, errorMessage);
        });
        
        ApiResponse response = new ApiResponse(false, "Validation failed", errors);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse> handleConstraintViolationException(ConstraintViolationException ex) {
        logger.warn("Constraint violation: {}", ex.getMessage());
        Map<String, String> errors = new HashMap<>();
        
        ex.getConstraintViolations().forEach(violation -> {
            String propertyPath = violation.getPropertyPath().toString();
            String message = violation.getMessage();
            errors.put(propertyPath, message);
        });
        
        ApiResponse response = new ApiResponse(false, "Validation failed", errors);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        logger.error("Data integrity violation: {}", ex.getMessage(), ex);
        String message = "Data integrity violation. The operation cannot be completed.";
        
        // Check for specific constraint violations
        if (ex.getMessage() != null) {
            if (ex.getMessage().contains("Duplicate entry")) {
                message = "A record with this information already exists.";
            } else if (ex.getMessage().contains("foreign key constraint")) {
                message = "Cannot delete this record as it is referenced by other records.";
            } else if (ex.getMessage().contains("unique constraint")) {
                message = "A record with this unique value already exists.";
            }
        }
        
        ApiResponse response = new ApiResponse(false, message, null);
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiResponse> handleBadCredentialsException(BadCredentialsException ex) {
        logger.warn("Bad credentials: {}", ex.getMessage());
        ApiResponse response = new ApiResponse(false, "Invalid username or password", null);
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(InsufficientAuthenticationException.class)
    public ResponseEntity<ApiResponse> handleInsufficientAuthenticationException(InsufficientAuthenticationException ex) {
        logger.warn("Insufficient authentication: {}", ex.getMessage());
        ApiResponse response = new ApiResponse(false, "Authentication required. Please provide valid credentials.", null);
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse> handleAccessDeniedException(AccessDeniedException ex) {
        logger.warn("Access denied: {}", ex.getMessage());
        ApiResponse response = new ApiResponse(false, "Access denied. Insufficient permissions.", null);
        return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiResponse> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException ex) {
        logger.warn("Method not supported: {}", ex.getMessage());
        String message = String.format("Method '%s' is not supported for this endpoint. Supported methods: %s",
                ex.getMethod(), ex.getSupportedHttpMethods());
        ApiResponse response = new ApiResponse(false, message, null);
        return new ResponseEntity<>(response, HttpStatus.METHOD_NOT_ALLOWED);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiResponse> handleMissingServletRequestParameterException(MissingServletRequestParameterException ex) {
        logger.warn("Missing request parameter: {}", ex.getMessage());
        String message = String.format("Required parameter '%s' is missing", ex.getParameterName());
        ApiResponse response = new ApiResponse(false, message, null);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex) {
        logger.warn("Argument type mismatch: {}", ex.getMessage());
        Class<?> requiredType = ex.getRequiredType();
        String expectedType = (requiredType != null) ? requiredType.getSimpleName() : "unknown";
        String message = String.format("Invalid value '%s' for parameter '%s'. Expected type: %s",
                ex.getValue(), ex.getName(), expectedType);
        ApiResponse response = new ApiResponse(false, message, null);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        logger.warn("Message not readable: {}", ex.getMessage());
        String message = "Invalid request body. Please check the JSON format and required fields.";
        ApiResponse response = new ApiResponse(false, message, null);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ApiResponse> handleNoHandlerFoundException(NoHandlerFoundException ex) {
        logger.warn("No handler found: {}", ex.getMessage());
        String message = String.format("Endpoint '%s %s' not found", ex.getHttpMethod(), ex.getRequestURL());
        ApiResponse response = new ApiResponse(false, message, null);
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
        logger.warn("Illegal argument: {}", ex.getMessage());
        ApiResponse response = new ApiResponse(false, ex.getMessage(), null);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse> handleGenericException(Exception ex) {
        logger.error("Unexpected error occurred: {}", ex.getMessage(), ex);
        // Don't expose internal error details in production
        String message = "An unexpected error occurred. Please try again later.";
        ApiResponse response = new ApiResponse(false, message, null);
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
