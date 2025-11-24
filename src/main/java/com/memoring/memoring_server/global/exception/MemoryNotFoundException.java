package com.memoring.memoring_server.global.exception;

public class MemoryNotFoundException extends CustomException {
    public MemoryNotFoundException() {
        super(ErrorCode.MEMORY_NOT_FOUND);
    }
}