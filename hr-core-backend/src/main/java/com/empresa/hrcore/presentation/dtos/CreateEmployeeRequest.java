package com.empresa.hrcore.presentation.dtos;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

/**
 * DTO de ENTRADA — Crear Empleado
 *
 * Los DTOs (Data Transfer Objects) son las clases que reciben
 * los datos del frontend (JSON). Las validaciones @NotBlank, @Email, etc.
 * se ejecutan ANTES de que el código llegue al Controller.
 *
 * Si el frontend envía datos inválidos, Spring lanza automáticamente
 * MethodArgumentNotValidException → GlobalExceptionHandler → ErrorResponse.
 */
public class CreateEmployeeRequest {

    // El código (EMP-001, EMP-002...) se genera automáticamente en el backend

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100, message = "El nombre no puede tener más de 100 caracteres")
    private String nombre;

    @NotBlank(message = "El apellido es obligatorio")
    @Size(max = 100, message = "El apellido no puede tener más de 100 caracteres")
    private String apellido;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email debe tener un formato válido")
    @Size(max = 150, message = "El email no puede tener más de 150 caracteres")
    private String email;

    @Size(max = 20, message = "El teléfono no puede tener más de 20 caracteres")
    private String telefono;

    private String genero;

    @NotBlank(message = "El cargo es obligatorio")
    @Size(max = 100)
    private String cargo;

    @NotBlank(message = "El departamento es obligatorio")
    @Size(max = 100)
    private String departamento;

    @NotNull(message = "El salario es obligatorio")
    @DecimalMin(value = "0.01", message = "El salario debe ser mayor a 0")
    private BigDecimal salario;

    @NotNull(message = "La fecha de alta es obligatoria")
    private String fechaAlta;  // Formato: "2025-01-15"

    // Getters y Setters
    public String getNombre()       { return nombre; }
    public void setNombre(String v) { this.nombre = v; }

    public String getApellido()       { return apellido; }
    public void setApellido(String v) { this.apellido = v; }

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

    public String getFechaAlta()       { return fechaAlta; }
    public void setFechaAlta(String v) { this.fechaAlta = v; }
}
