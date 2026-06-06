package com.empresa.hrcore.presentation.exceptions;

/**
 * Excepción para conflictos de datos duplicados.
 * Se lanza cuando se intenta crear un recurso que ya existe.
 *
 * Ejemplo: crear un empleado con un email que ya está registrado → 409 Conflict.
 */
public class ConflictException extends RuntimeException {

    public ConflictException(String message) {
        super(message);
    }

    public ConflictException(String resource, String field, Object value) {
        super(String.format("Ya existe un %s con %s '%s'.", resource, field, value));
    }
}
