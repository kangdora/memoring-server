package com.memoring.memoring_server.domain.user.exception;

import com.memoring.memoring_server.global.exception.CustomException;
import com.memoring.memoring_server.global.exception.ErrorCode;

public class InvalidUsernameFormatException extends CustomException {
    public InvalidUsernameFormatException() {
        super(ErrorCode.INVALID_USERNAME_CONFLICT);
    }
}
