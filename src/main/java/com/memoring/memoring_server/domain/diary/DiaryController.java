package com.memoring.memoring_server.domain.diary;

import com.memoring.memoring_server.domain.diary.dto.DiaryCreateRequestDto;
import com.memoring.memoring_server.domain.diary.dto.DiaryCreateResponseDto;
import com.memoring.memoring_server.domain.diary.dto.DiaryDetailResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/diary")
@RequiredArgsConstructor
public class DiaryController implements DiaryApi {

    private final DiaryService diaryService;

    @Override
    @PostMapping
    public ResponseEntity<DiaryCreateResponseDto> createDiary(@RequestBody DiaryCreateRequestDto dto){
        DiaryCreateResponseDto response = diaryService.createDiary(dto);
        return ResponseEntity.ok(response);
    }

    @Override
    @GetMapping("/{diaryId}")
    public ResponseEntity<DiaryDetailResponseDto> getDiary(@PathVariable Long diaryId) {
        return diaryService.getDiary(diaryId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Override
    @DeleteMapping("/{diaryId}")
    public ResponseEntity<Void> deleteDiary(@PathVariable Long diaryId) {
        if (diaryService.deleteDiary(diaryId)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
