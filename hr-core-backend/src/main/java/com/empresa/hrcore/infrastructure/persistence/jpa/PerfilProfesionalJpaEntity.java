package com.empresa.hrcore.infrastructure.persistence.jpa;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * ENTIDAD JPA — Perfil Profesional en la Base de Datos
 * Mapea la tabla 'professional_profiles'.
 */
@Entity
@Table(name = "professional_profiles")
public class PerfilProfesionalJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // Relación 1-a-1 con empleado
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false, unique = true)
    private EmpleadoJpaEntity empleado;

    @Column(name = "nivel_educativo", length = 50)
    private String nivelEducativo;

    @Column(name = "especialidad", length = 200)
    private String especialidad;

    // PostgreSQL soporta arrays nativos con TEXT[]
    @Column(name = "habilidades", columnDefinition = "TEXT[]")
    private String[] habilidades;

    @Column(name = "certificaciones", columnDefinition = "TEXT[]")
    private String[] certificaciones;

    @Column(name = "linkedin_url", length = 300)
    private String linkedinUrl;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // ==========================================================
    // Getters y Setters
    // ==========================================================

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public EmpleadoJpaEntity getEmpleado() { return empleado; }
    public void setEmpleado(EmpleadoJpaEntity empleado) { this.empleado = empleado; }

    public String getNivelEducativo() { return nivelEducativo; }
    public void setNivelEducativo(String nivelEducativo) { this.nivelEducativo = nivelEducativo; }

    public String getEspecialidad() { return especialidad; }
    public void setEspecialidad(String especialidad) { this.especialidad = especialidad; }

    public String[] getHabilidades() { return habilidades; }
    public void setHabilidades(String[] habilidades) { this.habilidades = habilidades; }

    public String[] getCertificaciones() { return certificaciones; }
    public void setCertificaciones(String[] certificaciones) { this.certificaciones = certificaciones; }

    public String getLinkedinUrl() { return linkedinUrl; }
    public void setLinkedinUrl(String linkedinUrl) { this.linkedinUrl = linkedinUrl; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
