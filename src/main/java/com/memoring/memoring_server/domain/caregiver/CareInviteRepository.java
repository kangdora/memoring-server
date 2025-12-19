package com.memoring.memoring_server.domain.caregiver;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CareInviteRepository extends JpaRepository<CareInvite, Long> {
}
