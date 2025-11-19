package com.memoring.memoring_server.domain.record.dto;

public record RecordResponseDto(
        Long recordId,
        String playbackUrl,
        Long sizeBytes
) {
}
