package com.memoring.memoring_server.global.exception;

public class OpenAiWhisperException extends CustomException {
    public OpenAiWhisperException() {
        super(ErrorCode.OPENAI_WHISPER_FAILED);
    }

    public OpenAiWhisperException(Throwable cause) {
        super(ErrorCode.OPENAI_WHISPER_FAILED, cause);
    }
}