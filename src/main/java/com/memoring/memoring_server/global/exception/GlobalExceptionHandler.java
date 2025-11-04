package com.memoring.memoring_server.global.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(PasswordMismatchException.class)
    public ResponseEntity<ErrorResponse> handlePasswordMismatchException(PasswordMismatchException ex) {
        return ResponseEntity.status(ErrorCode.PASSWORD_MISMATCH.getStatus())
                .body(ErrorResponse.of(ErrorCode.PASSWORD_MISMATCH));
    }

    @ExceptionHandler(DuplicateLoginIdException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateLoginIdException(DuplicateLoginIdException e) {
        return ResponseEntity.status(ErrorCode.DUPLICATE_LOGIN_ID.getStatus())
                .body(ErrorResponse.of(ErrorCode.DUPLICATE_LOGIN_ID));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneral(Exception ex) {
        return ResponseEntity.status(ErrorCode.INTERNAL_SERVER_ERROR.getStatus())
                .body(ErrorResponse.of(ErrorCode.INTERNAL_SERVER_ERROR));
    }
}
