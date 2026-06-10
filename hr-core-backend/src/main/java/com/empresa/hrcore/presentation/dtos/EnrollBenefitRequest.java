package com.empresa.hrcore.presentation.dtos;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

/**
 * DTO — Solicitud de enrolamiento.
 */
public class EnrollBenefitRequest {

    @NotNull(message = "El ID del empleado es obligatorio.")
    private UUID employeeId;

    @NotNull(message = "El ID del beneficio es obligatorio.")
    private UUID benefitId;

    // ==========================================================
    // Getters y Setters
    // ==========================================================

    public UUID getEmployeeId() { return employeeId; }
    public void setEmployeeId(UUID employeeId) { this.employeeId = employeeId; }

    public UUID getBenefitId() { return benefitId; }
    public void setBenefitId(UUID benefitId) { this.benefitId = benefitId; }
}
