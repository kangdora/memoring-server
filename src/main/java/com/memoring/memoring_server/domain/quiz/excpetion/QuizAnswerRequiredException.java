package com.memoring.memoring_server.domain.quiz.excpetion;

import com.memoring.memoring_server.global.exception.CustomException;
import com.memoring.memoring_server.global.exception.ErrorCode;

public class QuizAnswerRequiredException extends CustomException {
    public QuizAnswerRequiredException() {
        super(ErrorCode.QUIZ_ANSWER_REQUIRED);
    }
}
