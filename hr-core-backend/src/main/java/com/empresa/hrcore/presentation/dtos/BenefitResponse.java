package com.empresa.hrcore.presentation.dtos;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * DTO — Respuesta del catálogo de beneficios.
 */
public class BenefitResponse {

    private UUID id;
    private String nombre;
    private String descripcion;
    private BigDecimal costo;
    private boolean activo;

    // Reglas de elegibilidad expuestas para el front-end
    private BigDecimal requiereSalarioMin;
    private BigDecimal requiereSalarioMax;
    private Integer requiereAntiguedadMeses;
    private String requiereCargo;

    // Banderas calculadas dinámicamente si se evalúa un empleado
    private Boolean esElegible;
    private Boolean yaEnrolado;
    private UUID enrolamientoId; // ID del enrolamiento si yaEnrolado es true

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

    public Boolean getEsElegible() { return esElegible; }
    public void setEsElegible(Boolean esElegible) { this.esElegible = esElegible; }

    public Boolean getYaEnrolado() { return yaEnrolado; }
    public void setYaEnrolado(Boolean yaEnrolado) { this.yaEnrolado = yaEnrolado; }

    public UUID getEnrolamientoId() { return enrolamientoId; }
    public void setEnrolamientoId(UUID enrolamientoId) { this.enrolamientoId = enrolamientoId; }
}
