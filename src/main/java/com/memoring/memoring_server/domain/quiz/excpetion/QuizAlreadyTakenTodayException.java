package com.memoring.memoring_server.domain.quiz.excpetion;

import com.memoring.memoring_server.global.exception.CustomException;
import com.memoring.memoring_server.global.exception.ErrorCode;

public class QuizAlreadyTakenTodayException extends CustomException {
    public QuizAlreadyTakenTodayException() {
        super(ErrorCode.QUIZ_ALREADY_TAKEN_TODAY);
    }
}
