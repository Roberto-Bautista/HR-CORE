package com.empresa.hrcore.presentation.dtos;

import java.time.LocalDate;
import java.util.UUID;

/**
 * DTO — Respuesta de una afiliación (enrolamiento) a beneficio.
 */
public class EnrollmentResponse {

    private UUID id;
    private UUID employeeId;
    private UUID benefitId;
    private LocalDate fechaEnrolamiento;
    private String estado;

    // Beneficio asociado (detalles de sólo lectura)
    private BenefitResponse beneficio;

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

    public BenefitResponse getBeneficio() { return beneficio; }
    public void setBeneficio(BenefitResponse beneficio) { this.beneficio = beneficio; }
}
