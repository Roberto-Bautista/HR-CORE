package com.empresa.hrcore.application.services;

import com.empresa.hrcore.domain.entities.Empleado;
import com.empresa.hrcore.domain.entities.SaldoVacacion;
import com.empresa.hrcore.domain.entities.SolicitudVacacion;
import com.empresa.hrcore.domain.ports.IEmployeeRepository;
import com.empresa.hrcore.infrastructure.persistence.jpa.*;
import com.empresa.hrcore.presentation.exceptions.ResourceNotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * SERVICIO DE APLICACIÓN — Casos de Uso de Vacaciones
 *
 * Orquesta el flujo de solicitudes de vacaciones:
 *   1. Crear solicitud (en BORRADOR)
 *   2. Enviar solicitud (BORRADOR → PENDIENTE_JEFE)
 *   3. Aprobar solicitud (PENDIENTE_JEFE → APROBADA) + descuento de saldo
 *   4. Rechazar solicitud (PENDIENTE_JEFE → RECHAZADA)
 *   5. Eliminar borrador
 *
 * Usa el Patrón State para las transiciones (delegado a SolicitudVacacion).
 */
@Service
@Transactional
public class AbsenceService {

    private static final Logger log = LoggerFactory.getLogger(AbsenceService.class);

    private final SolicitudVacacionJpaRepository solicitudRepository;
    private final SaldoVacacionJpaRepository saldoRepository;
    private final IEmployeeRepository employeeRepository;

    public AbsenceService(SolicitudVacacionJpaRepository solicitudRepository,
                          SaldoVacacionJpaRepository saldoRepository,
                          IEmployeeRepository employeeRepository) {
        this.solicitudRepository = solicitudRepository;
        this.saldoRepository = saldoRepository;
        this.employeeRepository = employeeRepository;
    }

    // ==========================================================
    // CASO DE USO 1: Crear Solicitud (BORRADOR)
    // ==========================================================

    public SolicitudVacacion crearSolicitud(UUID empleadoId, LocalDate fechaInicio, LocalDate fechaFin) {
        log.info("Creando solicitud de vacaciones para empleado: {}", empleadoId);

        // Verificar que el empleado existe y está activo
        Empleado empleado = employeeRepository.findById(empleadoId)
                .orElseThrow(() -> new ResourceNotFoundException("Empleado", empleadoId));

        if (!empleado.estaActivo()) {
            throw new IllegalStateException("No se puede solicitar vacaciones: el empleado no está activo.");
        }

        // Validar fechas
        if (fechaFin.isBefore(fechaInicio)) {
            throw new IllegalArgumentException("La fecha fin no puede ser anterior a la fecha inicio.");
        }
        if (fechaInicio.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("La fecha inicio no puede ser anterior a hoy.");
        }

        // Crear la solicitud en estado BORRADOR (Patrón State)
        SolicitudVacacion solicitud = new SolicitudVacacion(empleadoId, fechaInicio, fechaFin);

        // Verificar que tiene saldo suficiente (validación preventiva)
        SaldoVacacion saldo = obtenerOCrearSaldo(empleadoId, fechaInicio.getYear());
        if (solicitud.getDiasSolicitados() > saldo.getDiasDisponibles()) {
            throw new com.empresa.hrcore.domain.exceptions.SaldoInsuficienteException(
                    solicitud.getDiasSolicitados(), saldo.getDiasDisponibles());
        }

        // Persistir
        SolicitudVacacionJpaEntity jpa = toJpaEntity(solicitud);
        SolicitudVacacionJpaEntity saved = solicitudRepository.save(jpa);

        log.info("Solicitud creada: {} ({} días) en estado BORRADOR", saved.getId(), saved.getDiasSolicitados());

        SolicitudVacacion result = toDomainEntity(saved);
        result.setNombreEmpleado(empleado.getNombre() + " " + empleado.getApellido());
        return result;
    }

    // ==========================================================
    // CASO DE USO 2: Enviar Solicitud (BORRADOR → PENDIENTE_JEFE)
    // ==========================================================

