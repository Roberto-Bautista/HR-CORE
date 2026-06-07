package com.empresa.hrcore.presentation.dtos;

import java.util.UUID;

/**
 * DTO de salida — Respuesta de Solicitud de Vacaciones
 */
public class SolicitudVacacionResponseDTO {

    private UUID id;
    private UUID empleadoId;
    private String nombreEmpleado;
    private String fechaInicio;
    private String fechaFin;
    private int diasSolicitados;
    private String estado;
    private String motivoRechazo;
    private String createdAt;
    private String updatedAt;

    // Getters y Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getEmpleadoId() { return empleadoId; }
    public void setEmpleadoId(UUID empleadoId) { this.empleadoId = empleadoId; }

    public String getNombreEmpleado() { return nombreEmpleado; }
    public void setNombreEmpleado(String nombreEmpleado) { this.nombreEmpleado = nombreEmpleado; }

    public String getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(String fechaInicio) { this.fechaInicio = fechaInicio; }

    public String getFechaFin() { return fechaFin; }
    public void setFechaFin(String fechaFin) { this.fechaFin = fechaFin; }

    public int getDiasSolicitados() { return diasSolicitados; }
    public void setDiasSolicitados(int diasSolicitados) { this.diasSolicitados = diasSolicitados; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getMotivoRechazo() { return motivoRechazo; }
    public void setMotivoRechazo(String motivoRechazo) { this.motivoRechazo = motivoRechazo; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }
}
