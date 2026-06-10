package com.empresa.hrcore.infrastructure.persistence.repositories;

import com.empresa.hrcore.domain.entities.Beneficio;
import com.empresa.hrcore.domain.entities.Enrolamiento;
import com.empresa.hrcore.domain.ports.IBenefitsRepository;
import com.empresa.hrcore.infrastructure.persistence.jpa.BeneficioJpaEntity;
import com.empresa.hrcore.infrastructure.persistence.jpa.BeneficioJpaRepository;
import com.empresa.hrcore.infrastructure.persistence.jpa.EnrolamientoJpaEntity;
import com.empresa.hrcore.infrastructure.persistence.jpa.EnrolamientoJpaRepository;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * ADAPTADOR DE PERSISTENCIA — Implementación del puerto IBenefitsRepository
 */
@Component
public class BenefitsRepositoryImpl implements IBenefitsRepository {

    private final BeneficioJpaRepository beneficioJpaRepository;
    private final EnrolamientoJpaRepository enrolamientoJpaRepository;

    public BenefitsRepositoryImpl(BeneficioJpaRepository beneficioJpaRepository,
                                  EnrolamientoJpaRepository enrolamientoJpaRepository) {
        this.beneficioJpaRepository = beneficioJpaRepository;
        this.enrolamientoJpaRepository = enrolamientoJpaRepository;
    }

    // ==========================================================
    // MÉTODOS DE BENEFICIOS
    // ==========================================================

    @Override
    public Beneficio save(Beneficio beneficio) {
        BeneficioJpaEntity jpaEntity = toJpaEntity(beneficio);
        BeneficioJpaEntity saved = beneficioJpaRepository.save(jpaEntity);
        return toDomainEntity(saved);
    }

    @Override
    public Optional<Beneficio> findById(UUID id) {
        return beneficioJpaRepository.findById(id).map(this::toDomainEntity);
    }

    @Override
    public Optional<Beneficio> findByNombre(String nombre) {
        return beneficioJpaRepository.findByNombre(nombre).map(this::toDomainEntity);
    }

    @Override
    public List<Beneficio> findAll() {
        return beneficioJpaRepository.findAll().stream()
                .map(this::toDomainEntity)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsByNombre(String nombre) {
        return beneficioJpaRepository.existsByNombre(nombre);
    }

    // ==========================================================
    // MÉTODOS DE ENROLAMIENTO
    // ==========================================================

    @Override
    public Enrolamiento saveEnrollment(Enrolamiento enrolamiento) {
        EnrolamientoJpaEntity jpaEntity = toJpaEntity(enrolamiento);
        EnrolamientoJpaEntity saved = enrolamientoJpaRepository.save(jpaEntity);
        return toDomainEntity(saved);
    }

    @Override
    public Optional<Enrolamiento> findEnrollmentById(UUID id) {
        return enrolamientoJpaRepository.findById(id).map(this::toDomainEntity);
    }

    @Override
    public Optional<Enrolamiento> findEnrollmentByEmpleadoIdAndBeneficioId(UUID empleadoId, UUID beneficioId) {
        return enrolamientoJpaRepository.findByEmployeeIdAndBeneficioId(empleadoId, beneficioId)
                .map(this::toDomainEntity);
    }

    @Override
    public List<Enrolamiento> findEnrollmentsByEmpleadoId(UUID empleadoId) {
        return enrolamientoJpaRepository.findByEmployeeId(empleadoId).stream()
                .map(this::toDomainEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<Enrolamiento> findEnrollmentsByBeneficioId(UUID beneficioId) {
        return enrolamientoJpaRepository.findByBeneficioId(beneficioId).stream()
                .map(this::toDomainEntity)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteEnrollmentById(UUID id) {
        enrolamientoJpaRepository.deleteById(id);
    }

    @Override
    public boolean existsEnrollmentByEmpleadoIdAndBeneficioId(UUID empleadoId, UUID beneficioId) {
        return enrolamientoJpaRepository.existsByEmployeeIdAndBeneficioId(empleadoId, beneficioId);
    }

    // ==========================================================
    // MAPEADORES
    // ==========================================================

    private BeneficioJpaEntity toJpaEntity(Beneficio domain) {
        if (domain == null) return null;
        BeneficioJpaEntity jpa = new BeneficioJpaEntity();
        jpa.setId(domain.getId());
        jpa.setNombre(domain.getNombre());
        jpa.setDescripcion(domain.getDescripcion());
        jpa.setCosto(domain.getCosto());
        jpa.setActivo(domain.isActivo());
        jpa.setRequiereSalarioMin(domain.getRequiereSalarioMin());
        jpa.setRequiereSalarioMax(domain.getRequiereSalarioMax());
        jpa.setRequiereAntiguedadMeses(domain.getRequiereAntiguedadMeses());
        jpa.setRequiereCargo(domain.getRequiereCargo());
        jpa.setCreatedAt(domain.getCreatedAt());
        jpa.setUpdatedAt(domain.getUpdatedAt());
        return jpa;
    }

    private Beneficio toDomainEntity(BeneficioJpaEntity jpa) {
        if (jpa == null) return null;
        Beneficio domain = new Beneficio();
        domain.setId(jpa.getId());
        domain.setNombre(jpa.getNombre());
        domain.setDescripcion(jpa.getDescripcion());
        domain.setCosto(jpa.getCosto());
        domain.setActivo(jpa.isActivo());
        domain.setRequiereSalarioMin(jpa.getRequiereSalarioMin());
        domain.setRequiereSalarioMax(jpa.getRequiereSalarioMax());
        domain.setRequiereAntiguedadMeses(jpa.getRequiereAntiguedadMeses());
        domain.setRequiereCargo(jpa.getRequiereCargo());
        domain.setCreatedAt(jpa.getCreatedAt());
        domain.setUpdatedAt(jpa.getUpdatedAt());
        return domain;
    }

    private EnrolamientoJpaEntity toJpaEntity(Enrolamiento domain) {
        if (domain == null) return null;
        EnrolamientoJpaEntity jpa = new EnrolamientoJpaEntity();
        jpa.setId(domain.getId());
        jpa.setEmployeeId(domain.getEmployeeId());
        jpa.setFechaEnrolamiento(domain.getFechaEnrolamiento());
        jpa.setEstado(domain.getEstado());
        jpa.setCreatedAt(domain.getCreatedAt());

        if (domain.getBenefitId() != null) {
            BeneficioJpaEntity benefitRef = beneficioJpaRepository.findById(domain.getBenefitId()).orElse(null);
            jpa.setBeneficio(benefitRef);
        }

        return jpa;
    }

    private Enrolamiento toDomainEntity(EnrolamientoJpaEntity jpa) {
        if (jpa == null) return null;
        Enrolamiento domain = new Enrolamiento();
        domain.setId(jpa.getId());
        domain.setEmployeeId(jpa.getEmployeeId());
        domain.setBenefitId(jpa.getBeneficio() != null ? jpa.getBeneficio().getId() : null);
        domain.setFechaEnrolamiento(jpa.getFechaEnrolamiento());
        domain.setEstado(jpa.getEstado());
        domain.setCreatedAt(jpa.getCreatedAt());

        if (jpa.getBeneficio() != null) {
            domain.setBeneficio(toDomainEntity(jpa.getBeneficio()));
        }

        return domain;
    }
}
