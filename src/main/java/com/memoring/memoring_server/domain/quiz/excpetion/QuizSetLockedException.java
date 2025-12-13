package com.memoring.memoring_server.domain.quiz.excpetion;

import com.memoring.memoring_server.global.exception.CustomException;
import com.memoring.memoring_server.global.exception.ErrorCode;

public class QuizSetLockedException extends CustomException {
    public QuizSetLockedException() {
        super(ErrorCode.QUIZ_SET_LOCKED);
    }
}
