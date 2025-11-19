package com.memoring.memoring_server.domain.memory;

import com.memoring.memoring_server.domain.memory.dto.MemoryDiaryResponseDto;
import com.memoring.memoring_server.domain.memory.dto.MemoryDiarySummaryDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/memories")
@RequiredArgsConstructor
public class MemoryController implements MemoryApi {

    private final MemoryService memoryService;

    @Override
    @GetMapping("/{memoryId}/recent")
    public ResponseEntity<List<MemoryDiarySummaryDto>> getRecentMemories(@PathVariable Long memoryId) {
        return ResponseEntity.ok(memoryService.getRecentMemories(memoryId));
    }

    @Override
    @GetMapping("/{memoryId}")
    public ResponseEntity<List<MemoryDiaryResponseDto>> getMemories(@PathVariable Long memoryId) {
        return ResponseEntity.ok(memoryService.getMemories(memoryId));
    }
}
