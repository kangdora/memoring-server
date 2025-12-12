package com.memoring.memoring_server.domain.record;

import com.memoring.memoring_server.domain.mission.UserMission;
import com.memoring.memoring_server.domain.mission.UserMissionRepository;
import com.memoring.memoring_server.domain.record.dto.RecordResponse;
import com.memoring.memoring_server.domain.user.UserService;
import com.memoring.memoring_server.global.exception.MissionNotFoundException;
import com.memoring.memoring_server.global.storage.StorageService;
import com.memoring.memoring_server.global.storage.dto.FileDeleteRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RecordService {

    private final RecordRepository recordRepository;
    private final UserService userService;
    private final UserMissionRepository userMissionRepository;
    private final StorageService storageService;

    @Transactional
    public RecordResponse saveRecord(MultipartFile file, String username) {
        UserMission userMission = userMissionRepository.findByUser(userService.getUserByUsername(username))
                .orElseThrow(MissionNotFoundException::new);

        if (userMission.getMission() == null) {
            throw new MissionNotFoundException();
        }

        String s3key = storageService.uploadRecord(userMission.getUser().getId(), file);
        Long sizeBytes = file.getSize();

        Record record = recordRepository.findByUserMission(userMission)
                .map(existing -> {
                    storageService.deleteFile(new FileDeleteRequest(existing.getS3key()));
                    existing.update(s3key, sizeBytes);
                    return existing;
                })
                .orElseGet(() -> recordRepository.save(Record.create(userMission, s3key, sizeBytes)));

        return new RecordResponse(record.getId(), storageService.generatePresignedUrl(record.getS3key()), record.getSizeBytes());
    }

    public Optional<RecordResponse> getRecord(String username) {
        return userMissionRepository.findByUser(userService.getUserByUsername(username))
                .flatMap(recordRepository::findByUserMission)
                .map(record -> new RecordResponse(
                        record.getId(),
                        storageService.generatePresignedUrl(record.getS3key()),
                        record.getSizeBytes()
                ));
    }
}