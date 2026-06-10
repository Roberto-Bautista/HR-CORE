package com.empresa.hrcore.domain.patterns.strategy.benefits;

import com.empresa.hrcore.domain.entities.Empleado;
import com.empresa.hrcore.domain.entities.Beneficio;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * STRATEGY: Validación de elegibilidad por antigüedad (meses).
 */
public class VerificadorPorAntiguedad implements VerificadorElegibilidad {

    @Override
    public boolean esElegible(Empleado empleado, Beneficio beneficio) {
        if (empleado == null || beneficio == null) {
            return false;
        }

        // Si no hay restricción de antigüedad, es elegible por defecto en esta regla
        if (beneficio.getRequiereAntiguedadMeses() == null) {
            return true;
        }

        LocalDate fechaAlta = empleado.getFechaAlta();
        if (fechaAlta == null) {
            return false;
        }

        // Calcular la diferencia en meses entre la fecha de alta y el día de hoy
        long mesesTranscurridos = ChronoUnit.MONTHS.between(fechaAlta, LocalDate.now());

        return mesesTranscurridos >= beneficio.getRequiereAntiguedadMeses();
    }
}
