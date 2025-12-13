package com.memoring.memoring_server.global.exception;

public class InvalidPasswordFormatException extends CustomException {
    public InvalidPasswordFormatException() {
        super(ErrorCode.INVALID_PASSWORD_FORMAT);
    }
}
