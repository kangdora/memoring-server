package com.memoring.memoring_server.domain.comment.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "댓글 응답")
public record CommentResponseDto(
        @Schema(description = "댓글 ID")
        Long commentId,

        @Schema(description = "일기 ID")
        Long diaryId,

        @Schema(description = "작성자 ID")
        Long userId,

        @Schema(description = "댓글 내용")
        String content,

        @Schema(description = "작성 시각")
        LocalDateTime createdAt
) {
}
