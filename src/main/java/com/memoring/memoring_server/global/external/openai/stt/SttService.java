package com.memoring.memoring_server.global.external.openai.stt;

import com.memoring.memoring_server.global.external.openai.OpenAiProperties;
import com.memoring.memoring_server.global.external.openai.stt.dto.SttTranscriptionResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.Objects;

@Service
public class SttService {

    private final WebClient webClient;
    private final OpenAiProperties openAiProperties;

    public SttService(OpenAiProperties openAiProperties, WebClient.Builder webClientBuilder) {
        this.openAiProperties = openAiProperties;
        if (!StringUtils.hasText(openAiProperties.getApiKey())) {
            throw new IllegalStateException("OpenAI API key must be configured (openai.api-key)");
        }
        this.webClient = webClientBuilder
                .baseUrl(openAiProperties.getBaseUrl())
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + openAiProperties.getApiKey())
                .build();
    }

    public SttTranscriptionResponse transcribe(MultipartFile file) {
        MultipartBodyBuilder bodyBuilder = new MultipartBodyBuilder();
        bodyBuilder.part("file", file.getResource())
                .filename(Objects.requireNonNull(file.getOriginalFilename()));
        bodyBuilder.part("model", openAiProperties.getWhisperModel());

        try {
            OpenAiTranscriptionResponse response = webClient.post()
                    .uri("/audio/transcriptions")
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .body(BodyInserters.fromMultipartData(bodyBuilder.build()))
                    .retrieve()
                    .bodyToMono(OpenAiTranscriptionResponse.class)
                    .block();

            if (response == null || !StringUtils.hasText(response.text())) {
                throw new IllegalStateException("Empty response from OpenAI Whisper");
            }

            return new SttTranscriptionResponse(response.text());
        } catch (WebClientResponseException e) {
            throw new IllegalStateException("Failed to call OpenAI Whisper API: " + e.getResponseBodyAsString(), e);
        }
    }

    private record OpenAiTranscriptionResponse(String text) { }
}
