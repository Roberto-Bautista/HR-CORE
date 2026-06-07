package com.empresa.hrcore.presentation.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

/**
 * DTO de entrada — Crear Solicitud de Vacaciones
 */
public class SolicitudVacacionRequestDTO {

    @NotNull(message = "El ID del empleado es obligatorio")
    private UUID empleadoId;

    @NotBlank(message = "La fecha de inicio es obligatoria")
    private String fechaInicio;

    @NotBlank(message = "La fecha de fin es obligatoria")
    private String fechaFin;

    // Getters y Setters
    public UUID getEmpleadoId() { return empleadoId; }
    public void setEmpleadoId(UUID empleadoId) { this.empleadoId = empleadoId; }

    public String getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(String fechaInicio) { this.fechaInicio = fechaInicio; }

    public String getFechaFin() { return fechaFin; }
    public void setFechaFin(String fechaFin) { this.fechaFin = fechaFin; }
}
