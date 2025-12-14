package com.memoring.memoring_server.domain.auth.exception;

import com.memoring.memoring_server.global.exception.CustomException;
import com.memoring.memoring_server.global.exception.ErrorCode;

public class ExpiredRefreshTokenException extends CustomException {
    public ExpiredRefreshTokenException() {
        super(ErrorCode.EXPIRED_REFRESH_TOKEN);
    }
}
