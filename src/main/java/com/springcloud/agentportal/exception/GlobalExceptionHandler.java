package com.springcloud.agentportal.exception;

import feign.FeignException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.util.stream.Collectors;

/**
 * Global exception handler to provide consistent error responses
 */
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(SubmissionServiceException.class)
    public ResponseEntity<ApiError> handleSubmissionServiceException(SubmissionServiceException ex, HttpServletRequest request) {
        log.error("Submission service exception: {}", ex.getMessage());
        ApiError apiError = new ApiError(
                HttpStatus.INTERNAL_SERVER_ERROR, 
                ex.getMessage(),
                request.getRequestURI());
        apiError.setErrorCode("SUBMISSION_SERVICE_ERROR");
        return new ResponseEntity<>(apiError, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(FeignException.class)
    public ResponseEntity<ApiError> handleFeignException(FeignException ex, HttpServletRequest request) {
        log.error("Feign client exception when calling external service: {}", ex.getMessage());
        ApiError apiError = new ApiError(
                HttpStatus.valueOf(ex.status()), 
                "External API error: " + ex.getMessage(),
                request.getRequestURI());
        apiError.setErrorCode("EXTERNAL_API_ERROR");
        return new ResponseEntity<>(apiError, HttpStatus.valueOf(ex.status() > 0 ? ex.status() : 500));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidationExceptions(MethodArgumentNotValidException ex, HttpServletRequest request) {
        String errorMessages = ex.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                .collect(Collectors.joining(", "));
        
        log.error("Validation error: {}", errorMessages);
        ApiError apiError = new ApiError(
                HttpStatus.BAD_REQUEST, 
                "Validation error: " + errorMessages,
                request.getRequestURI());
        apiError.setErrorCode("VALIDATION_ERROR");
        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ApiError> handleMaxSizeException(MaxUploadSizeExceededException ex, HttpServletRequest request) {
        log.error("File size exceeded: {}", ex.getMessage());
        ApiError apiError = new ApiError(
                HttpStatus.PAYLOAD_TOO_LARGE, 
                "File size exceeds the maximum allowed size",
                request.getRequestURI());
        apiError.setErrorCode("FILE_TOO_LARGE");
        return new ResponseEntity<>(apiError, HttpStatus.PAYLOAD_TOO_LARGE);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGenericException(Exception ex, HttpServletRequest request) {
        log.error("Unexpected error occurred: {}", ex.getMessage(), ex);
        ApiError apiError = new ApiError(
                HttpStatus.INTERNAL_SERVER_ERROR, 
                "An unexpected error occurred: " + ex.getMessage(),
                request.getRequestURI());
        apiError.setErrorCode("INTERNAL_SERVER_ERROR");
        return new ResponseEntity<>(apiError, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
