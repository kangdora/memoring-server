package com.memoring.memoring_server.domain.caregiver.exception;

import com.memoring.memoring_server.global.exception.CustomException;
import com.memoring.memoring_server.global.exception.ErrorCode;

public class CareRelationAccessDeniedException extends CustomException {
  public CareRelationAccessDeniedException() {
    super(ErrorCode.CARE_RELATION_ACCESS_DENIED);
  }
}
