package com.memoring.memoring_server.domain.user.exception;

import com.memoring.memoring_server.global.exception.CustomException;
import com.memoring.memoring_server.global.exception.ErrorCode;

public class InvalidPasswordFormatException extends CustomException {
    public InvalidPasswordFormatException() {
        super(ErrorCode.INVALID_PASSWORD_FORMAT);
    }
}
