package com.memoring.memoring_server.global.external.openai;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
@ConfigurationProperties(prefix = "openai")
public class OpenAiProperties {
    private String apiKey;
    private String baseUrl = "https://api.openai.com/v1";
    private String whisperModel = "whisper-1";
}