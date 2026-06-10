package com.empresa.hrcore.application.services;

import com.empresa.hrcore.application.usecases.BenefitsUseCase;
import com.empresa.hrcore.domain.entities.Empleado;
import com.empresa.hrcore.domain.entities.Beneficio;
import com.empresa.hrcore.domain.entities.Enrolamiento;
import com.empresa.hrcore.domain.exceptions.EmpleadoNoElegibleException;
import com.empresa.hrcore.domain.patterns.strategy.benefits.ElegibilidadComposite;
import com.empresa.hrcore.domain.ports.IBenefitsRepository;
import com.empresa.hrcore.domain.ports.IEmployeeRepository;
import com.empresa.hrcore.presentation.exceptions.ConflictException;
import com.empresa.hrcore.presentation.exceptions.ResourceNotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * SERVICIO DE APLICACIÓN — Gestión de Beneficios
 */
@Service
@Transactional
public class BenefitsService implements BenefitsUseCase {

    private static final Logger log = LoggerFactory.getLogger(BenefitsService.class);

    private final IBenefitsRepository benefitsRepository;
    private final IEmployeeRepository employeeRepository;
    private final ElegibilidadComposite elegibilidadComposite;

    public BenefitsService(IBenefitsRepository benefitsRepository,
                           IEmployeeRepository employeeRepository) {
        this.benefitsRepository = benefitsRepository;
        this.employeeRepository = employeeRepository;
        this.elegibilidadComposite = new ElegibilidadComposite();
    }

    @Override
    public Beneficio crearBeneficio(Beneficio beneficio) {
        log.info("Creando nuevo beneficio: {}", beneficio.getNombre());
        if (benefitsRepository.existsByNombre(beneficio.getNombre())) {
            throw new ConflictException("Beneficio", "nombre", beneficio.getNombre());
        }
        return benefitsRepository.save(beneficio);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Beneficio> obtenerPorId(UUID id) {
        return benefitsRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Beneficio> listarCatalogo() {
        return benefitsRepository.findAll();
    }

    @Override
    public Enrolamiento enrolar(UUID empleadoId, UUID beneficioId) {
        log.info("Iniciando enrolamiento de empleado {} en beneficio {}", empleadoId, beneficioId);

        // 1. Obtener el empleado
        Empleado empleado = employeeRepository.findById(empleadoId)
                .orElseThrow(() -> new ResourceNotFoundException("Empleado", empleadoId));

        if (!empleado.estaActivo()) {
            throw new EmpleadoNoElegibleException("El colaborador no se encuentra activo.");
        }

        // 2. Obtener el beneficio
        Beneficio beneficio = benefitsRepository.findById(beneficioId)
                .orElseThrow(() -> new ResourceNotFoundException("Beneficio", beneficioId));

        // 3. Validar si ya está enrolado
        if (benefitsRepository.existsEnrollmentByEmpleadoIdAndBeneficioId(empleadoId, beneficioId)) {
            throw new ConflictException("El colaborador ya se encuentra afiliado a este beneficio.");
        }

        // 4. Evaluar elegibilidad con el Patrón Strategy (Composite)
        boolean eligible = elegibilidadComposite.esElegible(empleado, beneficio);
        if (!eligible) {
            String razon = construirMensajeNoElegible(empleado, beneficio);
            throw new EmpleadoNoElegibleException(razon);
        }

        // 5. Crear y guardar enrolamiento
        Enrolamiento enrolamiento = new Enrolamiento(empleadoId, beneficioId);
        Enrolamiento guardado = benefitsRepository.saveEnrollment(enrolamiento);
        
        log.info("Enrolamiento exitoso. ID: {}", guardado.getId());
        return guardado;
    }

    @Override
    public void desenrolar(UUID enrolamientoId) {
        log.info("Cancelando enrolamiento: {}", enrolamientoId);
        benefitsRepository.findEnrollmentById(enrolamientoId)
                .orElseThrow(() -> new ResourceNotFoundException("Enrolamiento", enrolamientoId));
        benefitsRepository.deleteEnrollmentById(enrolamientoId);
        log.info("Enrolamiento cancelado exitosamente.");
    }

    @Override
    @Transactional(readOnly = true)
    public List<Enrolamiento> listarEnrolamientosEmpleado(UUID empleadoId) {
        // Verificar que el empleado existe
        if (employeeRepository.findById(empleadoId).isEmpty()) {
            throw new ResourceNotFoundException("Empleado", empleadoId);
        }
        return benefitsRepository.findEnrollmentsByEmpleadoId(empleadoId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean esElegible(UUID empleadoId, UUID beneficioId) {
        Empleado empleado = employeeRepository.findById(empleadoId).orElse(null);
        Beneficio beneficio = benefitsRepository.findById(beneficioId).orElse(null);

        if (empleado == null || beneficio == null || !empleado.estaActivo()) {
            return false;
        }

        return elegibilidadComposite.esElegible(empleado, beneficio);
    }

    // ==========================================================
    // MÉTODOS AUXILIARES
    // ==========================================================

    private String construirMensajeNoElegible(Empleado emp, Beneficio ben) {
        StringBuilder sb = new StringBuilder();
        if (ben.getRequiereCargo() != null && !ben.getRequiereCargo().isBlank() && 
            (emp.getCargo() == null || !emp.getCargo().equalsIgnoreCase(ben.getRequiereCargo()))) {
            sb.append("Requiere cargo '").append(ben.getRequiereCargo()).append("' (actual: '").append(emp.getCargo()).append("'). ");
        }
        if (ben.getRequiereSalarioMax() != null && 
            (emp.getSalario() == null || emp.getSalario().compareTo(ben.getRequiereSalarioMax()) > 0)) {
            sb.append("Requiere salario máximo de S/.").append(ben.getRequiereSalarioMax()).append(" (actual: S/.").append(emp.getSalario()).append("). ");
        }
        if (ben.getRequiereSalarioMin() != null && 
            (emp.getSalario() == null || emp.getSalario().compareTo(ben.getRequiereSalarioMin()) < 0)) {
            sb.append("Requiere salario mínimo de S/.").append(ben.getRequiereSalarioMin()).append(" (actual: S/.").append(emp.getSalario()).append("). ");
        }
        if (ben.getRequiereAntiguedadMeses() != null) {
            java.time.LocalDate alta = emp.getFechaAlta();
            long meses = alta == null ? 0 : java.time.temporal.ChronoUnit.MONTHS.between(alta, java.time.LocalDate.now());
            if (meses < ben.getRequiereAntiguedadMeses()) {
                sb.append("Requiere ").append(ben.getRequiereAntiguedadMeses()).append(" meses de antigüedad (actual: ").append(meses).append(" meses). ");
            }
        }
        return sb.toString().trim();
    }
}
