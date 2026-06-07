package com.empresa.hrcore.infrastructure.persistence.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * REPOSITORIO SPRING DATA JPA — Historial de Cambios
 *
 * Spring genera automáticamente la consulta SQL:
 *   findByEmployeeIdOrderByFechaDesc →
 *   SELECT * FROM employee_history WHERE employee_id = ? ORDER BY fecha DESC
 */
@Repository
public interface EmployeeHistoryJpaRepository extends JpaRepository<EmployeeHistoryJpaEntity, UUID> {

    List<EmployeeHistoryJpaEntity> findByEmployeeIdOrderByFechaDesc(UUID employeeId);
}
