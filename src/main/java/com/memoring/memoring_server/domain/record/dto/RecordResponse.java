package com.memoring.memoring_server.domain.record.dto;

public record RecordResponse(
        Long recordId,
        String playbackUrl,
        Long sizeBytes
) {
}
