package com.memoring.memoring_server.global.exception;

public class MissionNotFoundException extends CustomException {
    public MissionNotFoundException() {
        super(ErrorCode.MISSION_NOT_FOUND);
    }
}