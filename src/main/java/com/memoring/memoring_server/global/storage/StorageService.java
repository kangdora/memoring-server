package com.memoring.memoring_server.global.storage;

import com.memoring.memoring_server.global.exception.EmptyFileNameException;
import com.memoring.memoring_server.global.exception.StoredFileNotFoundException;
import com.memoring.memoring_server.global.storage.dto.FileDeleteRequestDto;
import com.memoring.memoring_server.global.storage.dto.FileUploadResponseDto;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class StorageService {

    private static final String UPLOAD_DIR = "uploads";
    private final Map<String, Path> storedFiles = new HashMap<>();

    public FileUploadResponseDto uploadFile(MultipartFile file) throws IOException {
        Files.createDirectories(Path.of(UPLOAD_DIR));
        String fileName = file.getOriginalFilename();
        if (fileName == null || fileName.isBlank()) {
            throw new EmptyFileNameException();
        }

        String s3key = UUID.randomUUID() + "-" + fileName;
        Path destination = Path.of(UPLOAD_DIR, s3key);
        Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);
        storedFiles.put(s3key, destination);
        String url = "/" + destination;
        return new FileUploadResponseDto(fileName, url, s3key);
    }

    public boolean deleteFile(FileDeleteRequestDto request) throws IOException {
        String s3key = request.s3key();
        Optional<Path> storedPath = Optional.ofNullable(storedFiles.remove(s3key));
        if (storedPath.isEmpty()) {
            throw new StoredFileNotFoundException();
        }

        Files.deleteIfExists(storedPath.get());
        return true;
    }
}