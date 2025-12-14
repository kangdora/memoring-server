package com.memoring.memoring_server.domain.quiz.excpetion;

import com.memoring.memoring_server.global.exception.CustomException;
import com.memoring.memoring_server.global.exception.ErrorCode;

public class QuizSetNotFoundException extends CustomException {
    public QuizSetNotFoundException() {
        super(ErrorCode.QUIZ_SET_NOT_FOUND);
    }
}
