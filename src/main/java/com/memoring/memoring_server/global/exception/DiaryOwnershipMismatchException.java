package com.memoring.memoring_server.global.exception;

public class DiaryOwnershipMismatchException extends CustomException {
    public DiaryOwnershipMismatchException() {
        super(ErrorCode.DIARY_OWNERSHIP_MISMATCH);
    }
}