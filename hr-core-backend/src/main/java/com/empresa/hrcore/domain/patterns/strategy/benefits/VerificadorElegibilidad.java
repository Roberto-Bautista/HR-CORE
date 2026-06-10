package com.empresa.hrcore.domain.patterns.strategy.benefits;

import com.empresa.hrcore.domain.entities.Empleado;
import com.empresa.hrcore.domain.entities.Beneficio;

/**
 * STRATEGY PATTERN - Interfaz para validar elegibilidad.
 */
public interface VerificadorElegibilidad {

    /**
     * Evalúa si un empleado cumple con las reglas del beneficio.
     *
     * @param empleado El empleado a evaluar.
     * @param beneficio El beneficio al que desea afiliarse.
     * @return true si es elegible, false en caso contrario.
     */
    boolean esElegible(Empleado empleado, Beneficio beneficio);
}
