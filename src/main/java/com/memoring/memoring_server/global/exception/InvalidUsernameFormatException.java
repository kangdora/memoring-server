package com.memoring.memoring_server.global.exception;

public class InvalidUsernameFormatException extends CustomException {
    public InvalidUsernameFormatException() {
        super(ErrorCode.INVALID_USERNAME_CONFLICT);
    }
}
