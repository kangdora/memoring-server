package com.memoring.memoring_server.global.exception;

public class StoredFileNotFoundException extends CustomException {
    public StoredFileNotFoundException() {
        super(ErrorCode.FILE_NOT_FOUND);
    }
}