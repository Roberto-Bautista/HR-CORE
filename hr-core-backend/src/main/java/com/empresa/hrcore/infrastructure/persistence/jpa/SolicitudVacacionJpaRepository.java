package com.empresa.hrcore.infrastructure.persistence.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * REPOSITORIO JPA — Solicitudes de Vacaciones
 *
 * Spring Data genera automáticamente las implementaciones
 * de estas queries a partir del nombre del método.
 */
@Repository
public interface SolicitudVacacionJpaRepository extends JpaRepository<SolicitudVacacionJpaEntity, UUID> {

    /** Lista las solicitudes de un empleado, ordenadas por fecha de creación descendente */
    List<SolicitudVacacionJpaEntity> findByEmployeeIdOrderByCreatedAtDesc(UUID employeeId);

    /** Lista solicitudes por estado (para la bandeja del admin) */
    List<SolicitudVacacionJpaEntity> findByEstadoOrderByCreatedAtDesc(String estado);

    /** Cuenta solicitudes por estado */
    long countByEstado(String estado);
}
