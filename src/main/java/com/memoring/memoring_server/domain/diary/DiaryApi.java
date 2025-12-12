package com.memoring.memoring_server.domain.diary;

import com.memoring.memoring_server.domain.diary.dto.*;
import com.memoring.memoring_server.global.external.openai.stt.dto.SttTranscriptionResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "일기", description = "일기 작성 및 관리 API")
public interface DiaryApi {

    @Operation(summary = "일기 생성", description = "새로운 일기를 작성하고 사진을 업로드")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "일기 생성 성공"),
            @ApiResponse(responseCode = "404", description = "기억 또는 미션을 찾을 수 없음"),
            @ApiResponse(responseCode = "400", description = "기억과 미션의 소유자가 일치하지 않음")
    })
    ResponseEntity<DiaryCreateResponse> createDiary(
            DiaryCreateRequest request,
            MultipartFile image,
            @AuthenticationPrincipal UserDetails userDetails
    );

    @Operation(summary = "일기 단건 조회", description = "일기 ID로 상세 내용을 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "일기 조회 성공"),
            @ApiResponse(responseCode = "404", description = "해당 일기를 찾을 수 없음")
    })
    ResponseEntity<DiaryDetailResponse> getDiary(Long diaryId, @AuthenticationPrincipal UserDetails userDetails);

    @Operation(summary = "일기 삭제", description = "일기 ID로 저장된 일기를 삭제")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "일기 삭제 성공"),
            @ApiResponse(responseCode = "404", description = "삭제할 일기가 존재하지 않음")
    })
    ResponseEntity<Void> deleteDiary(Long diaryId, @AuthenticationPrincipal UserDetails userDetails);

    @Operation(summary = "일기 음성 텍스트 변환", description = "다이어리 작성을 위한 음성 파일을 텍스트로 변환")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "변환 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 파일 요청"),
            @ApiResponse(responseCode = "500", description = "OpenAI 연동 오류")
    })
    ResponseEntity<SttTranscriptionResponse> transcribeDiaryAudio(MultipartFile file);
}