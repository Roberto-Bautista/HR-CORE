package com.empresa.hrcore.domain.patterns.strategy.benefits;

import com.empresa.hrcore.domain.entities.Empleado;
import com.empresa.hrcore.domain.entities.Beneficio;

import java.util.ArrayList;
import java.util.List;

/**
 * COMPOSITE: Agrupa todas las estrategias de elegibilidad.
 * Evalúa cada una en orden. Si alguna falla, el empleado no es elegible.
 */
public class ElegibilidadComposite implements VerificadorElegibilidad {

    private final List<VerificadorElegibilidad> verificadores = new ArrayList<>();

    public ElegibilidadComposite() {
        // Inicializar con todas las estrategias del catálogo
        verificadores.add(new VerificadorPorSalario());
        verificadores.add(new VerificadorPorAntiguedad());
        verificadores.add(new VerificadorPorCargo());
    }

    @Override
    public boolean esElegible(Empleado empleado, Beneficio beneficio) {
        if (empleado == null || beneficio == null) {
            return false;
        }

        // Si el beneficio no está activo, nadie es elegible
        if (!beneficio.isActivo()) {
            return false;
        }

        // Evaluar todas las estrategias (debe cumplir todas -> AND)
        for (VerificadorElegibilidad verificador : verificadores) {
            if (!verificador.esElegible(empleado, beneficio)) {
                return false;
            }
        }

        return true;
    }
}
