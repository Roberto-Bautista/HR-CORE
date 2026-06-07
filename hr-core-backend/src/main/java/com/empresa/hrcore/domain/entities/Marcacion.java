package com.empresa.hrcore.domain.entities;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * ENTIDAD DE DOMINIO - Marcacion
 * Representa un evento de registro de tiempo de un empleado (entrada o salida).
 */
public class Marcacion {

    private UUID id;
    private UUID empleadoId;
    private TipoMarcacion tipo;
    private LocalDateTime timestamp;

    public enum TipoMarcacion {
        ENTRADA, SALIDA
    }

    public Marcacion() {
    }

    public Marcacion(UUID empleadoId, TipoMarcacion tipo, LocalDateTime timestamp) {
        this.id = UUID.randomUUID();
        this.empleadoId = empleadoId;
        this.tipo = tipo;
        this.timestamp = timestamp;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getEmpleadoId() {
        return empleadoId;
    }

    public void setEmpleadoId(UUID empleadoId) {
        this.empleadoId = empleadoId;
    }

    public TipoMarcacion getTipo() {
        return tipo;
    }

    public void setTipo(TipoMarcacion tipo) {
        this.tipo = tipo;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
