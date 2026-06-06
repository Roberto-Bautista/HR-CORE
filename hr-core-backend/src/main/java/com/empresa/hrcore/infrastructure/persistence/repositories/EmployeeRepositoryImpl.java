package com.empresa.hrcore.infrastructure.persistence.repositories;

import com.empresa.hrcore.domain.entities.Empleado;
import com.empresa.hrcore.domain.entities.PerfilProfesional;
import com.empresa.hrcore.domain.ports.IEmployeeRepository;
import com.empresa.hrcore.infrastructure.persistence.jpa.EmpleadoJpaEntity;
import com.empresa.hrcore.infrastructure.persistence.jpa.EmpleadoJpaRepository;
import com.empresa.hrcore.infrastructure.persistence.jpa.PerfilProfesionalJpaEntity;

import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * ADAPTADOR DE PERSISTENCIA — Implementación del puerto IEmployeeRepository
 *
 * Esta clase es el "puente" entre el mundo del dominio y la base de datos.
 * Convierte entidades de Dominio (Empleado) ↔ entidades JPA (EmpleadoJpaEntity).
 *
 * En Arquitectura Hexagonal, este es un ADAPTADOR CONDUCIDO (Driven Adapter):
 *   - El dominio pide datos a través del puerto (IEmployeeRepository)
 *   - Este adaptador cumple esa petición usando JPA + PostgreSQL
 */
@Component
public class EmployeeRepositoryImpl implements IEmployeeRepository {

    private final EmpleadoJpaRepository jpaRepository;

