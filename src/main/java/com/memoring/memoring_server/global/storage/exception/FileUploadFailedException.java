package com.memoring.memoring_server.global.storage.exception;

import com.memoring.memoring_server.global.exception.CustomException;
import com.memoring.memoring_server.global.exception.ErrorCode;

public class FileUploadFailedException extends CustomException {
    public FileUploadFailedException(Throwable cause) {
        super(ErrorCode.FILE_UPLOAD_FAILED, cause);
    }
}