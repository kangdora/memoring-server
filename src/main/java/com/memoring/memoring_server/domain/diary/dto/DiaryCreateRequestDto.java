package com.memoring.memoring_server.domain.diary.dto;

import com.memoring.memoring_server.domain.diary.Emotion;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "일기 생성 요청")
public record DiaryCreateRequestDto(
        @Schema(description = "연결된 메모리 ID")
        Long memoryId,

        @Schema(description = "선택된 사용자 미션 ID")
        Long missionId,

        @Schema(description = "작성한 일기 내용")
        String content,

        @Schema(description = "작성자의 감정 상태")
        Emotion mood
) {
}