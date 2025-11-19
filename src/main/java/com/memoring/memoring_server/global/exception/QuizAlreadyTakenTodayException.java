package com.memoring.memoring_server.global.exception;

public class QuizAlreadyTakenTodayException extends CustomException {
    public QuizAlreadyTakenTodayException() {
        super(ErrorCode.QUIZ_ALREADY_TAKEN_TODAY);
    }
}
