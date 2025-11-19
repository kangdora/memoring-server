package com.memoring.memoring_server.global.external.stt;

import com.memoring.memoring_server.global.external.stt.dto.SttTranscriptionResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/stt")
@RequiredArgsConstructor
public class SttController implements SttApi {
    private final SttService sttService;


    @PostMapping(value = "/transcribe", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<SttTranscriptionResponseDto> transcribe(@RequestPart("file") MultipartFile file) {
        SttTranscriptionResponseDto response = sttService.transcribe(file);
        return ResponseEntity.ok(response);
    }
}
