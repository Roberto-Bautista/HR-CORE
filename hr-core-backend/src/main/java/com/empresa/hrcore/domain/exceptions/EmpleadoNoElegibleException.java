package com.empresa.hrcore.domain.exceptions;

/**
 * Se lanza cuando un empleado no cumple los criterios de elegibilidad
 * para enrolarse en un beneficio corporativo.
 *
 * Ejemplo: empleado con menos de 6 meses de antigüedad intenta
 * enrolarse en un beneficio que requiere 1 año mínimo.
 *
 * Relacionado con el Patrón STRATEGY de elegibilidad en:
 * domain/patterns/strategy/benefits/
 */
public class EmpleadoNoElegibleException extends DomainException {

    private static final String ERROR_CODE = "BENEFITS_001";

    public EmpleadoNoElegibleException(String razon) {
        super(ERROR_CODE,
              "El empleado no cumple los criterios de elegibilidad para este beneficio: " + razon);
    }
}
