package com.memoring.memoring_server.global.exception;

public class DiaryImageMissingException extends CustomException {
    public DiaryImageMissingException() {
        super(ErrorCode.DIARY_IMAGE_MISSING);
    }
}
