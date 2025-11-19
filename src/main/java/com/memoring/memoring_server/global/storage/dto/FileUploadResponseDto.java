package com.memoring.memoring_server.global.storage.dto;

public record FileUploadResponseDto(String fileName, String fileUrl, String s3key) {
}
