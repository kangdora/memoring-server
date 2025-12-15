package com.memoring.memoring_server.domain.memory;

import com.memoring.memoring_server.domain.diary.dto.DiaryCreateRequest;
import com.memoring.memoring_server.domain.diary.dto.DiaryCreateResponse;
import com.memoring.memoring_server.domain.memory.dto.MemoryDiaryResponse;
import com.memoring.memoring_server.domain.memory.dto.MemoryDiarySummary;
import com.memoring.memoring_server.domain.memory.dto.MemoryWeeklyResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@Tag(name = "메모리", description = "메모리 조회 API")
public interface MemoryApi {

    @Operation(summary = "메모리 주차별 조회", description = "해당 주차의 일기와 썸네일을 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "주차별 메모리 조회 성공"),
            @ApiResponse(responseCode = "404", description = "메모리를 찾을 수 없음")
    })
    ResponseEntity<MemoryWeeklyResponse> getWeeklyMemories(
            @PathVariable Long memoryId,
            @RequestParam(name = "date", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate date,
            @AuthenticationPrincipal UserDetails userDetails
    );


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
}
