package com.memoring.memoring_server.global.exception;

public class InvalidMissionSelectionException extends CustomException {
    public InvalidMissionSelectionException() {
        super(ErrorCode.INVALID_MISSION_SELECTION);
    }
}