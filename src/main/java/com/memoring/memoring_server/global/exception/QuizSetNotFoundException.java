package com.memoring.memoring_server.global.exception;

public class QuizSetNotFoundException extends CustomException {
    public QuizSetNotFoundException() {
        super(ErrorCode.QUIZ_SET_NOT_FOUND);
    }
}
