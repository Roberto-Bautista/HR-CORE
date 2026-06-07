package com.empresa.hrcore.infrastructure.persistence.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * REPOSITORIO SPRING DATA JPA
 *
 * Spring genera automáticamente las consultas SQL
 * a partir de los nombres de los métodos.
 *
 * Ejemplo: findByEmail → SELECT * FROM employees WHERE email = ?
 *
 * Este repositorio opera sobre EmpleadoJpaEntity (la clase de BD),
 * NO sobre Empleado (la clase de dominio).
 */
@Repository
public interface EmpleadoJpaRepository extends JpaRepository<EmpleadoJpaEntity, UUID> {

    Optional<EmpleadoJpaEntity> findByCodigo(String codigo);

    Optional<EmpleadoJpaEntity> findByEmail(String email);

    List<EmpleadoJpaEntity> findByStatus(EmpleadoJpaEntity.Status status);

    List<EmpleadoJpaEntity> findByDepartamento(String departamento);

    boolean existsByEmail(String email);

    boolean existsByCodigo(String codigo);
}
