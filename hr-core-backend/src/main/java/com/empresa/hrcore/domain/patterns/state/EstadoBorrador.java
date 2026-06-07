package com.empresa.hrcore.domain.patterns.state;

import com.empresa.hrcore.domain.entities.SolicitudVacacion;
import com.empresa.hrcore.domain.exceptions.EstadoTransicionInvalidaException;

/**
 * PATRÓN STATE — Estado: BORRADOR
 *
 * Estado inicial de toda solicitud de vacaciones.
 * El empleado puede:
 *   - ENVIAR la solicitud → pasa a PENDIENTE_JEFE
 *
 * No puede: aprobar, rechazar (eso lo hace el jefe).
 */
public class EstadoBorrador implements EstadoVacacion {

    @Override
    public void enviar(SolicitudVacacion solicitud) {
        // Transición válida: BORRADOR → PENDIENTE_JEFE
        solicitud.setEstadoInterno(new EstadoPendienteJefe());
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
        return "BORRADOR";
    }
}
