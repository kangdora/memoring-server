package com.memoring.memoring_server.domain.diary;

import com.memoring.memoring_server.domain.diary.dto.DiaryCreateRequest;
import com.memoring.memoring_server.domain.diary.dto.DiaryCreateResponse;
import com.memoring.memoring_server.domain.diary.dto.DiaryDetailResponse;
import com.memoring.memoring_server.global.external.openai.stt.dto.SttTranscriptionResponse;
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

    @Override
    @PostMapping
    public ResponseEntity<DiaryCreateResponse> createDiary(@RequestBody DiaryCreateRequest request){
        DiaryCreateResponse response = diaryService.createDiary(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{diaryId}/image")
    public ResponseEntity<Void> uploadDiaryImage(
            @PathVariable Long diaryId,
            @RequestPart("file") MultipartFile file
    ) {
        diaryService.uploadDiaryImage(diaryId, file);

        return ResponseEntity.noContent().build();
    }

    @Override
    @GetMapping("/{diaryId}")
    public ResponseEntity<DiaryDetailResponse> getDiary(@PathVariable Long diaryId) {
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

    @Override
    @PostMapping(value = "/transcribe", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<SttTranscriptionResponse> transcribeDiaryAudio(
            @RequestPart("file") MultipartFile file
    ) {
        SttTranscriptionResponse response = diaryService.transcribeDiaryAudio(file);
        return ResponseEntity.ok(response);
    }
}
