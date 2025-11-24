package com.memoring.memoring_server.global.exception;

public class ExpiredRefreshTokenException extends CustomException {
    public ExpiredRefreshTokenException() {
        super(ErrorCode.EXPIRED_REFRESH_TOKEN);
    }
}
