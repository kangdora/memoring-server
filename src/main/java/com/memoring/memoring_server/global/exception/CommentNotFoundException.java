package com.memoring.memoring_server.global.exception;

public class CommentNotFoundException extends CustomException {
    public CommentNotFoundException() {
        super(ErrorCode.COMMENT_NOT_FOUND);
    }
}
