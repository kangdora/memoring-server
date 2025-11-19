package com.memoring.memoring_server.domain.diary.dto;

import com.memoring.memoring_server.domain.message.Emotion;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

@Schema(description = "일기 상세 조회 응답")
public record DiaryDetailResponseDto(
        @Schema(description = "일기 작성 날짜")
        LocalDate date,

        @Schema(description = "일기 이미지 presigned URL")
        String imageUrl,

        @Schema(description = "미션 내용")
        String missionContent,

        @Schema(description = "일기 내용")
        String content,

        @Schema(description = "작성자의 기분")
        Emotion mood
) {
}
