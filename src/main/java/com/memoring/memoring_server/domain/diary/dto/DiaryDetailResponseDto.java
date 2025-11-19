package com.memoring.memoring_server.domain.diary.dto;

import com.memoring.memoring_server.domain.message.Emotion;

public record DiaryDetailResponseDto(
        Long diaryId,
        Long memoryId,
        Long missionId,
        String content,
        Emotion mood
) {
}
