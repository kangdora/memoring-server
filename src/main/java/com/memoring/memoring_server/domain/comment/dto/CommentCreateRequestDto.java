package com.memoring.memoring_server.domain.comment.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "댓글 생성 요청")
public record CommentCreateRequestDto(
        @Schema(description = "댓글을 작성할 일기 ID")
        Long diaryId,

        @Schema(description = "댓글 작성자 ID")
        Long userId,

        @Schema(description = "댓글 내용")
        String content
) {
}
