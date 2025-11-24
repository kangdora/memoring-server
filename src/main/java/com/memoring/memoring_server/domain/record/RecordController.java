package com.memoring.memoring_server.domain.record;

import com.memoring.memoring_server.domain.record.dto.RecordResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/record")
@RequiredArgsConstructor
public class RecordController implements RecordApi {

    private final RecordService recordService;

    @Override
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<RecordResponse> uploadRecord(
            @RequestPart("file") MultipartFile file,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        RecordResponse response = recordService.saveRecord(file, userDetails.getUsername());
        return ResponseEntity.ok(response);
    }

    @Override
    @GetMapping
    public ResponseEntity<RecordResponse> getRecord(@AuthenticationPrincipal UserDetails userDetails) {
        return recordService.getRecord(userDetails.getUsername())
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
