package com.memoring.memoring_server.domain.user.exception;

import com.memoring.memoring_server.global.exception.CustomException;
import com.memoring.memoring_server.global.exception.ErrorCode;

public class InvalidSignupTypeException extends CustomException {
    public InvalidSignupTypeException() {
        super(ErrorCode.INVALID_SIGNUP_TYPE);
    }
}
