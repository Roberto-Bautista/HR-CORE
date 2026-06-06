package com.empresa.hrcore.presentation.dtos;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

/**
 * DTO de ENTRADA — Actualizar Empleado (PUT)
 */
public class UpdateEmployeeRequest {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100)
    private String nombre;

    @NotBlank(message = "El apellido es obligatorio")
    @Size(max = 100)
    private String apellido;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email debe tener un formato válido")
    @Size(max = 150)
    private String email;

    @Size(max = 20)
    private String telefono;

    @NotBlank(message = "El cargo es obligatorio")
    @Size(max = 100)
    private String cargo;

    @NotBlank(message = "El departamento es obligatorio")
    @Size(max = 100)
    private String departamento;

    @NotNull(message = "El salario es obligatorio")
    @DecimalMin(value = "0.01", message = "El salario debe ser mayor a 0")
    private BigDecimal salario;

    // Getters y Setters
    public String getNombre()       { return nombre; }
    public void setNombre(String v) { this.nombre = v; }

    public String getApellido()       { return apellido; }
    public void setApellido(String v) { this.apellido = v; }

    public String getEmail()       { return email; }
    public void setEmail(String v) { this.email = v; }

    public String getTelefono()       { return telefono; }
    public void setTelefono(String v) { this.telefono = v; }

    public String getCargo()       { return cargo; }
    public void setCargo(String v) { this.cargo = v; }

    public String getDepartamento()       { return departamento; }
    public void setDepartamento(String v) { this.departamento = v; }

    public BigDecimal getSalario()       { return salario; }
    public void setSalario(BigDecimal v) { this.salario = v; }
}
