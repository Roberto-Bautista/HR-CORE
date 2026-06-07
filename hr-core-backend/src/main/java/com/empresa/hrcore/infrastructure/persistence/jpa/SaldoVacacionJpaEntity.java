package com.empresa.hrcore.infrastructure.persistence.jpa;

import jakarta.persistence.*;
import java.util.UUID;

/**
 * ENTIDAD JPA — Saldo de Vacaciones en la Base de Datos
 *
 * Mapea la tabla 'vacation_balances'.
 * Cada registro representa el saldo de un empleado para un año específico.
 */
@Entity
@Table(name = "vacation_balances",
       uniqueConstraints = @UniqueConstraint(columnNames = {"employee_id", "anio"}))
public class SaldoVacacionJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "employee_id", nullable = false)
    private UUID employeeId;

    @Column(name = "anio", nullable = false)
    private int anio;

    @Column(name = "dias_totales", nullable = false)
    private int diasTotales;

    @Column(name = "dias_usados", nullable = false)
    private int diasUsados;

    // ==========================================================
    // Getters y Setters
    // ==========================================================

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getEmployeeId() { return employeeId; }
    public void setEmployeeId(UUID employeeId) { this.employeeId = employeeId; }

    public int getAnio() { return anio; }
    public void setAnio(int anio) { this.anio = anio; }

    public int getDiasTotales() { return diasTotales; }
    public void setDiasTotales(int diasTotales) { this.diasTotales = diasTotales; }

    public int getDiasUsados() { return diasUsados; }
    public void setDiasUsados(int diasUsados) { this.diasUsados = diasUsados; }
}
