package com.memoring.memoring_server.domain.diary.exception;

import com.memoring.memoring_server.global.exception.CustomException;
import com.memoring.memoring_server.global.exception.ErrorCode;

public class DiaryOwnershipMismatchException extends CustomException {
    public DiaryOwnershipMismatchException() {
        super(ErrorCode.DIARY_OWNERSHIP_MISMATCH);
    }
}