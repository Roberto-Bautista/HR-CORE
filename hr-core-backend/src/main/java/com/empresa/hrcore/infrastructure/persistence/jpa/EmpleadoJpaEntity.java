package com.empresa.hrcore.infrastructure.persistence.jpa;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * ENTIDAD JPA — Representación del Empleado en la Base de Datos
 *
 * Esta clase SÍ tiene anotaciones de JPA porque su responsabilidad
 * es mapearse a la tabla 'employees' de PostgreSQL.
 *
 * En Arquitectura Hexagonal, esta es la "capa externa" (adaptador).
 * NUNCA debe usarse directamente en el Servicio o el Dominio.
 *
 * El flujo es:
 *   Controller → Service → Entidad de Dominio (Empleado.java)
 *                        → RepositoryImpl convierte a JpaEntity para guardar
 */
@Entity
@Table(name = "employees")
public class EmpleadoJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "codigo", nullable = false, unique = true, length = 20)
    private String codigo;

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @Column(name = "apellido", nullable = false, length = 100)
    private String apellido;

    @Column(name = "email", nullable = false, unique = true, length = 150)
    private String email;

    @Column(name = "telefono", length = 20)
    private String telefono;

    @Column(name = "genero", length = 20)
    private String genero;

    @Column(name = "cargo", nullable = false, length = 100)
    private String cargo;

    @Column(name = "departamento", nullable = false, length = 100)
    private String departamento;

    @Column(name = "salario", nullable = false, precision = 12, scale = 2)
    private BigDecimal salario;

    @Column(name = "fecha_alta", nullable = false)
    private LocalDate fechaAlta;

    @Column(name = "fecha_cese")
    private LocalDate fechaCese;

    @Column(name = "status", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "created_by", length = 150)
    private String createdBy;

    @Column(name = "updated_by", length = 150)
    private String updatedBy;

    // Relación 1-a-1 con perfil profesional
    @OneToOne(mappedBy = "empleado", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private PerfilProfesionalJpaEntity perfilProfesional;

    // Enum del status (debe coincidir con el tipo ENUM de PostgreSQL)
    public enum Status {
        ACTIVO, INACTIVO, CESADO
    }

    // Callbacks de JPA para manejar timestamps automáticamente
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

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getGenero() { return genero; }
    public void setGenero(String genero) { this.genero = genero; }

    public String getCargo() { return cargo; }
    public void setCargo(String cargo) { this.cargo = cargo; }

    public String getDepartamento() { return departamento; }
    public void setDepartamento(String departamento) { this.departamento = departamento; }

    public BigDecimal getSalario() { return salario; }
    public void setSalario(BigDecimal salario) { this.salario = salario; }

    public LocalDate getFechaAlta() { return fechaAlta; }
    public void setFechaAlta(LocalDate fechaAlta) { this.fechaAlta = fechaAlta; }

    public LocalDate getFechaCese() { return fechaCese; }
    public void setFechaCese(LocalDate fechaCese) { this.fechaCese = fechaCese; }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }

    public String getUpdatedBy() { return updatedBy; }
    public void setUpdatedBy(String updatedBy) { this.updatedBy = updatedBy; }

    public PerfilProfesionalJpaEntity getPerfilProfesional() { return perfilProfesional; }
    public void setPerfilProfesional(PerfilProfesionalJpaEntity perfilProfesional) { this.perfilProfesional = perfilProfesional; }
}
