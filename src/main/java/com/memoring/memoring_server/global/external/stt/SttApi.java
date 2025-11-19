package com.memoring.memoring_server.global.external.stt;

import com.memoring.memoring_server.global.external.stt.dto.SttTranscriptionResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "STT", description = "음성 파일을 텍스트로 변환하는 Whisper 기반 API")
public interface SttApi {
    @Operation(summary = "음성 텍스트 변환", description = "업로드된 음성 파일을 OpenAI Whisper 모델로 텍스트로 변환합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "변환 성공"),
            @ApiResponse(responseCode = "400", description = "요청 파일 누락 또는 형식 오류"),
            @ApiResponse(responseCode = "500", description = "변환 중 서버 오류")
    })
    ResponseEntity<SttTranscriptionResponseDto> transcribe(MultipartFile file);
}