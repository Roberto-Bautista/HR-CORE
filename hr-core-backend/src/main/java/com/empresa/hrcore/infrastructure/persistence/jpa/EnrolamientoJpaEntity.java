package com.empresa.hrcore.infrastructure.persistence.jpa;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * ENTIDAD JPA — Enrolamiento
 *
 * Mapea la tabla 'benefit_enrollments' en la base de datos PostgreSQL.
 */
@Entity
@Table(name = "benefit_enrollments", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"employee_id", "benefit_id"})
})
public class EnrolamientoJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "employee_id", nullable = false)
    private UUID employeeId;

    // Relación de tipo Muchos Enrolamientos a Un Beneficio
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "benefit_id", nullable = false)
    private BeneficioJpaEntity beneficio;

    @Column(name = "fecha_enrolamiento", nullable = false)
    private LocalDate fechaEnrolamiento;

    @Column(nullable = false, length = 20)
    private String estado; // "ACTIVO", "INACTIVO"

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    // ==========================================================
    // Getters y Setters
    // ==========================================================

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getEmployeeId() { return employeeId; }
    public void setEmployeeId(UUID employeeId) { this.employeeId = employeeId; }

    public BeneficioJpaEntity getBeneficio() { return beneficio; }
    public void setBeneficio(BeneficioJpaEntity beneficio) { this.beneficio = beneficio; }

    public LocalDate getFechaEnrolamiento() { return fechaEnrolamiento; }
    public void setFechaEnrolamiento(LocalDate fechaEnrolamiento) { this.fechaEnrolamiento = fechaEnrolamiento; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
