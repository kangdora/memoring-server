package com.memoring.memoring_server.global.storage;

import com.memoring.memoring_server.global.storage.dto.FileDeleteRequest;
import com.memoring.memoring_server.global.storage.dto.FileUploadResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "파일 스토리지", description = "파일 업로드 및 삭제 API")
public interface StorageApi {

    @Operation(summary = "파일 업로드", description = "멀티파트 파일을 업로드하고 S3 키와 접근 URL을 반환합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "파일 업로드 성공"),
            @ApiResponse(responseCode = "400", description = "파일 이름이 비어 있음")
    })
    ResponseEntity<FileUploadResponse> uploadFile(MultipartFile file) throws Exception;

    @Operation(summary = "파일 삭제", description = "S3 키를 기반으로 업로드된 파일을 삭제합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "파일 삭제 성공"),
            @ApiResponse(responseCode = "404", description = "삭제할 파일을 찾을 수 없음")
    })
    ResponseEntity<Void> deleteFile(FileDeleteRequest request) throws Exception;
}