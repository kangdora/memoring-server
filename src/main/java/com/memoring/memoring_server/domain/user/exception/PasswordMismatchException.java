package com.memoring.memoring_server.domain.user.exception;

import com.memoring.memoring_server.global.exception.CustomException;
import com.memoring.memoring_server.global.exception.ErrorCode;

public class PasswordMismatchException extends CustomException {
    public PasswordMismatchException() {
        super(ErrorCode.PASSWORD_MISMATCH);
    }
}
