package com.empresa.hrcore.domain.patterns.strategy.benefits;

import com.empresa.hrcore.domain.entities.Empleado;
import com.empresa.hrcore.domain.entities.Beneficio;
import java.math.BigDecimal;

/**
 * STRATEGY: Validación de elegibilidad por salario.
 */
public class VerificadorPorSalario implements VerificadorElegibilidad {

    @Override
    public boolean esElegible(Empleado empleado, Beneficio beneficio) {
        if (empleado == null || beneficio == null) {
            return false;
        }

        BigDecimal salario = empleado.getSalario();

        // 1. Validar límite inferior (salario mínimo requerido)
        if (beneficio.getRequiereSalarioMin() != null) {
            if (salario == null || salario.compareTo(beneficio.getRequiereSalarioMin()) < 0) {
                return false;
            }
        }

        // 2. Validar límite superior (salario máximo permitido)
        if (beneficio.getRequiereSalarioMax() != null) {
            if (salario == null || salario.compareTo(beneficio.getRequiereSalarioMax()) > 0) {
                return false;
            }
        }

        return true;
    }
}
