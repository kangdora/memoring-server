package com.memoring.memoring_server.global.storage;

import com.memoring.memoring_server.global.storage.dto.FileDeleteRequest;
import com.memoring.memoring_server.global.storage.dto.FileUploadResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/storage")
@RequiredArgsConstructor
public class StorageController implements StorageApi {

    private final StorageService storageService;

    @Override
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<FileUploadResponse> uploadFile(@RequestPart("file") MultipartFile file) {
        FileUploadResponse response = storageService.uploadFile(file);
        return ResponseEntity.ok(response);
    }

    @Override
    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteFile(@RequestBody FileDeleteRequest request){
        if (storageService.deleteFile(request)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}