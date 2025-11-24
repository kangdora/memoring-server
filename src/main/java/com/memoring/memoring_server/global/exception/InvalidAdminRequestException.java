package com.memoring.memoring_server.global.exception;

public class InvalidAdminRequestException extends CustomException {
    public InvalidAdminRequestException() {
        super(ErrorCode.INVALID_ADMIN_REQUEST);
    }
}
