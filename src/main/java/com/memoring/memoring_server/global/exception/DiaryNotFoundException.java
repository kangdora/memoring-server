package com.memoring.memoring_server.global.exception;

public class DiaryNotFoundException extends CustomException {
  public DiaryNotFoundException() {
    super(ErrorCode.DIARY_NOT_FOUND);
  }
}
