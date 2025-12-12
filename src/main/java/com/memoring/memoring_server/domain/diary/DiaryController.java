package com.memoring.memoring_server.domain.diary;

import com.memoring.memoring_server.domain.diary.dto.*;
import com.memoring.memoring_server.global.external.openai.stt.dto.SttTranscriptionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/diary")
@RequiredArgsConstructor
public class DiaryController implements DiaryApi {

    private final DiaryService diaryService;

    @Override
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<DiaryCreateResponse> createDiary(
            @RequestPart("request") DiaryCreateRequest request,
            @RequestPart("image") MultipartFile image,
            @AuthenticationPrincipal UserDetails userDetails
    ){
        DiaryCreateResponse response = diaryService.createDiary(request, image, userDetails.getUsername());
        return ResponseEntity.ok(response);
    }

    @Override
    @GetMapping("/{diaryId}")
    public ResponseEntity<DiaryDetailResponse> getDiary(
            @PathVariable Long diaryId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return diaryService.getDiary(diaryId, userDetails.getUsername())
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build()); // 얘 서비스단으로 빼죠
    }

    @Override
    @DeleteMapping("/{diaryId}")
    public ResponseEntity<Void> deleteDiary(
            @PathVariable Long diaryId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        if (diaryService.deleteDiary(diaryId, userDetails.getUsername())) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build(); // 얘 서비스단으로 빼죠
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
