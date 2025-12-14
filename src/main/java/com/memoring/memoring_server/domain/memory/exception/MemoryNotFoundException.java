package com.memoring.memoring_server.domain.memory.exception;

import com.memoring.memoring_server.global.exception.CustomException;
import com.memoring.memoring_server.global.exception.ErrorCode;

public class MemoryNotFoundException extends CustomException {
    public MemoryNotFoundException() {
        super(ErrorCode.MEMORY_NOT_FOUND);
    }
}