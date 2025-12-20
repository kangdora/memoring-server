package com.memoring.memoring_server.domain.record;

import com.memoring.memoring_server.domain.mission.Mission;
import com.memoring.memoring_server.domain.mission.UserMission;
import com.memoring.memoring_server.domain.mission.UserMissionRepository;
import com.memoring.memoring_server.domain.mission.exception.MissionNotFoundException;
import com.memoring.memoring_server.domain.record.dto.RecordResponse;
import com.memoring.memoring_server.domain.user.User;
import com.memoring.memoring_server.domain.user.UserService;
import com.memoring.memoring_server.global.storage.StorageService;
import com.memoring.memoring_server.global.storage.dto.FileDeleteRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class RecordServiceTest {

    @Mock
    private RecordRepository recordRepository;

    @Mock
    private UserService userService;

    @Mock
    private UserMissionRepository userMissionRepository;

    @Mock
    private StorageService storageService;

    @InjectMocks
    private RecordService recordService;

    @DisplayName("미션이 없으면 음성 기록 저장 시 예외가 발생한다")
    @Test
    void saveRecordFailsWhenMissionMissing() {
        User user = createUser("tester");
        given(userService.getUserByUsername("tester")).willReturn(user);
        given(userMissionRepository.findByUser(user)).willReturn(Optional.empty());

        MultipartFile file = org.mockito.Mockito.mock(MultipartFile.class);

        assertThatThrownBy(() -> recordService.saveRecord(file, "tester"))
                .isInstanceOf(MissionNotFoundException.class);
    }

    @DisplayName("새 음성 기록을 저장한다")
    @Test
    void saveRecordCreatesNew() {
        User user = createUser("tester");
        UserMission userMission = UserMission.create(user, Mission.create("m1"));
        MultipartFile file = org.mockito.Mockito.mock(MultipartFile.class);
        given(file.getSize()).willReturn(10L);

        given(userService.getUserByUsername("tester")).willReturn(user);
        given(userMissionRepository.findByUser(user)).willReturn(Optional.of(userMission));
        given(recordRepository.findByUserMission(userMission)).willReturn(Optional.empty());
        given(storageService.uploadRecord(eq(user.getId()), eq(file))).willReturn("key");
        given(recordRepository.save(any(Record.class))).willAnswer(invocation -> {
            Record record = invocation.getArgument(0);
            ReflectionTestUtils.setField(record, "id", 1L);
            return record;
        });
        given(storageService.generatePresignedUrl("key")).willReturn("url");

        RecordResponse response = recordService.saveRecord(file, "tester");

        assertThat(response.recordId()).isEqualTo(1L);
        assertThat(response.playbackUrl()).isEqualTo("url");
    }

    @DisplayName("기존 음성 기록이 있으면 덮어쓰고 이전 파일을 삭제한다")
    @Test
    void saveRecordUpdatesExisting() {
        User user = createUser("tester");
        UserMission userMission = UserMission.create(user, Mission.create("m1"));
        MultipartFile file = org.mockito.Mockito.mock(MultipartFile.class);
        given(file.getSize()).willReturn(20L);

        Record existing = Record.create(userMission, "old", 5L);

        given(userService.getUserByUsername("tester")).willReturn(user);
        given(userMissionRepository.findByUser(user)).willReturn(Optional.of(userMission));
        given(recordRepository.findByUserMission(userMission)).willReturn(Optional.of(existing));
        given(storageService.uploadRecord(user.getId(), file)).willReturn("new");
        given(storageService.generatePresignedUrl("new")).willReturn("url");

        RecordResponse response = recordService.saveRecord(file, "tester");

        assertThat(response.playbackUrl()).isEqualTo("url");
        assertThat(existing.getS3key()).isEqualTo("new");
        verify(storageService).deleteFile(new FileDeleteRequest("old"));
    }

    @DisplayName("음성 기록을 조회한다")
    @Test
    void getRecord() {
        User user = createUser("tester");
        UserMission userMission = UserMission.create(user, Mission.create("m1"));
        Record record = Record.create(userMission, "key", 10L);

        given(userService.getUserByUsername("tester")).willReturn(user);
        given(userMissionRepository.findByUser(user)).willReturn(Optional.of(userMission));
        given(recordRepository.findByUserMission(userMission)).willReturn(Optional.of(record));
        given(storageService.generatePresignedUrl("key")).willReturn("url");

        Optional<RecordResponse> response = recordService.getRecord("tester");

        assertThat(response).isPresent();
        assertThat(response.get().playbackUrl()).isEqualTo("url");
    }

    private User createUser(String username) {
        User user = User.create("nick", username, "pass");
        ReflectionTestUtils.setField(user, "id", 1L);
        return user;
    }
}
