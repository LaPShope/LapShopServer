package com.example.demo.Common;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.NoHandlerFoundException;

@ControllerAdvice
public class GlobalExceptionHandler {



    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<?> handleEntityNotFoundException(EntityNotFoundException ex) {
        ErrorMessage errorMessage = ErrorMessage.builder()
                .success(false)
                .statusCode(Enums.ErrorKey.ErrorInternal)
                .timestamp(new java.sql.Timestamp(System.currentTimeMillis()))
                .message(ex.getLocalizedMessage())
                .data(null)
                .build();

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(errorMessage);
    }

    @ExceptionHandler(EntityExistsException.class)
    public ResponseEntity<?> handleEntityExistsException(EntityExistsException ex) {
        ErrorMessage errorMessage = ErrorMessage.builder()
                .success(false)
                .statusCode(Enums.ErrorKey.ErrorNoPermission)
                .timestamp(new java.sql.Timestamp(System.currentTimeMillis()))
                .message(ex.getLocalizedMessage())
                .data(null)
                .build();

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(errorMessage);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleIllegalArgumentException(IllegalArgumentException ex) {
        ErrorMessage errorMessage = ErrorMessage.builder()
                .success(false)
                .statusCode(Enums.ErrorKey.ErrorNoPermission)
                .timestamp(new java.sql.Timestamp(System.currentTimeMillis()))
                .message(ex.getLocalizedMessage())
                .data(null)
                .build();

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(errorMessage);
    }

    @ExceptionHandler({NoHandlerFoundException.class})
    public ResponseEntity<?> handleNoHandlerFoundException(NoHandlerFoundException ex) {
        ErrorMessage errorMessage = ErrorMessage.builder()
                .success(false)
                .statusCode(Enums.ErrorKey.ErrorInternal)
                .timestamp(new java.sql.Timestamp(System.currentTimeMillis()))
                .message(ex.getLocalizedMessage())
                .data(null)
                .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGeneralException(Exception ex) {
        ErrorMessage errorMessage = ErrorMessage.builder()
                .success(false)
                .statusCode(Enums.ErrorKey.ErrorInternal)
                .timestamp(new java.sql.Timestamp(System.currentTimeMillis()))
                .message(ex.getLocalizedMessage())
                .data(null)
                .build();

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(errorMessage);
    }


}