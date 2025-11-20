package com.memoring.memoring_server.domain.diary.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "일기 이미지 업로드용 presigned URL 응답")
public record DiaryImagePresignedUrlResponse(
        @Schema(description = "이미지 업로드에 사용할 presigned URL")
        String uploadUrl,

        @Schema(description = "이미지를 업로드할 S3 키")
        String s3key
) {
}
