package com.empresa.hrcore.presentation.exceptions;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO estándar para todas las respuestas de error de la API.
 * Garantiza que el cliente siempre reciba el mismo formato JSON
 * sin importar el tipo de error.
 *
 * Ejemplo de respuesta JSON:
 * {
 *   "timestamp": "2025-01-15T10:30:00",
 *   "status": 400,
 *   "errorCode": "ABSENCE_001",
 *   "message": "Saldo insuficiente...",
 *   "path": "/v1/absences",
 *   "errors": []
 * }
 */
public class ErrorResponse {

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime timestamp;

    private int status;
    private String errorCode;
    private String message;
    private String path;
    private List<String> errors;  // Para errores de validación (múltiples campos)

    // Constructor para error simple
    public ErrorResponse(int status, String errorCode, String message, String path) {
        this.timestamp = LocalDateTime.now();
        this.status = status;
        this.errorCode = errorCode;
        this.message = message;
        this.path = path;
        this.errors = List.of();
    }

    // Constructor para errores de validación (múltiples mensajes)
    public ErrorResponse(int status, String errorCode, String message, String path, List<String> errors) {
        this(status, errorCode, message, path);
        this.errors = errors;
    }

    // Getters
    public LocalDateTime getTimestamp() { return timestamp; }
    public int getStatus()              { return status; }
    public String getErrorCode()        { return errorCode; }
    public String getMessage()          { return message; }
    public String getPath()             { return path; }
    public List<String> getErrors()     { return errors; }
}
