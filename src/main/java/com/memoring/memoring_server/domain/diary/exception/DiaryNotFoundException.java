package com.memoring.memoring_server.domain.diary.exception;

import com.memoring.memoring_server.global.exception.CustomException;
import com.memoring.memoring_server.global.exception.ErrorCode;

public class DiaryNotFoundException extends CustomException {
  public DiaryNotFoundException() {
    super(ErrorCode.DIARY_NOT_FOUND);
  }
}
