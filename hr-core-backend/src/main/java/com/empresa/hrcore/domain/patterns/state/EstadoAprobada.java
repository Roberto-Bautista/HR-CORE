package com.empresa.hrcore.domain.patterns.state;

import com.empresa.hrcore.domain.entities.SolicitudVacacion;
import com.empresa.hrcore.domain.exceptions.EstadoTransicionInvalidaException;

/**
 * PATRÓN STATE — Estado: APROBADA
 *
 * Estado terminal. La solicitud fue aprobada por el jefe.
 * No se permite ninguna acción adicional.
 */
public class EstadoAprobada implements EstadoVacacion {

    @Override
    public void enviar(SolicitudVacacion solicitud) {
        throw new EstadoTransicionInvalidaException(getNombre(), "enviar");
    }

    @Override
    public void aprobar(SolicitudVacacion solicitud) {
        throw new EstadoTransicionInvalidaException(getNombre(), "aprobar");
    }

    @Override
    public void rechazar(SolicitudVacacion solicitud, String motivo) {
        throw new EstadoTransicionInvalidaException(getNombre(), "rechazar");
    }

    @Override
    public String getNombre() {
        return "APROBADA";
    }
}
