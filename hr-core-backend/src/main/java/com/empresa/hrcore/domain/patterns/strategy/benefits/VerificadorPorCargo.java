package com.empresa.hrcore.domain.patterns.strategy.benefits;

import com.empresa.hrcore.domain.entities.Empleado;
import com.empresa.hrcore.domain.entities.Beneficio;

/**
 * STRATEGY: Validación de elegibilidad por cargo/puesto.
 */
public class VerificadorPorCargo implements VerificadorElegibilidad {

    @Override
    public boolean esElegible(Empleado empleado, Beneficio beneficio) {
        if (empleado == null || beneficio == null) {
            return false;
        }

        String cargoRequerido = beneficio.getRequiereCargo();
        if (cargoRequerido == null || cargoRequerido.strip().isEmpty()) {
            return true;
        }

        String cargoEmpleado = empleado.getCargo();
        if (cargoEmpleado == null) {
            return false;
        }

        return cargoEmpleado.strip().equalsIgnoreCase(cargoRequerido.strip());
    }
}
