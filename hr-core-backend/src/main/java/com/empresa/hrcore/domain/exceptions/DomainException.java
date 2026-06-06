package com.empresa.hrcore.domain.exceptions;

/**
 * Excepción base de la capa de Dominio.
 * Todas las excepciones de negocio deben extender de esta clase.
 * Al ser RuntimeException, no obliga a ser declarada con "throws".
 */
public abstract class DomainException extends RuntimeException {

    private final String errorCode;

    protected DomainException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    protected DomainException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
