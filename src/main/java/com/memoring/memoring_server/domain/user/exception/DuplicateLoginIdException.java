package com.memoring.memoring_server.domain.user.exception;

import com.memoring.memoring_server.global.exception.CustomException;
import com.memoring.memoring_server.global.exception.ErrorCode;

public class DuplicateLoginIdException extends CustomException {
    public DuplicateLoginIdException() {
        super(ErrorCode.DUPLICATE_LOGIN_ID);
    }
}
