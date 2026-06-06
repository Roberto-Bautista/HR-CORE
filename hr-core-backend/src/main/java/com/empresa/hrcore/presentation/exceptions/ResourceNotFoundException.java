package com.empresa.hrcore.presentation.exceptions;

/**
 * Excepción de la capa de Presentación para recursos no encontrados.
 * Se lanza cuando se busca un empleado, solicitud, beneficio, etc. por ID
 * y no existe en la base de datos.
 *
 * Ejemplo: GET /v1/employees/{id} con un UUID que no existe → 404 Not Found.
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String resource, Object id) {
        super(String.format("%s con id '%s' no fue encontrado.", resource, id));
    }

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
