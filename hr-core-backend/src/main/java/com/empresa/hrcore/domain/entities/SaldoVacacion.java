package com.empresa.hrcore.domain.entities;

import com.empresa.hrcore.domain.exceptions.SaldoInsuficienteException;

import java.util.UUID;

/**
 * VALUE OBJECT DE DOMINIO — Saldo de Vacaciones
 *
 * Representa el saldo de días de vacaciones de un empleado
 * para un año específico. Contiene la lógica de negocio
 * para validar si tiene días suficientes.
 *
 * Regla de negocio: cada empleado tiene 30 días por año.
 */
public class SaldoVacacion {

    private UUID id;
    private UUID empleadoId;
    private int anio;
    private int diasTotales;
    private int diasUsados;

    // ==========================================================
    // Constructores
    // ==========================================================

    public SaldoVacacion() {}

    /** Crea un saldo nuevo para un empleado en un año específico */
    public SaldoVacacion(UUID empleadoId, int anio) {
        this.id = UUID.randomUUID();
        this.empleadoId = empleadoId;
        this.anio = anio;
        this.diasTotales = 30;  // Regla de negocio: 30 días por año
        this.diasUsados = 0;
    }

    // ==========================================================
    // LÓGICA DE NEGOCIO
    // ==========================================================

    /** Calcula los días disponibles */
    public int getDiasDisponibles() {
        return diasTotales - diasUsados;
    }

    /**
     * Descuenta días del saldo.
     * Lanza SaldoInsuficienteException si no hay días suficientes.
     */
    public void descontarDias(int dias) {
        if (dias > getDiasDisponibles()) {
            throw new SaldoInsuficienteException(dias, getDiasDisponibles());
        }
        this.diasUsados += dias;
    }

    /**
     * Devuelve días al saldo (por cancelación o rechazo futuro).
     */
    public void devolverDias(int dias) {
        this.diasUsados = Math.max(0, this.diasUsados - dias);
    }

    // ==========================================================
    // Getters y Setters
    // ==========================================================

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getEmpleadoId() { return empleadoId; }
    public void setEmpleadoId(UUID empleadoId) { this.empleadoId = empleadoId; }

    public int getAnio() { return anio; }
    public void setAnio(int anio) { this.anio = anio; }

    public int getDiasTotales() { return diasTotales; }
    public void setDiasTotales(int diasTotales) { this.diasTotales = diasTotales; }

    public int getDiasUsados() { return diasUsados; }
    public void setDiasUsados(int diasUsados) { this.diasUsados = diasUsados; }
}
