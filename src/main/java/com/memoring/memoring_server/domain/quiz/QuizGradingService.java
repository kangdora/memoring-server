package com.memoring.memoring_server.domain.quiz;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.memoring.memoring_server.domain.quiz.dto.QuizAnswer;
import com.memoring.memoring_server.domain.quiz.excpetion.QuizGradingFailedException;
import com.memoring.memoring_server.global.exception.OpenAiApiKeyMissingException;
import com.memoring.memoring_server.global.external.openai.OpenAiProperties;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.List;

@Service
public class QuizGradingService {

    private static final String SYSTEM_PROMPT = "당신은 주어진 문제와 사용자 답변을 검증하는 채점자입니다. 항상 JSON 형식 {\\\"user_answer\\\":\\\"...\\\",\\\"is_correct\\\":true/false}로만 응답하세요. user_answer은 whisper을 통한 stt를 활용하여 들어옵니다. 사용자는 65세 이상의 노인으로 발음 및 의미가 상통하는 경우 의미를 해석하여 채점 기준에 맞게 채점해주세요.";

    private final WebClient webClient;
    private final ObjectMapper objectMapper;
    private final OpenAiProperties openAiProperties;

    public QuizGradingService(OpenAiProperties openAiProperties, WebClient.Builder webClientBuilder, ObjectMapper objectMapper) {
        this.openAiProperties = openAiProperties;
        this.objectMapper = objectMapper;
        if (!StringUtils.hasText(openAiProperties.getApiKey())) {
            throw new OpenAiApiKeyMissingException();
        }
        this.webClient = webClientBuilder
                .baseUrl(openAiProperties.getBaseUrl())
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + openAiProperties.getApiKey())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    public QuizAnswer grade(Quiz quiz, String userAnswer) {
        try {
            ChatCompletionRequest request = new ChatCompletionRequest(
                    openAiProperties.getChatModel(),
                    List.of(
                            new ChatMessage("system", SYSTEM_PROMPT),
                            new ChatMessage("user", buildUserPrompt(quiz, userAnswer))
                    )
            );

            ChatCompletionResponse response = webClient.post()
                    .uri("/chat/completions")
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(ChatCompletionResponse.class)
                    .block();

            if (response == null || response.choices() == null || response.choices().isEmpty()) {
                throw new QuizGradingFailedException();
            }

            ChatChoice choice = response.choices().getFirst();
            if (choice == null || choice.message() == null || !StringUtils.hasText(choice.message().content())) {
                throw new QuizGradingFailedException();
            }

            String rawContent = choice.message().content();
            String jsonContent = extractJson(rawContent);
            QuizAnswer result = objectMapper.readValue(jsonContent, QuizAnswer.class);
            if (result == null || result.userAnswer() == null || result.isCorrect() == null) {
                throw new QuizGradingFailedException();
            }
            return result;
        } catch (WebClientResponseException | JsonProcessingException e) {
            throw new QuizGradingFailedException(e);
        }
    }

    private String buildUserPrompt(Quiz quiz, String userAnswer) {
        StringBuilder builder = new StringBuilder();
        if (StringUtils.hasText(quiz.getPrompt())) {
            builder.append(quiz.getPrompt()).append("\n\n");
        }
        builder.append("[문제]\n")
                .append(quiz.getContent())
                .append("\n\n[사용자 답변]\n")
                .append(userAnswer);
        return builder.toString();
    }

    private String extractJson(String content) {
        if (!StringUtils.hasText(content)) {
            throw new QuizGradingFailedException();
        }
        String trimmed = content.trim();
        if (trimmed.startsWith("```")) {
            int firstNewline = trimmed.indexOf('\n');
            int lastTicks = trimmed.lastIndexOf("```");
            if (firstNewline > 0 && lastTicks > firstNewline) {
                return trimmed.substring(firstNewline + 1, lastTicks).trim();
            }
        }
        return trimmed;
    }

    private record ChatCompletionRequest(String model, List<ChatMessage> messages) {
    }

    private record ChatMessage(String role, String content) {
    }

    private record ChatCompletionResponse(List<ChatChoice> choices) {
    }

    private record ChatChoice(ChatMessage message) {
    }
}
