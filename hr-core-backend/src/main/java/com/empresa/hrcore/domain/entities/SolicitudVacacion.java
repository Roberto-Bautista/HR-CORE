package com.empresa.hrcore.domain.entities;

import com.empresa.hrcore.domain.patterns.state.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

/**
 * ENTIDAD DE DOMINIO — Solicitud de Vacaciones
 *
 * Usa el PATRÓN STATE para manejar las transiciones de estado.
 * El estado actual decide qué acciones son válidas.
 *
 * Flujo: BORRADOR → PENDIENTE_JEFE → APROBADA / RECHAZADA
 *
 * Esta clase NO tiene anotaciones de JPA (es dominio puro).
 */
public class SolicitudVacacion {

    private UUID id;
    private UUID empleadoId;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private int diasSolicitados;
    private String motivoRechazo;

    // Patrón State: el estado actual controla las transiciones
    private EstadoVacacion estado;
    private String estadoNombre; // Para persistencia

    // Auditoría
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Datos del empleado (para mostrar en listados, se llena en el servicio)
    private String nombreEmpleado;

    // ==========================================================
    // Constructores
    // ==========================================================

    /** Constructor vacío (necesario para reconstrucción desde BD) */
    public SolicitudVacacion() {}

    /** Constructor para CREAR una nueva solicitud */
    public SolicitudVacacion(UUID empleadoId, LocalDate fechaInicio, LocalDate fechaFin) {
        this.id = UUID.randomUUID();
        this.empleadoId = empleadoId;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.diasSolicitados = calcularDias(fechaInicio, fechaFin);
        this.estado = new EstadoBorrador();
        this.estadoNombre = this.estado.getNombre();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // ==========================================================
    // LÓGICA DE NEGOCIO (delegada al Patrón State)
    // ==========================================================

    /**
     * Envía la solicitud para revisión del jefe.
     * Solo válido desde estado BORRADOR.
     */
    public void enviar() {
        this.estado.enviar(this);
        this.estadoNombre = this.estado.getNombre();
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Aprueba la solicitud.
     * Solo válido desde estado PENDIENTE_JEFE.
     */
    public void aprobar() {
        this.estado.aprobar(this);
        this.estadoNombre = this.estado.getNombre();
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Rechaza la solicitud con un motivo.
     * Solo válido desde estado PENDIENTE_JEFE.
     */
    public void rechazar(String motivo) {
        this.estado.rechazar(this, motivo);
        this.estadoNombre = this.estado.getNombre();
        this.updatedAt = LocalDateTime.now();
    }

    // ==========================================================
    // Reconstrucción del estado desde la base de datos
    // ==========================================================

    /**
     * Reconstruye el objeto EstadoVacacion a partir del nombre guardado en BD.
     * Se llama al convertir de JPA a Dominio.
     */
    public static EstadoVacacion estadoDesdeNombre(String nombre) {
        if (nombre == null) return new EstadoBorrador();
        switch (nombre) {
            case "BORRADOR":        return new EstadoBorrador();
            case "PENDIENTE_JEFE":  return new EstadoPendienteJefe();
            case "APROBADA":        return new EstadoAprobada();
            case "RECHAZADA":       return new EstadoRechazada();
            default:                return new EstadoBorrador();
        }
    }

    // ==========================================================
    // Métodos auxiliares
    // ==========================================================

    /** Calcula los días hábiles entre dos fechas (simplificado: días naturales) */
    private int calcularDias(LocalDate inicio, LocalDate fin) {
        return (int) ChronoUnit.DAYS.between(inicio, fin) + 1; // inclusivo
    }

    // ==========================================================
    // Método usado internamente por los estados para cambiar estado
    // ==========================================================

    /**
     * Método usado SOLO por las clases del Patrón State
     * para cambiar el estado interno de la solicitud.
     */
    public void setEstadoInterno(EstadoVacacion nuevoEstado) {
        this.estado = nuevoEstado;
    }

    // ==========================================================
    // Getters y Setters
    // ==========================================================

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getEmpleadoId() { return empleadoId; }
    public void setEmpleadoId(UUID empleadoId) { this.empleadoId = empleadoId; }

    public LocalDate getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(LocalDate fechaInicio) { this.fechaInicio = fechaInicio; }

    public LocalDate getFechaFin() { return fechaFin; }
    public void setFechaFin(LocalDate fechaFin) { this.fechaFin = fechaFin; }

    public int getDiasSolicitados() { return diasSolicitados; }
    public void setDiasSolicitados(int diasSolicitados) { this.diasSolicitados = diasSolicitados; }

    public String getMotivoRechazo() { return motivoRechazo; }
    public void setMotivoRechazo(String motivoRechazo) { this.motivoRechazo = motivoRechazo; }

    public EstadoVacacion getEstado() { return estado; }
    public void setEstado(EstadoVacacion estado) { this.estado = estado; }

    public String getEstadoNombre() { return estadoNombre; }
    public void setEstadoNombre(String estadoNombre) { this.estadoNombre = estadoNombre; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public String getNombreEmpleado() { return nombreEmpleado; }
    public void setNombreEmpleado(String nombreEmpleado) { this.nombreEmpleado = nombreEmpleado; }
}
