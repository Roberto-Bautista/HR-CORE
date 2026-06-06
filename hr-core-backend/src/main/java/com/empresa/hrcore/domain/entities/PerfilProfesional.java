package com.empresa.hrcore.domain.entities;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * ENTIDAD DE DOMINIO — Perfil Profesional
 *
 * Almacena información complementaria del empleado:
 * habilidades, certificaciones, nivel educativo, etc.
 *
 * Relación: Un Empleado tiene exactamente un PerfilProfesional (1-a-1).
 */
public class PerfilProfesional {

    private UUID id;
    private UUID empleadoId;        // Referencia al empleado dueño de este perfil
    private String nivelEducativo;  // SECUNDARIA, TECNICO, LICENCIATURA, MAESTRIA, DOCTORADO
    private String especialidad;    // Ej: "Ingeniería de Software", "Administración"
    private List<String> habilidades;      // ["Java", "Spring Boot", "SQL"]
    private List<String> certificaciones;  // ["AWS Certified", "Scrum Master"]
    private String linkedinUrl;
    private LocalDateTime updatedAt;

    // ==========================================================
    // Constructores
    // ==========================================================

    public PerfilProfesional() {
        this.habilidades = new ArrayList<>();
        this.certificaciones = new ArrayList<>();
    }

    public PerfilProfesional(UUID empleadoId) {
        this.id = UUID.randomUUID();
        this.empleadoId = empleadoId;
        this.habilidades = new ArrayList<>();
        this.certificaciones = new ArrayList<>();
        this.updatedAt = LocalDateTime.now();
    }

    // ==========================================================
    // Lógica de dominio
    // ==========================================================

    public void agregarHabilidad(String habilidad) {
        if (!this.habilidades.contains(habilidad)) {
            this.habilidades.add(habilidad);
            this.updatedAt = LocalDateTime.now();
        }
    }

    public void agregarCertificacion(String certificacion) {
        if (!this.certificaciones.contains(certificacion)) {
            this.certificaciones.add(certificacion);
            this.updatedAt = LocalDateTime.now();
        }
    }

    public void actualizarPerfil(String nivelEducativo, String especialidad, String linkedinUrl) {
        this.nivelEducativo = nivelEducativo;
        this.especialidad = especialidad;
        this.linkedinUrl = linkedinUrl;
        this.updatedAt = LocalDateTime.now();
    }

    // ==========================================================
    // Getters y Setters
    // ==========================================================

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getEmpleadoId() { return empleadoId; }
    public void setEmpleadoId(UUID empleadoId) { this.empleadoId = empleadoId; }

    public String getNivelEducativo() { return nivelEducativo; }
    public void setNivelEducativo(String nivelEducativo) { this.nivelEducativo = nivelEducativo; }

    public String getEspecialidad() { return especialidad; }
    public void setEspecialidad(String especialidad) { this.especialidad = especialidad; }

    public List<String> getHabilidades() { return habilidades; }
    public void setHabilidades(List<String> habilidades) { this.habilidades = habilidades; }

    public List<String> getCertificaciones() { return certificaciones; }
    public void setCertificaciones(List<String> certificaciones) { this.certificaciones = certificaciones; }

    public String getLinkedinUrl() { return linkedinUrl; }
    public void setLinkedinUrl(String linkedinUrl) { this.linkedinUrl = linkedinUrl; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
