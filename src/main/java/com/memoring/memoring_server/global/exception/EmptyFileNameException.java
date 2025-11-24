package com.memoring.memoring_server.global.exception;

public class EmptyFileNameException extends CustomException {
  public EmptyFileNameException() {
    super(ErrorCode.FILE_NAME_EMPTY);
  }
}