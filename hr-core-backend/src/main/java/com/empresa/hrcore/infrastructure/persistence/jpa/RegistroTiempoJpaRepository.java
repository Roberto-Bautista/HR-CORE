package com.empresa.hrcore.infrastructure.persistence.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RegistroTiempoJpaRepository extends JpaRepository<RegistroTiempoJpaEntity, UUID> {
    Optional<RegistroTiempoJpaEntity> findByEmpleadoIdAndFecha(UUID empleadoId, LocalDate fecha);
    List<RegistroTiempoJpaEntity> findByEmpleadoIdAndFechaBetweenOrderByFechaAsc(UUID empleadoId, LocalDate desde, LocalDate hasta);
}