    public EmployeeRepositoryImpl(EmpleadoJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    // ==========================================================
    // Implementación de los métodos del puerto
    // ==========================================================

    @Override
    public Empleado save(Empleado empleado) {
        EmpleadoJpaEntity jpaEntity = toJpaEntity(empleado);
        EmpleadoJpaEntity saved = jpaRepository.save(jpaEntity);
        return toDomainEntity(saved);
    }

    @Override
    public Optional<Empleado> findById(UUID id) {
        return jpaRepository.findById(id).map(this::toDomainEntity);
    }

    @Override
    public Optional<Empleado> findByCodigo(String codigo) {
        return jpaRepository.findByCodigo(codigo).map(this::toDomainEntity);
    }

    @Override
    public Optional<Empleado> findByEmail(String email) {
        return jpaRepository.findByEmail(email).map(this::toDomainEntity);
    }

    @Override
    public List<Empleado> findAll() {
        return jpaRepository.findAll().stream()
                .map(this::toDomainEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<Empleado> findByStatus(Empleado.EstadoEmpleado status) {
        EmpleadoJpaEntity.Status jpaStatus = EmpleadoJpaEntity.Status.valueOf(status.name());
        return jpaRepository.findByStatus(jpaStatus).stream()
                .map(this::toDomainEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<Empleado> findByDepartamento(String departamento) {
        return jpaRepository.findByDepartamento(departamento).stream()
                .map(this::toDomainEntity)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsByEmail(String email) {
        return jpaRepository.existsByEmail(email);
    }

    @Override
    public boolean existsByCodigo(String codigo) {
        return jpaRepository.existsByCodigo(codigo);
    }

    @Override
    public long count() {
        return jpaRepository.count();
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }

    // ==========================================================
    // MAPEADORES — Conversión entre Dominio ↔ JPA
    //
    // Estos métodos traducen entre las dos "lenguas":
    //   - Empleado (lo que entiende el negocio)
    //   - EmpleadoJpaEntity (lo que entiende PostgreSQL)
    // ==========================================================

    /**
     * Convierte una entidad de DOMINIO a una entidad JPA (para guardar en BD)
     */
    private EmpleadoJpaEntity toJpaEntity(Empleado domain) {
        EmpleadoJpaEntity jpa = new EmpleadoJpaEntity();

        jpa.setId(domain.getId());
        jpa.setCodigo(domain.getCodigo());
        jpa.setNombre(domain.getNombre());
        jpa.setApellido(domain.getApellido());
        jpa.setEmail(domain.getEmail());
        jpa.setTelefono(domain.getTelefono());
        jpa.setGenero(domain.getGenero());
        jpa.setCargo(domain.getCargo());
        jpa.setDepartamento(domain.getDepartamento());
        jpa.setSalario(domain.getSalario());
        jpa.setFechaAlta(domain.getFechaAlta());
        jpa.setFechaCese(domain.getFechaCese());
        jpa.setCreatedAt(domain.getCreatedAt());
        jpa.setUpdatedAt(domain.getUpdatedAt());
        jpa.setCreatedBy(domain.getCreatedBy());
        jpa.setUpdatedBy(domain.getUpdatedBy());

        // Convertir el enum de Dominio al enum de JPA
        if (domain.getStatus() != null) {
            jpa.setStatus(EmpleadoJpaEntity.Status.valueOf(domain.getStatus().name()));
        }

        // Mapear perfil profesional
        if (domain.getPerfilProfesional() != null) {
            PerfilProfesionalJpaEntity perfilJpa = toPerfilJpaEntity(domain.getPerfilProfesional(), jpa);
            jpa.setPerfilProfesional(perfilJpa);
        }

        return jpa;
    }

    /**
     * Convierte una entidad JPA a una entidad de DOMINIO (para devolver al servicio)
     */
    private Empleado toDomainEntity(EmpleadoJpaEntity jpa) {
        Empleado domain = new Empleado();

        domain.setId(jpa.getId());
        domain.setCodigo(jpa.getCodigo());
        domain.setNombre(jpa.getNombre());
        domain.setApellido(jpa.getApellido());
        domain.setEmail(jpa.getEmail());
        domain.setTelefono(jpa.getTelefono());
        domain.setGenero(jpa.getGenero());
        domain.setCargo(jpa.getCargo());
        domain.setDepartamento(jpa.getDepartamento());
        domain.setSalario(jpa.getSalario());
        domain.setFechaAlta(jpa.getFechaAlta());
        domain.setFechaCese(jpa.getFechaCese());
        domain.setCreatedAt(jpa.getCreatedAt());
        domain.setUpdatedAt(jpa.getUpdatedAt());
        domain.setCreatedBy(jpa.getCreatedBy());
        domain.setUpdatedBy(jpa.getUpdatedBy());

        // Convertir el enum de JPA al enum de Dominio
        if (jpa.getStatus() != null) {
            domain.setStatus(Empleado.EstadoEmpleado.valueOf(jpa.getStatus().name()));
        }

        // Mapear perfil profesional
        if (jpa.getPerfilProfesional() != null) {
            domain.setPerfilProfesional(toPerfilDomain(jpa.getPerfilProfesional()));
        }

        return domain;
    }

    private PerfilProfesionalJpaEntity toPerfilJpaEntity(PerfilProfesional domain, EmpleadoJpaEntity empleadoJpa) {
        PerfilProfesionalJpaEntity jpa = new PerfilProfesionalJpaEntity();
        jpa.setId(domain.getId());
        jpa.setEmpleado(empleadoJpa);
        jpa.setNivelEducativo(domain.getNivelEducativo());
        jpa.setEspecialidad(domain.getEspecialidad());
        jpa.setLinkedinUrl(domain.getLinkedinUrl());
        jpa.setUpdatedAt(domain.getUpdatedAt());

        if (domain.getHabilidades() != null) {
            jpa.setHabilidades(domain.getHabilidades().toArray(new String[0]));
        }
        if (domain.getCertificaciones() != null) {
            jpa.setCertificaciones(domain.getCertificaciones().toArray(new String[0]));
        }

        return jpa;
    }

    private PerfilProfesional toPerfilDomain(PerfilProfesionalJpaEntity jpa) {
        PerfilProfesional domain = new PerfilProfesional();
        domain.setId(jpa.getId());
        domain.setEmpleadoId(jpa.getEmpleado().getId());
        domain.setNivelEducativo(jpa.getNivelEducativo());
        domain.setEspecialidad(jpa.getEspecialidad());
        domain.setLinkedinUrl(jpa.getLinkedinUrl());
        domain.setUpdatedAt(jpa.getUpdatedAt());

        if (jpa.getHabilidades() != null) {
            domain.setHabilidades(Arrays.asList(jpa.getHabilidades()));
        }
        if (jpa.getCertificaciones() != null) {
            domain.setCertificaciones(Arrays.asList(jpa.getCertificaciones()));
        }

        return domain;
    }
}
