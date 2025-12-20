package com.memoring.memoring_server.domain.caregiver.exception;

import com.memoring.memoring_server.global.exception.CustomException;
import com.memoring.memoring_server.global.exception.ErrorCode;

public class CaregiverRoleRequiredException extends CustomException {
    public CaregiverRoleRequiredException() {
        super(ErrorCode.CAREGIVER_ROLE_REQUIRED);
    }
}
