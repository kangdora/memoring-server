package com.memoring.memoring_server.domain.diary.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "일기 이미지 업로드용 presigned URL 요청")
public record DiaryImagePresignedUrlRequest(
        @Schema(description = "업로드할 파일 이름", example = "photo.png")
        String fileName,

        @Schema(description = "업로드할 파일의 콘텐츠 타입", example = "image/png")
        String contentType,

        @Schema(description = "업로드할 파일 크기(바이트)", example = "102400")
        Long fileSizeBytes
) {
}
