package com.memoring.memoring_server.global.exception;

public class DuplicateLoginIdException extends CustomException {
    public DuplicateLoginIdException() {
        super(ErrorCode.DUPLICATE_LOGIN_ID);
    }
}
