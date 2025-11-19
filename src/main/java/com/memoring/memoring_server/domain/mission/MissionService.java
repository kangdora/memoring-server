package com.memoring.memoring_server.domain.mission;

import com.memoring.memoring_server.domain.mission.dto.MissionOptionResponseDto;
import com.memoring.memoring_server.domain.mission.dto.MissionSelectRequestDto;
import com.memoring.memoring_server.domain.mission.dto.MissionSelectResponseDto;
import com.memoring.memoring_server.domain.user.User;
import com.memoring.memoring_server.domain.user.UserService;
import com.memoring.memoring_server.global.exception.MissionNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MissionService {

    private final MissionRepository missionRepository;
    private final UserMissionRepository userMissionRepository;
    private final UserService userService;

    public List<MissionOptionResponseDto> getMissionOptions() {
        return missionRepository.findAll().stream()
                .map(mission -> new MissionOptionResponseDto(
                        mission.getId(),
                        mission.getContent()
                ))
                .toList();
    }

    @Transactional
    public MissionSelectResponseDto selectMission(MissionSelectRequestDto dto, String username) {
        User user = userService.getUserByUsername(username);

        Mission mission = missionRepository.findById(dto.missionId())
                .orElseThrow(MissionNotFoundException::new);

        UserMission userMission = userMissionRepository.findByUser(user)
                .map(existing -> {
                    existing.updateMission(mission);
                    return existing;
                })
                .orElseGet(() -> UserMission.create(user, mission));

        UserMission saved = userMissionRepository.save(userMission);
        return new MissionSelectResponseDto(saved.getId(), mission.getContent());
    }

    @Transactional
    public void cancelMission(String username) {
        User user = userService.getUserByUsername(username);

        UserMission userMission = userMissionRepository.findByUser(user)
                .orElseThrow(MissionNotFoundException::new);

        userMission.clearMission();
    }
}