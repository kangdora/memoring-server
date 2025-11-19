package com.memoring.memoring_server.global.storage;

import com.memoring.memoring_server.global.storage.dto.FileDeleteRequestDto;
import com.memoring.memoring_server.global.storage.dto.FileUploadResponseDto;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/storage")
public class StorageController implements StorageApi {

    private final StorageService storageService;

    public StorageController(StorageService storageService) {
        this.storageService = storageService;
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<FileUploadResponseDto> uploadFile(@RequestPart("file") MultipartFile file) throws IOException {
        FileUploadResponseDto response = storageService.uploadFile(file);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteFile(@RequestBody FileDeleteRequestDto request) throws IOException {
        if (storageService.deleteFile(request)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}