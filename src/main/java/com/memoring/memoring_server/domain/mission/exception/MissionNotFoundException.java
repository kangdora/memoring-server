package com.memoring.memoring_server.domain.mission.exception;

import com.memoring.memoring_server.global.exception.CustomException;
import com.memoring.memoring_server.global.exception.ErrorCode;

public class MissionNotFoundException extends CustomException {
    public MissionNotFoundException() {
        super(ErrorCode.MISSION_NOT_FOUND);
    }
}