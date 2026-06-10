package com.empresa.hrcore.domain.entities;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * ENTIDAD DE DOMINIO — Enrolamiento
 *
 * Representa la afiliación o enrolamiento de un empleado a un beneficio.
 * Es agnóstico a la persistencia.
 */
public class Enrolamiento {

    private UUID id;
    private UUID employeeId; // ID del Empleado (colaborador)
    private UUID benefitId;  // ID del Beneficio

    private LocalDate fechaEnrolamiento;
    private String estado; // "ACTIVO", "INACTIVO"
    private LocalDateTime createdAt;

    // Agregación de sólo lectura para conveniencia en presentación/negocio
    private Beneficio beneficio;

    public Enrolamiento() {}

    public Enrolamiento(UUID id, UUID employeeId, UUID benefitId, LocalDate fechaEnrolamiento, String estado) {
        this.id = id;
        this.employeeId = employeeId;
        this.benefitId = benefitId;
        this.fechaEnrolamiento = fechaEnrolamiento;
        this.estado = estado;
        this.createdAt = LocalDateTime.now();
    }

    public Enrolamiento(UUID employeeId, UUID benefitId) {
        this.id = UUID.randomUUID();
        this.employeeId = employeeId;
        this.benefitId = benefitId;
        this.fechaEnrolamiento = LocalDate.now();
        this.estado = "ACTIVO";
        this.createdAt = LocalDateTime.now();
    }

    // ==========================================================
    // Getters y Setters
    // ==========================================================

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getEmployeeId() { return employeeId; }
    public void setEmployeeId(UUID employeeId) { this.employeeId = employeeId; }

    public UUID getBenefitId() { return benefitId; }
    public void setBenefitId(UUID benefitId) { this.benefitId = benefitId; }

    public LocalDate getFechaEnrolamiento() { return fechaEnrolamiento; }
    public void setFechaEnrolamiento(LocalDate fechaEnrolamiento) { this.fechaEnrolamiento = fechaEnrolamiento; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public Beneficio getBeneficio() { return beneficio; }
    public void setBeneficio(Beneficio beneficio) { this.beneficio = beneficio; }
}
