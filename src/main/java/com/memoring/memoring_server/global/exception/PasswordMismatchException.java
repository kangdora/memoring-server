package com.memoring.memoring_server.global.exception;

public class PasswordMismatchException extends CustomException {
    public PasswordMismatchException() {
        super(ErrorCode.PASSWORD_MISMATCH);
    }
}
