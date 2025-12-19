package com.memoring.memoring_server.domain.caregiver;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CareRelationRepository extends JpaRepository<CareRelation, Long> {
    boolean existsByPatientIdAndCaregiverId(Long patientId, Long caregiverId);
    List<CareRelation> findByCaregiverId(Long caregiverId);
}
