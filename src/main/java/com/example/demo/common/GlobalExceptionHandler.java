package com.example.demo.common;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.NoHandlerFoundException;

@ControllerAdvice
public class GlobalExceptionHandler  {

    @ExceptionHandler({EntityNotFoundException.class})
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

    @ExceptionHandler({EntityExistsException.class})
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

    @ExceptionHandler({IllegalArgumentException.class})
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

    @ExceptionHandler({Exception.class})
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

    @ExceptionHandler({JWTVerificationException.class})
    public ResponseEntity<?> handleJWTVerificationException(JWTVerificationException ex) {
        ErrorMessage errorMessage = ErrorMessage.builder()
                .success(false)
                .statusCode(Enums.ErrorKey.ErrorNoPermission)
                .timestamp(new java.sql.Timestamp(System.currentTimeMillis()))
                .message(ex.getLocalizedMessage())
                .data("hehe")
                .build();

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(errorMessage);
    }

    @ExceptionHandler({RuntimeException.class})
    public ResponseEntity<?> handleRuntimeException(RuntimeException ex) {
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

    @ExceptionHandler({SignatureVerificationException.class})
    public ResponseEntity<?> handleSignatureVerificationException(SignatureVerificationException ex) {
        System.out.println("con heheheheh");
        ErrorMessage errorMessage = ErrorMessage.builder()
                .success(false)
                .statusCode(Enums.ErrorKey.ErrorNoPermission)
                .timestamp(new java.sql.Timestamp(System.currentTimeMillis()))
                .message(ex.getLocalizedMessage())
                .data("hehe")
                .build();

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(errorMessage);
    }
}