package com.memoring.memoring_server.domain.diary;

import com.memoring.memoring_server.domain.diary.dto.DiaryCreateRequestDto;
import com.memoring.memoring_server.domain.diary.dto.DiaryCreateResponseDto;
import com.memoring.memoring_server.domain.diary.dto.DiaryDetailResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/diary")
public class DiaryController implements DiaryApi {

    @PostMapping
    public ResponseEntity<DiaryCreateResponseDto> createDiary(@RequestBody DiaryCreateRequestDto dto){
        DiaryCreateResponseDto response = null;
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{diaryId}")
    public ResponseEntity<DiaryDetailResponseDto> getDiary(@PathVariable Long diaryId) {
        DiaryDetailResponseDto dto = null;
        return ResponseEntity.ok(dto);
    }

    @DeleteMapping("/{diaryId}")
    public ResponseEntity<Void> deleteDiary(@PathVariable Long diaryId) {
        return ResponseEntity.noContent().build();
    }
}
