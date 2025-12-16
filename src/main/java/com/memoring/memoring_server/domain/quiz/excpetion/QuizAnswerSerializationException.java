package com.memoring.memoring_server.domain.quiz.excpetion;

import com.memoring.memoring_server.global.exception.CustomException;
import com.memoring.memoring_server.global.exception.ErrorCode;

public class QuizAnswerSerializationException extends CustomException {
    public QuizAnswerSerializationException(Throwable cause) {
        super(ErrorCode.QUIZ_ANSWER_SERIALIZATION_FAILED, cause);
    }
}