package com.empresa.hrcore.domain.entities;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * ENTIDAD DE DOMINIO — Empleado
 *
 * Esta clase representa al empleado tal como lo entiende el NEGOCIO.
 * NO tiene anotaciones de JPA (@Entity, @Column, etc.) porque el dominio
 * no debe saber nada sobre la base de datos.
 *
 * En Arquitectura Hexagonal:
 *   Dominio (esta clase) → NO depende de nada externo
 *   JPA Entity            → Depende de esta clase (capa de infraestructura)
 */
public class Empleado {

    private UUID id;
    private String codigo;          // EMP-0001
    private String nombre;
    private String apellido;
    private String email;
    private String telefono;
    private String genero;          // MASCULINO, FEMENINO, OTRO

    // Datos laborales
    private String cargo;
    private String departamento;
    private BigDecimal salario;
    private LocalDate fechaAlta;    // Fecha de ingreso a la empresa
    private LocalDate fechaCese;    // null si sigue activo
    private EstadoEmpleado status;

    // Perfil profesional (relación 1-a-1)
    private PerfilProfesional perfilProfesional;

    // Auditoría
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;

    // ==========================================================
    // ENUM interno: estados posibles del empleado
    // ==========================================================
    public enum EstadoEmpleado {
        ACTIVO, INACTIVO, CESADO
    }

    // ==========================================================
    // Constructores
    // ==========================================================

    /** Constructor vacío (necesario para frameworks) */
    public Empleado() {}

    /** Constructor para CREAR un empleado nuevo (uso típico desde el Use Case) */
    public Empleado(String codigo, String nombre, String apellido, String email,
                    String cargo, String departamento, BigDecimal salario, LocalDate fechaAlta) {
        this.id = UUID.randomUUID();
        this.codigo = codigo;
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.cargo = cargo;
        this.departamento = departamento;
        this.salario = salario;
        this.fechaAlta = fechaAlta;
        this.status = EstadoEmpleado.ACTIVO;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // ==========================================================
    // LÓGICA DE NEGOCIO (aquí vive la inteligencia del dominio)
    // ==========================================================

    /**
     * Cesa a un empleado (lo despide o renuncia).
     * Regla de negocio: no se puede cesar a alguien ya cesado.
     */
    public void cesar(LocalDate fechaCese) {
        if (this.status == EstadoEmpleado.CESADO) {
            throw new IllegalStateException("El empleado ya se encuentra cesado.");
        }
        this.status = EstadoEmpleado.CESADO;
        this.fechaCese = fechaCese;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Reactiva a un empleado inactivo.
     */
    public void reactivar() {
        if (this.status == EstadoEmpleado.ACTIVO) {
            throw new IllegalStateException("El empleado ya se encuentra activo.");
        }
        this.status = EstadoEmpleado.ACTIVO;
        this.fechaCese = null;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Actualiza los datos laborales del empleado.
     */
    public void actualizarDatosLaborales(String cargo, String departamento, BigDecimal salario) {
        this.cargo = cargo;
        this.departamento = departamento;
        this.salario = salario;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Verifica si el empleado está activo.
     */
    public boolean estaActivo() {
        return this.status == EstadoEmpleado.ACTIVO;
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

    public EstadoEmpleado getStatus() { return status; }
    public void setStatus(EstadoEmpleado status) { this.status = status; }

    public PerfilProfesional getPerfilProfesional() { return perfilProfesional; }
    public void setPerfilProfesional(PerfilProfesional perfilProfesional) { this.perfilProfesional = perfilProfesional; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }

    public String getUpdatedBy() { return updatedBy; }
    public void setUpdatedBy(String updatedBy) { this.updatedBy = updatedBy; }
}
