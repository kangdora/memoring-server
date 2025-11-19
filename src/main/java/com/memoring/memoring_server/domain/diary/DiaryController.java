package com.memoring.memoring_server.domain.diary;

import com.memoring.memoring_server.domain.diary.dto.DiaryCreateRequestDto;
import com.memoring.memoring_server.domain.diary.dto.DiaryCreateResponseDto;
import com.memoring.memoring_server.domain.diary.dto.DiaryDetailResponseDto;
import com.memoring.memoring_server.global.external.stt.SttService;
import com.memoring.memoring_server.global.storage.StorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/diary")
@RequiredArgsConstructor
public class DiaryController implements DiaryApi {

    private final DiaryService diaryService;
    private final SttService sttService;
    private final StorageService storageService;

    @Override
    @PostMapping
    public ResponseEntity<DiaryCreateResponseDto> createDiary(@RequestBody DiaryCreateRequestDto dto){
        DiaryCreateResponseDto response = diaryService.createDiary(dto);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{diaryId}/image")
    public ResponseEntity<Void> uploadDiaryImage(
            @PathVariable Long diaryId,
            @RequestPart("file") MultipartFile file
    ) {
        String imageS3Key = storageService.uploadDiaryImage(diaryId, file);

        diaryService.updateDiaryImageKey(diaryId, imageS3Key);

        return ResponseEntity.noContent().build();
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
