package com.memoring.memoring_server.domain.mission;

import com.memoring.memoring_server.domain.mission.dto.*;
import com.memoring.memoring_server.domain.user.User;
import com.memoring.memoring_server.domain.user.UserService;
import com.memoring.memoring_server.global.exception.InvalidAdminRequestException;
import com.memoring.memoring_server.global.exception.MissionNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MissionService {

    private final MissionRepository missionRepository;
    private final UserMissionRepository userMissionRepository;
    private final UserService userService;

    @Transactional
    public AdminMissionResponse createMission(AdminMissionCreateRequest request) {
        if (request == null || !StringUtils.hasText(request.content())) {
            throw new InvalidAdminRequestException();
        }

        Mission mission = Mission.create(request.content());
        Mission saved = missionRepository.save(mission);
        return new AdminMissionResponse(saved.getId(), saved.getContent());
    }

    public MissionSelectResponse getSelectedMission(String username) {
        UserMission userMission = userMissionRepository.findByUser(userService.getUserByUsername(username))
                .orElseThrow(MissionNotFoundException::new);

        Mission mission = userMission.getMission();
        if (mission == null) {
            throw new MissionNotFoundException();
        }

        return new MissionSelectResponse(userMission.getId(), mission.getContent());
    }

    public List<MissionOptionResponse> getMissionOptions() {
        return missionRepository.findAll().stream()
                .map(mission -> new MissionOptionResponse(
                        mission.getId(),
                        mission.getContent()
                ))
                .toList();
    }

    @Transactional
    public MissionSelectResponse selectMission(MissionSelectRequest request, String username) {
        User user = userService.getUserByUsername(username);

        Mission mission = missionRepository.findById(request.missionId())
                .orElseThrow(MissionNotFoundException::new);

        UserMission userMission = userMissionRepository.findByUser(user)
                .map(existing -> {
                    existing.updateMission(mission);
                    return existing;
                })
                .orElseGet(() -> UserMission.create(user, mission));

        // 이 부분 반환 꼭 해줘야 할까?
        UserMission saved = userMissionRepository.save(userMission);
        return new MissionSelectResponse(saved.getId(), mission.getContent());
    }

    @Transactional
    public void cancelMission(String username) {
        User user = userService.getUserByUsername(username);

        UserMission userMission = userMissionRepository.findByUser(user)
                .orElseThrow(MissionNotFoundException::new);

        userMission.clearMission();
    }
}