    public SolicitudVacacion enviarSolicitud(UUID solicitudId) {
        log.info("Enviando solicitud: {}", solicitudId);

        SolicitudVacacionJpaEntity jpa = solicitudRepository.findById(solicitudId)
                .orElseThrow(() -> new ResourceNotFoundException("SolicitudVacacion", solicitudId));

        SolicitudVacacion solicitud = toDomainEntity(jpa);
        solicitud.enviar(); // Patrón State: delega la transición

        // Actualizar en BD
        jpa.setEstado(solicitud.getEstadoNombre());
        jpa.setUpdatedAt(LocalDateTime.now());
        solicitudRepository.save(jpa);

        log.info("Solicitud {} enviada → PENDIENTE_JEFE", solicitudId);
        return toDomainEntity(jpa);
    }

    // ==========================================================
    // CASO DE USO 3: Aprobar Solicitud (PENDIENTE_JEFE → APROBADA)
    // ==========================================================

    public SolicitudVacacion aprobarSolicitud(UUID solicitudId) {
        log.info("Aprobando solicitud: {}", solicitudId);

        SolicitudVacacionJpaEntity jpa = solicitudRepository.findById(solicitudId)
                .orElseThrow(() -> new ResourceNotFoundException("SolicitudVacacion", solicitudId));

        SolicitudVacacion solicitud = toDomainEntity(jpa);
        solicitud.aprobar(); // Patrón State

        // Descontar días del saldo
        SaldoVacacion saldo = obtenerOCrearSaldo(jpa.getEmployeeId(), jpa.getFechaInicio().getYear());
        saldo.descontarDias(jpa.getDiasSolicitados());
        guardarSaldo(saldo);

        // Actualizar en BD
        jpa.setEstado(solicitud.getEstadoNombre());
        jpa.setUpdatedAt(LocalDateTime.now());
        solicitudRepository.save(jpa);

        log.info("Solicitud {} aprobada. Saldo restante: {} días", solicitudId, saldo.getDiasDisponibles());
        return toDomainEntity(jpa);
    }

    // ==========================================================
    // CASO DE USO 4: Rechazar Solicitud (PENDIENTE_JEFE → RECHAZADA)
    // ==========================================================

    public SolicitudVacacion rechazarSolicitud(UUID solicitudId, String motivo) {
        log.info("Rechazando solicitud: {} — Motivo: {}", solicitudId, motivo);

        SolicitudVacacionJpaEntity jpa = solicitudRepository.findById(solicitudId)
                .orElseThrow(() -> new ResourceNotFoundException("SolicitudVacacion", solicitudId));

        SolicitudVacacion solicitud = toDomainEntity(jpa);
        solicitud.rechazar(motivo); // Patrón State

        // Actualizar en BD
        jpa.setEstado(solicitud.getEstadoNombre());
        jpa.setMotivoRechazo(motivo);
        jpa.setUpdatedAt(LocalDateTime.now());
        solicitudRepository.save(jpa);

        log.info("Solicitud {} rechazada", solicitudId);
        return toDomainEntity(jpa);
    }

    // ==========================================================
    // CASO DE USO 5: Eliminar Borrador
    // ==========================================================

    public void eliminarBorrador(UUID solicitudId) {
        log.info("Eliminando borrador: {}", solicitudId);

        SolicitudVacacionJpaEntity jpa = solicitudRepository.findById(solicitudId)
                .orElseThrow(() -> new ResourceNotFoundException("SolicitudVacacion", solicitudId));

        if (!"BORRADOR".equals(jpa.getEstado())) {
            throw new IllegalStateException("Solo se pueden eliminar solicitudes en estado BORRADOR.");
        }

        solicitudRepository.delete(jpa);
        log.info("Borrador {} eliminado", solicitudId);
    }

    // ==========================================================
    // CASO DE USO 6: Listar Solicitudes
    // ==========================================================

