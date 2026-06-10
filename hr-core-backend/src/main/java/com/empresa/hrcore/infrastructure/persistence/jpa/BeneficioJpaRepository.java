package com.empresa.hrcore.infrastructure.persistence.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * REPOSITORIO JPA — Beneficios
 */
@Repository
public interface BeneficioJpaRepository extends JpaRepository<BeneficioJpaEntity, UUID> {

    Optional<BeneficioJpaEntity> findByNombre(String nombre);
    
    boolean existsByNombre(String nombre);
}
