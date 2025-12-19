package com.memoring.memoring_server.domain.caregiver.exception;

import com.memoring.memoring_server.global.exception.CustomException;
import com.memoring.memoring_server.global.exception.ErrorCode;

public class CaregiverInviteAlreadyExpiredException extends CustomException {
    public CaregiverInviteAlreadyExpiredException() {
        super(ErrorCode.CARE_INVITE_ALREADY_EXPIRED);
    }
}
