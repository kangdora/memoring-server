package com.memoring.memoring_server.global.exception;

public class OpenAiApiKeyMissingException extends CustomException {
    public OpenAiApiKeyMissingException() {
        super(ErrorCode.OPENAI_API_KEY_NOT_CONFIGURED);
    }
}