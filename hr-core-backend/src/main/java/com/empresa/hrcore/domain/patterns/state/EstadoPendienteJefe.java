package com.empresa.hrcore.domain.patterns.state;

import com.empresa.hrcore.domain.entities.SolicitudVacacion;
import com.empresa.hrcore.domain.exceptions.EstadoTransicionInvalidaException;

/**
 * PATRÓN STATE — Estado: PENDIENTE_JEFE
 *
 * La solicitud fue enviada y espera la revisión del jefe/admin.
 * El jefe puede:
 *   - APROBAR → pasa a APROBADA
 *   - RECHAZAR → pasa a RECHAZADA (con motivo obligatorio)
 *
 * No puede: enviar (ya fue enviada).
 */
public class EstadoPendienteJefe implements EstadoVacacion {

    @Override
    public void enviar(SolicitudVacacion solicitud) {
        throw new EstadoTransicionInvalidaException(getNombre(), "enviar");
    }

    @Override
    public void aprobar(SolicitudVacacion solicitud) {
        // Transición válida: PENDIENTE_JEFE → APROBADA
        solicitud.setEstadoInterno(new EstadoAprobada());
    }

    @Override
    public void rechazar(SolicitudVacacion solicitud, String motivo) {
        // Transición válida: PENDIENTE_JEFE → RECHAZADA
        solicitud.setMotivoRechazo(motivo);
        solicitud.setEstadoInterno(new EstadoRechazada());
    }

    @Override
    public String getNombre() {
        return "PENDIENTE_JEFE";
    }
}
