package com.memoring.memoring_server.domain.caregiver;

import com.memoring.memoring_server.domain.caregiver.dto.CareInviteAcceptRequest;
import com.memoring.memoring_server.domain.caregiver.dto.CaregiverPatientListResponse;
import com.memoring.memoring_server.domain.caregiver.dto.CaregiverPatientResponse;
import com.memoring.memoring_server.domain.caregiver.exception.*;
import com.memoring.memoring_server.domain.user.Role;
import com.memoring.memoring_server.domain.user.User;
import com.memoring.memoring_server.domain.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CareRelationService {

    private final CareInviteRepository careInviteRepository;
    private final CareRelationRepository careRelationRepository;
    private final UserService userService;
    private final Clock clock;

    @Transactional
    public void acceptCareInvite(CareInviteAcceptRequest request, String caregiverUsername) {
        if (request == null || !StringUtils.hasText(request.inviteCode())) {
            throw new CareInviteNotFoundException();
        }

        CareInvite invite = careInviteRepository.findByCode(request.inviteCode())
                .orElseThrow(CareInviteNotFoundException::new);

        LocalDateTime now = LocalDateTime.now(clock);

        if (now.isAfter(invite.getExpiredAt())) {
            throw new CaregiverInviteAlreadyExpiredException();
        }

        User caregiver = userService.getUserByUsername(caregiverUsername);
        validateCaregiverRole(caregiver);

        User patient = userService.getUserById(invite.getPatientId());

        if (careRelationRepository.existsByPatientIdAndCaregiverId(patient.getId(), caregiver.getId())) {
            throw new CareRelationAlreadyExistsException();
        }

        if (Role.CAREGIVER.equals(patient.getRole())) {
            throw new CareRelationAccessDeniedException();
        }

        if (patient.getId().equals(caregiver.getId())) {
            throw new CareRelationAccessDeniedException();
        }

        CareRelation relation = CareRelation.create(patient.getId(), caregiver.getId(), now);
        careRelationRepository.save(relation);
    }

    private void validateCaregiverRole(User caregiver) {
        if (!Role.CAREGIVER.equals(caregiver.getRole())) {
            throw new CaregiverRoleRequiredException();
        }
    }

    public CaregiverPatientListResponse getPatients(String caregiverUsername) {
        User caregiver = userService.getUserByUsername(caregiverUsername);
        validateCaregiverRole(caregiver);

        List<CareRelation> relations = careRelationRepository.findByCaregiverId(caregiver.getId());

        return new CaregiverPatientListResponse(relations
                .stream()
                .map(relation -> userService.getUserById(relation.getPatientId()))
                .map(CaregiverPatientResponse::from)
                .collect(Collectors.toList()));
    }

    public void validateCaregiverAccessToUser(String caregiverUsername, Long patientId) {
        User caregiver = userService.getUserByUsername(caregiverUsername);
        validateCaregiverRole(caregiver);
        if (!careRelationRepository.existsByPatientIdAndCaregiverId(patientId, caregiver.getId())) {
            throw new CareRelationAccessDeniedException();
        }
    }

    public boolean isConnected(Long patientId, Long caregiverId) {
        return careRelationRepository.existsByPatientIdAndCaregiverId(patientId, caregiverId);
    }
}
