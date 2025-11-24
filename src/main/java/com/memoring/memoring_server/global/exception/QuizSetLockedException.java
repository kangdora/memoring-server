package com.memoring.memoring_server.global.exception;

public class QuizSetLockedException extends CustomException {
    public QuizSetLockedException() {
        super(ErrorCode.QUIZ_SET_LOCKED);
    }
}
