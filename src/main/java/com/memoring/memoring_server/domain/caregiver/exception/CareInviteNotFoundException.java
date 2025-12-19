package com.memoring.memoring_server.domain.caregiver.exception;

import com.memoring.memoring_server.global.exception.CustomException;
import com.memoring.memoring_server.global.exception.ErrorCode;

public class CareInviteNotFoundException extends CustomException {
    public CareInviteNotFoundException() {
        super(ErrorCode.CARE_INVITE_NOT_FOUND);
    }
}
