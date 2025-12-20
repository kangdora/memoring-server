package com.memoring.memoring_server.domain.caregiver.exception;

import com.memoring.memoring_server.global.exception.CustomException;
import com.memoring.memoring_server.global.exception.ErrorCode;

public class CareRelationAlreadyExistsException extends CustomException {
    public CareRelationAlreadyExistsException() {
        super(ErrorCode.CARE_RELATION_ALREADY_EXISTS);
    }
}
