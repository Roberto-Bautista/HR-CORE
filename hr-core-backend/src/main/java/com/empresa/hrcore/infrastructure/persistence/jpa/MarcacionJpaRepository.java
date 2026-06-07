package com.empresa.hrcore.infrastructure.persistence.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface MarcacionJpaRepository extends JpaRepository<MarcacionJpaEntity, UUID> {
    List<MarcacionJpaEntity> findByEmpleadoIdAndTimestampBetweenOrderByTimestampAsc(UUID empleadoId, LocalDateTime start, LocalDateTime end);
}
