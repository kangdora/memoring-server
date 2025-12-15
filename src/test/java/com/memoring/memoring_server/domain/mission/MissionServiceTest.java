package com.memoring.memoring_server.domain.mission;

import com.memoring.memoring_server.domain.mission.dto.AdminMissionCreateRequest;
import com.memoring.memoring_server.domain.mission.dto.MissionSelectRequest;
import com.memoring.memoring_server.domain.mission.exception.MissionNotFoundException;
import com.memoring.memoring_server.domain.user.User;
import com.memoring.memoring_server.domain.user.UserService;
import com.memoring.memoring_server.global.exception.InvalidAdminRequestException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class MissionServiceTest {

    @Mock
    private MissionRepository missionRepository;

    @Mock
    private UserMissionRepository userMissionRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private MissionService missionService;

    @DisplayName("관리자 미션 생성 요청이 비어있으면 예외가 발생한다")
    @Test
    void createMissionFailsWhenInvalid() {
        assertThatThrownBy(() -> missionService.createMission(new AdminMissionCreateRequest("")))
                .isInstanceOf(InvalidAdminRequestException.class);
    }

    @DisplayName("관리자 미션을 생성한다")
    @Test
    void createMission() {
        Mission mission = Mission.create("content");
        given(missionRepository.save(any(Mission.class))).willReturn(mission);

        var response = missionService.createMission(new AdminMissionCreateRequest("content"));

        assertThat(response.content()).isEqualTo("content");
        verify(missionRepository).save(any(Mission.class));
    }

    @DisplayName("사용자의 선택된 미션을 조회한다")
    @Test
    void getSelectedMission() {
        User user = User.create("nick", "tester", "pass");
        Mission mission = Mission.create("content");
        UserMission userMission = UserMission.create(user, mission);

        given(userService.getUserByUsername("tester")).willReturn(user);
        given(userMissionRepository.findByUser(user)).willReturn(Optional.of(userMission));

        var response = missionService.getSelectedMission("tester");

        assertThat(response.missionId()).isEqualTo(mission.getId());
        assertThat(response.content()).isEqualTo("content");
    }

    @DisplayName("미션이 없으면 선택 조회 시 예외가 발생한다")
    @Test
    void getSelectedMissionFailsWhenMissing() {
        User user = User.create("nick", "tester", "pass");
        given(userService.getUserByUsername("tester")).willReturn(user);
        given(userMissionRepository.findByUser(user)).willReturn(Optional.empty());

        assertThatThrownBy(() -> missionService.getSelectedMission("tester"))
                .isInstanceOf(MissionNotFoundException.class);
    }

    @DisplayName("미션을 선택하면 기존 미션을 업데이트하거나 새로 생성한다")
    @Test
    void selectMission() {
        User user = User.create("nick", "tester", "pass");
        Mission mission = Mission.create("content");
        MissionSelectRequest request = new MissionSelectRequest(1L);

        given(userService.getUserByUsername("tester")).willReturn(user);
        given(missionRepository.findById(1L)).willReturn(Optional.of(mission));
        given(userMissionRepository.findByUser(user)).willReturn(Optional.empty());
        given(userMissionRepository.save(any(UserMission.class))).willAnswer(invocation -> invocation.getArgument(0));

        var response = missionService.selectMission(request, "tester");

        assertThat(response.missionId()).isEqualTo(mission.getId());
        verify(userMissionRepository).save(any(UserMission.class));
    }

    @DisplayName("미션을 취소하면 유저 미션을 비운다")
    @Test
    void cancelMission() {
        User user = User.create("nick", "tester", "pass");
        UserMission userMission = UserMission.create(user, Mission.create("content"));

        given(userService.getUserByUsername("tester")).willReturn(user);
        given(userMissionRepository.findByUser(user)).willReturn(Optional.of(userMission));

        missionService.cancelMission("tester");

        assertThat(userMission.getMission()).isNull();
    }

    @DisplayName("취소 대상 미션이 없으면 예외가 발생한다")
    @Test
    void cancelMissionFailsWhenMissing() {
        User user = User.create("nick", "tester", "pass");
        given(userService.getUserByUsername("tester")).willReturn(user);
        given(userMissionRepository.findByUser(user)).willReturn(Optional.empty());

        assertThatThrownBy(() -> missionService.cancelMission("tester"))
                .isInstanceOf(MissionNotFoundException.class);
    }

    @DisplayName("미션 리스트를 반환한다")
    @Test
    void getMissionOptions() {
        Mission mission1 = Mission.create("m1");
        Mission mission2 = Mission.create("m2");
        given(missionRepository.findAll()).willReturn(List.of(mission1, mission2));

        var response = missionService.getMissionOptions();

        assertThat(response.missions()).hasSize(2);
    }
}
