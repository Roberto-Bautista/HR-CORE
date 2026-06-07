package com.empresa.hrcore.infrastructure.persistence.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;
import java.util.UUID;

/**
 * REPOSITORIO JPA — Saldos de Vacaciones
 */
@Repository
public interface SaldoVacacionJpaRepository extends JpaRepository<SaldoVacacionJpaEntity, UUID> {

    /** Busca el saldo de un empleado para un año específico */
    Optional<SaldoVacacionJpaEntity> findByEmployeeIdAndAnio(UUID employeeId, int anio);

    /** Lista todos los saldos de un empleado */
    List<SaldoVacacionJpaEntity> findByEmployeeId(UUID employeeId);
}
