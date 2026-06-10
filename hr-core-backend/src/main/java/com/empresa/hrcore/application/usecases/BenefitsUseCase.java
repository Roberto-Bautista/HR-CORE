package com.empresa.hrcore.application.usecases;

import com.empresa.hrcore.domain.entities.Beneficio;
import com.empresa.hrcore.domain.entities.Enrolamiento;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * CASOS DE USO — Módulo de Beneficios
 */
public interface BenefitsUseCase {

    /**
     * Registra un nuevo beneficio en el catálogo.
     */
    Beneficio crearBeneficio(Beneficio beneficio);

    /**
     * Obtiene un beneficio por su ID.
     */
    Optional<Beneficio> obtenerPorId(UUID id);

    /**
     * Retorna todos los beneficios del catálogo.
     */
    List<Beneficio> listarCatalogo();

    /**
     * Afilia a un colaborador en un beneficio específico.
     * Valida reglas de elegibilidad y que no esté ya afiliado.
     */
    Enrolamiento enrolar(UUID empleadoId, UUID beneficioId);

    /**
     * Cancela la afiliación a un beneficio.
     */
    void desenrolar(UUID enrolamientoId);

    /**
     * Lista todas las afiliaciones activas de un empleado.
     */
    List<Enrolamiento> listarEnrolamientosEmpleado(UUID empleadoId);

    /**
     * Evalúa dinámicamente si un empleado es elegible para un beneficio.
     */
    boolean esElegible(UUID empleadoId, UUID beneficioId);
}
