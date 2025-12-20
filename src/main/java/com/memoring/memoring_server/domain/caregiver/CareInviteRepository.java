package com.memoring.memoring_server.domain.caregiver;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CareInviteRepository extends JpaRepository<CareInvite, Long> {
    Optional<CareInvite> findByCode(String code);
}
