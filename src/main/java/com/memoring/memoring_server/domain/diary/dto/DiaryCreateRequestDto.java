package com.memoring.memoring_server.domain.diary.dto;

import com.memoring.memoring_server.domain.message.Emotion;

public record DiaryCreateRequestDto(
        Long memoryId,
        Long missionId,
        String content,
        Emotion mood
) {
}
