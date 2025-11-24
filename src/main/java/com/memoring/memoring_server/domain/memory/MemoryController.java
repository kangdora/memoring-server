package com.memoring.memoring_server.domain.memory;

import com.memoring.memoring_server.domain.memory.dto.MemoryDiaryResponse;
import com.memoring.memoring_server.domain.memory.dto.MemoryDiarySummary;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/memories")
@RequiredArgsConstructor
public class MemoryController implements MemoryApi {

    private final MemoryService memoryService;

    @Override
    @GetMapping("/{memoryId}/recent")
    public ResponseEntity<List<MemoryDiarySummary>> getRecentMemories(
            @PathVariable Long memoryId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return ResponseEntity.ok(memoryService.getRecentMemories(memoryId, userDetails.getUsername()));
    }

    @Override
    @GetMapping("/{memoryId}")
    public ResponseEntity<List<MemoryDiaryResponse>> getMemories(
            @PathVariable Long memoryId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return ResponseEntity.ok(memoryService.getMemories(memoryId, userDetails.getUsername()));
    }
}
