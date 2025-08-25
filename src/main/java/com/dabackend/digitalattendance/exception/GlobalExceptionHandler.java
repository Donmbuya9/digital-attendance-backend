package com.dabackend.digitalattendance.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException; // <-- NEW IMPORT
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.time.Instant;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    // This handles the "User with email ... already exists" error.
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Object> handleIllegalStateException(IllegalStateException ex, WebRequest request) {
        return buildErrorResponse(ex, HttpStatus.CONFLICT); // 409 Conflict
    }

    // This handles the "Invalid email or password" error.
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Object> handleBadCredentialsException(BadCredentialsException ex, WebRequest request) {
        return buildErrorResponse(ex, HttpStatus.UNAUTHORIZED); // 401 Unauthorized
    }

    // ✅ NEW: This handles forbidden access due to security restrictions.
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Object> handleAccessDeniedException(AccessDeniedException ex, WebRequest request) {
        return buildErrorResponse(new Exception("You do not have permission to access this resource."), HttpStatus.FORBIDDEN); // 403 Forbidden
    }

    // A fallback for any other unexpected errors.
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleAllExceptions(Exception ex, WebRequest request) {
        // It's good practice to log the full exception for debugging.
        ex.printStackTrace();
        return buildErrorResponse(ex, HttpStatus.INTERNAL_SERVER_ERROR); // 500 Internal Server Error
    }

    private ResponseEntity<Object> buildErrorResponse(Exception ex, HttpStatus status) {
        Map<String, Object> body = Map.of(
                "timestamp", Instant.now().toString(),
                "status", status.value(),
                "error", status.getReasonPhrase(),
                "message", ex.getMessage()
        );
        return new ResponseEntity<>(body, status);
    }
}
