package com.empresa.hrcore.presentation.dtos;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * DTO de SALIDA — Respuesta del Empleado
 *
 * El Controller NUNCA devuelve la entidad de dominio directamente al frontend.
 * En su lugar, devuelve este DTO que:
 *   1. Oculta campos internos (createdBy, updatedBy, etc.)
 *   2. Evita exponer la estructura interna del dominio
 *   3. Puede incluir campos calculados (nombreCompleto, etc.)
 */
public class EmployeeResponse {

    private UUID id;
    private String codigo;
    private String nombre;
    private String apellido;
    private String nombreCompleto;   // Campo calculado: "Ana Torres"
    private String email;
    private String telefono;
    private String genero;
    private String cargo;
    private String departamento;
    private BigDecimal salario;
    private LocalDate fechaAlta;
    private LocalDate fechaCese;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Perfil profesional (embebido en la respuesta)
    private PerfilResponse perfilProfesional;

    // ==========================================================
    // Subclase para el perfil profesional
    // ==========================================================
    public static class PerfilResponse {
        private String nivelEducativo;
        private String especialidad;
        private List<String> habilidades;
        private List<String> certificaciones;
        private String linkedinUrl;

        // Getters y Setters
        public String getNivelEducativo()       { return nivelEducativo; }
        public void setNivelEducativo(String v) { this.nivelEducativo = v; }

        public String getEspecialidad()       { return especialidad; }
        public void setEspecialidad(String v) { this.especialidad = v; }

        public List<String> getHabilidades()       { return habilidades; }
        public void setHabilidades(List<String> v) { this.habilidades = v; }

        public List<String> getCertificaciones()       { return certificaciones; }
        public void setCertificaciones(List<String> v) { this.certificaciones = v; }

        public String getLinkedinUrl()       { return linkedinUrl; }
        public void setLinkedinUrl(String v) { this.linkedinUrl = v; }
    }

    // ==========================================================
    // Getters y Setters
    // ==========================================================

    public UUID getId()           { return id; }
    public void setId(UUID v)     { this.id = v; }

    public String getCodigo()       { return codigo; }
    public void setCodigo(String v) { this.codigo = v; }

    public String getNombre()       { return nombre; }
    public void setNombre(String v) { this.nombre = v; }

    public String getApellido()       { return apellido; }
    public void setApellido(String v) { this.apellido = v; }

    public String getNombreCompleto()       { return nombreCompleto; }
    public void setNombreCompleto(String v) { this.nombreCompleto = v; }

    public String getEmail()       { return email; }
    public void setEmail(String v) { this.email = v; }

    public String getTelefono()       { return telefono; }
    public void setTelefono(String v) { this.telefono = v; }

    public String getGenero()       { return genero; }
    public void setGenero(String v) { this.genero = v; }

    public String getCargo()       { return cargo; }
    public void setCargo(String v) { this.cargo = v; }

    public String getDepartamento()       { return departamento; }
    public void setDepartamento(String v) { this.departamento = v; }

    public BigDecimal getSalario()       { return salario; }
    public void setSalario(BigDecimal v) { this.salario = v; }

    public LocalDate getFechaAlta()       { return fechaAlta; }
    public void setFechaAlta(LocalDate v) { this.fechaAlta = v; }

    public LocalDate getFechaCese()       { return fechaCese; }
    public void setFechaCese(LocalDate v) { this.fechaCese = v; }

    public String getStatus()       { return status; }
    public void setStatus(String v) { this.status = v; }

    public LocalDateTime getCreatedAt()       { return createdAt; }
    public void setCreatedAt(LocalDateTime v) { this.createdAt = v; }

    public LocalDateTime getUpdatedAt()       { return updatedAt; }
    public void setUpdatedAt(LocalDateTime v) { this.updatedAt = v; }

    public PerfilResponse getPerfilProfesional()       { return perfilProfesional; }
    public void setPerfilProfesional(PerfilResponse v) { this.perfilProfesional = v; }
}
