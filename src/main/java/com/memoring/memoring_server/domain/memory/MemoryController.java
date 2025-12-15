package com.memoring.memoring_server.domain.memory;

import com.memoring.memoring_server.domain.diary.dto.DiaryCreateRequest;
import com.memoring.memoring_server.domain.diary.dto.DiaryCreateResponse;
import com.memoring.memoring_server.domain.memory.dto.MemoryDiaryResponse;
import com.memoring.memoring_server.domain.memory.dto.MemoryDiarySummary;
import com.memoring.memoring_server.domain.memory.dto.MemoryWeeklyResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/memories")
@RequiredArgsConstructor
public class MemoryController implements MemoryApi {

    private final MemoryService memoryService;

    @Override
    @GetMapping("/{memoryId}/weeks")
    public ResponseEntity<MemoryWeeklyResponse> getWeeklyMemories(
            @PathVariable Long memoryId,
            @RequestParam(name = "date", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate date,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return ResponseEntity.ok(memoryService.getWeeklyMemories(memoryId, userDetails.getUsername(), date));
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
