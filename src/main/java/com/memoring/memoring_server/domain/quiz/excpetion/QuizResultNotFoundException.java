package com.memoring.memoring_server.domain.quiz.excpetion;

import com.memoring.memoring_server.global.exception.CustomException;
import com.memoring.memoring_server.global.exception.ErrorCode;

public class QuizResultNotFoundException extends CustomException {
    public QuizResultNotFoundException() {
        super(ErrorCode.QUIZ_RESULT_NOT_FOUND);
    }
}
