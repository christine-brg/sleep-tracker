package org.example.sleeptracker.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.example.sleeptracker.dto.ErrorResponse;
import org.example.sleeptracker.dto.PasswordChangeRequest;
import org.example.sleeptracker.exceptions.JwtAuthenticationException;
import org.example.sleeptracker.exceptions.PasswordMismatchException;
import org.example.sleeptracker.exceptions.ResourceNotFoundException;
import org.example.sleeptracker.exceptions.UnauthorizedUserException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(JwtAuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleJwtAuthenticationException(JwtAuthenticationException e, HttpServletRequest request) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .message(e.getMessage())
                .details(request.getRequestURI())
                .recoverable("WARNING")
                .build();
        return ResponseEntity.status(400).body(errorResponse);
    }

    @ExceptionHandler(PasswordMismatchException.class)
    public ResponseEntity<ErrorResponse> handlePasswordMismatchException(PasswordMismatchException e, HttpServletRequest request) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .message(e.getMessage())
                .details(request.getRequestURI())
                .recoverable("WARNING")
                .build();
        return ResponseEntity.status(400).body(errorResponse);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(ResourceNotFoundException e, HttpServletRequest request) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .message(e.getMessage())
                .details(request.getRequestURI())
                .recoverable("WARNING")
                .build();
        return ResponseEntity.status(404).body(errorResponse);
    }

    @ExceptionHandler(UnauthorizedUserException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorizedUserException(UnauthorizedUserException e, HttpServletRequest request) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .message(e.getMessage())
                .details(request.getRequestURI())
                .recoverable("WARNING")
                .build();
        return ResponseEntity.status(401).body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e, HttpServletRequest request) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .message(e.getMessage())
                .details(request.getRequestURI())
                .recoverable("CRITICAL ERROR")
                .build();
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
