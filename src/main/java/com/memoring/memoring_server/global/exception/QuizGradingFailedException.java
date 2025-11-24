package com.memoring.memoring_server.global.exception;

public class QuizGradingFailedException extends CustomException {
  public QuizGradingFailedException() {
    super(ErrorCode.QUIZ_GRADING_FAILED);
  }

  public QuizGradingFailedException(Throwable cause) {
    super(ErrorCode.QUIZ_GRADING_FAILED, cause);
  }
}
