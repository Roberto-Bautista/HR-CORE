package com.empresa.hrcore.domain.ports;

import com.empresa.hrcore.domain.entities.Beneficio;
import com.empresa.hrcore.domain.entities.Enrolamiento;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * PUERTO DE PERSISTENCIA — IBenefitsRepository
 *
 * Define las operaciones que el dominio requiere para persistir y
 * consultar los beneficios y enrolamientos.
 */
public interface IBenefitsRepository {

    // --- Operaciones de Beneficios ---
    Beneficio save(Beneficio beneficio);
    Optional<Beneficio> findById(UUID id);
    Optional<Beneficio> findByNombre(String nombre);
    List<Beneficio> findAll();
    boolean existsByNombre(String nombre);

    // --- Operaciones de Enrolamiento ---
    Enrolamiento saveEnrollment(Enrolamiento enrolamiento);
    Optional<Enrolamiento> findEnrollmentById(UUID id);
    Optional<Enrolamiento> findEnrollmentByEmpleadoIdAndBeneficioId(UUID empleadoId, UUID beneficioId);
    List<Enrolamiento> findEnrollmentsByEmpleadoId(UUID empleadoId);
    List<Enrolamiento> findEnrollmentsByBeneficioId(UUID beneficioId);
    void deleteEnrollmentById(UUID id);
    boolean existsEnrollmentByEmpleadoIdAndBeneficioId(UUID empleadoId, UUID beneficioId);
}
