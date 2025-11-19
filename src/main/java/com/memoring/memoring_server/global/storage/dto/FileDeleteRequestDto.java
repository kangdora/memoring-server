package com.memoring.memoring_server.global.storage.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "파일 삭제 요청")
public record FileDeleteRequestDto(
        @Schema(description = "삭제할 파일의 S3 키")
        String s3key
) {
}
