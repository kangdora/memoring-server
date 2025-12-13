package com.memoring.memoring_server.domain.memory;

import com.memoring.memoring_server.domain.diary.dto.DiaryCreateRequest;
import com.memoring.memoring_server.domain.diary.dto.DiaryCreateResponse;
import com.memoring.memoring_server.domain.memory.dto.MemoryDiaryResponse;
import com.memoring.memoring_server.domain.memory.dto.MemoryDiarySummary;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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

    @Override
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<DiaryCreateResponse> createDiary(
            @RequestPart("request") DiaryCreateRequest request,
            @RequestPart("image") MultipartFile image,
            @AuthenticationPrincipal UserDetails userDetails
    ){
        DiaryCreateResponse response = memoryService.createDiary(request, image, userDetails.getUsername());
        return ResponseEntity.ok(response);
    }
}
