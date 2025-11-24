package com.memoring.memoring_server.global.exception;

public class QuizAnswerRequiredException extends CustomException {
    public QuizAnswerRequiredException() {
        super(ErrorCode.QUIZ_ANSWER_REQUIRED);
    }
}
