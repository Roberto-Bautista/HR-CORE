package com.empresa.hrcore.infrastructure.persistence.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * REPOSITORIO JPA — Enrolamiento de Beneficios
 */
@Repository
public interface EnrolamientoJpaRepository extends JpaRepository<EnrolamientoJpaEntity, UUID> {

    Optional<EnrolamientoJpaEntity> findByEmployeeIdAndBeneficioId(UUID employeeId, UUID benefitId);

    List<EnrolamientoJpaEntity> findByEmployeeId(UUID employeeId);

    List<EnrolamientoJpaEntity> findByBeneficioId(UUID benefitId);

    boolean existsByEmployeeIdAndBeneficioId(UUID employeeId, UUID benefitId);
}
