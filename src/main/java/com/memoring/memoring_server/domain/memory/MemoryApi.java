package com.memoring.memoring_server.domain.memory;

import com.memoring.memoring_server.domain.memory.dto.MemoryDiaryResponseDto;
import com.memoring.memoring_server.domain.memory.dto.MemoryDiarySummaryDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Tag(name = "메모리", description = "메모리 조회 API")
public interface MemoryApi {

    @Operation(summary = "최근 메모리 조회", description = "최근 일기 3개의 요약 정보를 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "최근 메모리 조회 성공"),
            @ApiResponse(responseCode = "404", description = "메모리를 찾을 수 없음")
    })
    ResponseEntity<List<MemoryDiarySummaryDto>> getRecentMemories(@PathVariable Long memoryId);

    @Operation(summary = "메모리 전체 조회", description = "해당 메모리에 속한 모든 일기를 최신순으로 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "메모리 조회 성공"),
            @ApiResponse(responseCode = "404", description = "메모리를 찾을 수 없음")
    })
    ResponseEntity<List<MemoryDiaryResponseDto>> getMemories(@PathVariable Long memoryId);
}
