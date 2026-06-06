package com.empresa.hrcore.domain.exceptions;

/**
 * Se lanza cuando se intenta hacer una transición de estado inválida
 * en el flujo de la Solicitud de Vacaciones.
 *
 * Ejemplo: intentar "Aprobar" una solicitud que ya está en estado "Rechazada".
 *
 * Relacionado con el Patrón STATE implementado en:
 * domain/patterns/state/
 */
public class EstadoTransicionInvalidaException extends DomainException {

    private static final String ERROR_CODE = "ABSENCE_002";

    public EstadoTransicionInvalidaException(String estadoActual, String accionIntentada) {
        super(ERROR_CODE,
              String.format("Transición inválida: no se puede ejecutar '%s' cuando la solicitud está en estado '%s'.",
                            accionIntentada, estadoActual));
    }
}
