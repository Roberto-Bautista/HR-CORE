package com.empresa.hrcore.infrastructure.persistence.jpa;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * ENTIDAD JPA — Beneficio
 *
 * Mapea la tabla 'benefits' en la base de datos PostgreSQL.
 */
@Entity
@Table(name = "benefits")
public class BeneficioJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true, length = 100)
    private String nombre;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Column(nullable = false)
    private BigDecimal costo;

    @Column(nullable = false)
    private boolean activo;

    // Reglas de elegibilidad
    @Column(name = "requiere_salario_min")
    private BigDecimal requiereSalarioMin;

    @Column(name = "requiere_salario_max")
    private BigDecimal requiereSalarioMax;

    @Column(name = "requiere_antiguedad_meses")
    private Integer requiereAntiguedadMeses;

    @Column(name = "requiere_cargo", length = 100)
    private String requiereCargo;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // ==========================================================
    // Getters y Setters
    // ==========================================================

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public BigDecimal getCosto() { return costo; }
    public void setCosto(BigDecimal costo) { this.costo = costo; }

    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }

    public BigDecimal getRequiereSalarioMin() { return requiereSalarioMin; }
    public void setRequiereSalarioMin(BigDecimal requiereSalarioMin) { this.requiereSalarioMin = requiereSalarioMin; }

    public BigDecimal getRequiereSalarioMax() { return requiereSalarioMax; }
    public void setRequiereSalarioMax(BigDecimal requiereSalarioMax) { this.requiereSalarioMax = requiereSalarioMax; }

    public Integer getRequiereAntiguedadMeses() { return requiereAntiguedadMeses; }
    public void setRequiereAntiguedadMeses(Integer requiereAntiguedadMeses) { this.requiereAntiguedadMeses = requiereAntiguedadMeses; }

    public String getRequiereCargo() { return requiereCargo; }
    public void setRequiereCargo(String requiereCargo) { this.requiereCargo = requiereCargo; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
