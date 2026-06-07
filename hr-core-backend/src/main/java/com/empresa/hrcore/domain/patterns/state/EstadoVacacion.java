package com.empresa.hrcore.domain.patterns.state;

import com.empresa.hrcore.domain.entities.SolicitudVacacion;

/**
 * PATRÓN STATE — Interfaz del Estado de Vacaciones
 *
 * Define las acciones posibles sobre una solicitud de vacaciones.
 * Cada estado concreto decide qué acciones permite y cuáles rechaza.
 *
 * Transiciones válidas:
 *   BORRADOR → PENDIENTE_JEFE (enviar)
 *   PENDIENTE_JEFE → APROBADA (aprobar)
 *   PENDIENTE_JEFE → RECHAZADA (rechazar)
 *
 * Diagrama:
 *   [Borrador] --enviar--> [PendienteJefe] --aprobar--> [Aprobada]
 *                                          --rechazar--> [Rechazada]
 */
public interface EstadoVacacion {

    /** Envía la solicitud para revisión del jefe */
    void enviar(SolicitudVacacion solicitud);

    /** Aprueba la solicitud (solo desde PendienteJefe) */
    void aprobar(SolicitudVacacion solicitud);

    /** Rechaza la solicitud con un motivo (solo desde PendienteJefe) */
    void rechazar(SolicitudVacacion solicitud, String motivo);

    /** Retorna el nombre del estado para persistirlo en la BD */
    String getNombre();
}
