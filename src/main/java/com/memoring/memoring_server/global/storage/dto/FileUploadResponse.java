package com.memoring.memoring_server.global.storage.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "파일 업로드 응답")
public record FileUploadResponse(
        @Schema(description = "업로드된 파일 이름")
        String fileName,

        @Schema(description = "업로드된 파일 접근 URL")
        String fileUrl,

        @Schema(description = "저장소의 S3 키")
        String s3key
) {
}
