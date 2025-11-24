package com.memoring.memoring_server.global.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Getter
public class ErrorResponse {

    private final String code;
    private final int status;
    private final String message;
    private final LocalDateTime timestamp;

    private ErrorResponse(ErrorCode errorCode) {
        this.code = errorCode.name();
        this.status = errorCode.getStatus().value();
        this.message = errorCode.getMessage();
        this.timestamp = LocalDateTime.now();
    }

    private ErrorResponse(String message, HttpStatus status) {
        this.code = status.name();
        this.status = status.value();
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }

    public static ErrorResponse of(ErrorCode errorCode) {
        return new ErrorResponse(errorCode);
    }

    public static ErrorResponse of(String message, HttpStatus status) {
        return new ErrorResponse(message, status);
    }
}
