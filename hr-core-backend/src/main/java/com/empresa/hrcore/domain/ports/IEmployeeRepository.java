package com.empresa.hrcore.domain.ports;

import com.empresa.hrcore.domain.entities.Empleado;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * PUERTO DE SALIDA — Repositorio de Empleados
 *
 * En Arquitectura Hexagonal, un "Puerto" es una interfaz que define
 * QUÉ necesita el dominio, pero NO CÓMO se implementa.
 *
 * Esta interfaz dice: "Necesito poder guardar y buscar empleados".
 * Quién la implementa (PostgreSQL, MongoDB, un archivo de texto)
 * es decisión de la capa de INFRAESTRUCTURA, no del dominio.
 *
 * Implementación: EmployeeRepositoryImpl.java (infra/persistence/)
 */
public interface IEmployeeRepository {

    /** Guarda un empleado nuevo o actualiza uno existente */
    Empleado save(Empleado empleado);

    /** Busca un empleado por su ID (UUID) */
    Optional<Empleado> findById(UUID id);

    /** Busca un empleado por su código único (EMP-0001) */
    Optional<Empleado> findByCodigo(String codigo);

    /** Busca un empleado por su email */
    Optional<Empleado> findByEmail(String email);

    /** Lista todos los empleados */
    List<Empleado> findAll();

    /** Lista empleados por estado (ACTIVO, INACTIVO, CESADO) */
    List<Empleado> findByStatus(Empleado.EstadoEmpleado status);

    /** Lista empleados por departamento */
    List<Empleado> findByDepartamento(String departamento);

    /** Verifica si ya existe un empleado con ese email */
    boolean existsByEmail(String email);

    /** Verifica si ya existe un empleado con ese código */
    boolean existsByCodigo(String codigo);

    /** Cuenta el total de empleados */
    long count();

    /** Elimina un empleado por su ID */
    void deleteById(UUID id);
}