    @Transactional(readOnly = true)
    public List<SolicitudVacacion> listarPorEmpleado(UUID empleadoId) {
        return solicitudRepository.findByEmployeeIdOrderByCreatedAtDesc(empleadoId)
                .stream()
                .map(this::toDomainEntityConNombre)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<SolicitudVacacion> listarTodas() {
        return solicitudRepository.findAll()
                .stream()
                .map(this::toDomainEntityConNombre)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<SolicitudVacacion> listarPendientes() {
        return solicitudRepository.findByEstadoOrderByCreatedAtDesc("PENDIENTE_JEFE")
                .stream()
                .map(this::toDomainEntityConNombre)
                .collect(Collectors.toList());
    }

    // ==========================================================
    // CASO DE USO 7: Obtener Saldo de Vacaciones
    // ==========================================================

    @Transactional(readOnly = true)
    public SaldoVacacion obtenerSaldo(UUID empleadoId, int anio) {
        return obtenerOCrearSaldo(empleadoId, anio);
    }

    // ==========================================================
    // CASO DE USO 8: Estadísticas
    // ==========================================================

    @Transactional(readOnly = true)
    public long contarPendientes() {
        return solicitudRepository.countByEstado("PENDIENTE_JEFE");
    }

    @Transactional(readOnly = true)
    public long contarAprobadas() {
        return solicitudRepository.countByEstado("APROBADA");
    }

    @Transactional(readOnly = true)
    public long contarRechazadas() {
        return solicitudRepository.countByEstado("RECHAZADA");
    }

    // ==========================================================
    // MÉTODOS PRIVADOS — Saldo
    // ==========================================================

    /**
     * Obtiene el saldo de un empleado para un año. Si no existe, lo crea con 30 días.
     */
    private SaldoVacacion obtenerOCrearSaldo(UUID empleadoId, int anio) {
        return saldoRepository.findByEmployeeIdAndAnio(empleadoId, anio)
                .map(this::toSaldoDomain)
                .orElseGet(() -> {
                    SaldoVacacion nuevo = new SaldoVacacion(empleadoId, anio);
                    guardarSaldo(nuevo);
                    return nuevo;
                });
    }

    private void guardarSaldo(SaldoVacacion saldo) {
        SaldoVacacionJpaEntity jpa = new SaldoVacacionJpaEntity();
        jpa.setId(saldo.getId());
        jpa.setEmployeeId(saldo.getEmpleadoId());
        jpa.setAnio(saldo.getAnio());
        jpa.setDiasTotales(saldo.getDiasTotales());
        jpa.setDiasUsados(saldo.getDiasUsados());
        saldoRepository.save(jpa);
    }

    // ==========================================================
    // MAPEADORES — Dominio ↔ JPA
    // ==========================================================

    private SolicitudVacacionJpaEntity toJpaEntity(SolicitudVacacion domain) {
        SolicitudVacacionJpaEntity jpa = new SolicitudVacacionJpaEntity();
        jpa.setId(domain.getId());
        jpa.setEmployeeId(domain.getEmpleadoId());
        jpa.setFechaInicio(domain.getFechaInicio());
        jpa.setFechaFin(domain.getFechaFin());
        jpa.setDiasSolicitados(domain.getDiasSolicitados());
        jpa.setEstado(domain.getEstadoNombre());
        jpa.setMotivoRechazo(domain.getMotivoRechazo());
        jpa.setCreatedAt(domain.getCreatedAt());
        jpa.setUpdatedAt(domain.getUpdatedAt());
        return jpa;
    }

    private SolicitudVacacion toDomainEntity(SolicitudVacacionJpaEntity jpa) {
        SolicitudVacacion domain = new SolicitudVacacion();
        domain.setId(jpa.getId());
        domain.setEmpleadoId(jpa.getEmployeeId());
        domain.setFechaInicio(jpa.getFechaInicio());
        domain.setFechaFin(jpa.getFechaFin());
        domain.setDiasSolicitados(jpa.getDiasSolicitados());
        domain.setEstadoNombre(jpa.getEstado());
        domain.setEstado(SolicitudVacacion.estadoDesdeNombre(jpa.getEstado()));
        domain.setMotivoRechazo(jpa.getMotivoRechazo());
        domain.setCreatedAt(jpa.getCreatedAt());
        domain.setUpdatedAt(jpa.getUpdatedAt());
        return domain;
    }

    /**
     * Convierte a dominio y agrega el nombre del empleado (para listados).
     */
    private SolicitudVacacion toDomainEntityConNombre(SolicitudVacacionJpaEntity jpa) {
        SolicitudVacacion domain = toDomainEntity(jpa);
        employeeRepository.findById(jpa.getEmployeeId())
                .ifPresent(emp -> domain.setNombreEmpleado(emp.getNombre() + " " + emp.getApellido()));
        return domain;
    }

    private SaldoVacacion toSaldoDomain(SaldoVacacionJpaEntity jpa) {
        SaldoVacacion domain = new SaldoVacacion();
        domain.setId(jpa.getId());
        domain.setEmpleadoId(jpa.getEmployeeId());
        domain.setAnio(jpa.getAnio());
        domain.setDiasTotales(jpa.getDiasTotales());
        domain.setDiasUsados(jpa.getDiasUsados());
        return domain;
    }
}
