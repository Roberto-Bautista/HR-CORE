package com.empresa.hrcore.presentation.exceptions;

import com.empresa.hrcore.domain.exceptions.DomainException;
import com.empresa.hrcore.domain.exceptions.EmpleadoNoElegibleException;
import com.empresa.hrcore.domain.exceptions.EstadoTransicionInvalidaException;
import com.empresa.hrcore.domain.exceptions.SaldoInsuficienteException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Manejador global de excepciones para toda la API REST.
 *
 * @RestControllerAdvice intercepta las excepciones de cualquier
 * @RestController antes de que lleguen al cliente, asegurando que
 * SIEMPRE se devuelva un ErrorResponse con el formato estándar.
 *
 * Jerarquía de manejo:
 * 1. Excepciones de Dominio (SaldoInsuficiente, EstadoInvalido, etc.)
 * 2. Excepciones de Validación (@Valid en los DTOs)
 * 3. Excepciones de Recurso No Encontrado
 * 4. Excepciones de Conflicto (ej: email duplicado)
 * 5. Excepciones genéricas (fallback)
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // ============================================================
    // 1. EXCEPCIONES DE DOMINIO ESPECÍFICAS
    // ============================================================

    /**
     * Maneja: SaldoInsuficienteException
     * Caso: empleado solicita más días de vacaciones de los que tiene.
     * HTTP: 422 Unprocessable Entity
     */
    @ExceptionHandler(SaldoInsuficienteException.class)
    public ResponseEntity<ErrorResponse> handleSaldoInsuficiente(
            SaldoInsuficienteException ex, HttpServletRequest request) {

        log.warn("Saldo insuficiente: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(new ErrorResponse(
                        HttpStatus.UNPROCESSABLE_ENTITY.value(),
                        ex.getErrorCode(),
                        ex.getMessage(),
                        request.getRequestURI()));
    }

    /**
     * Maneja: EstadoTransicionInvalidaException
     * Caso: intento de transición de estado inválida en solicitud de vacaciones.
     * HTTP: 409 Conflict
     */
    @ExceptionHandler(EstadoTransicionInvalidaException.class)
    public ResponseEntity<ErrorResponse> handleEstadoInvalido(
            EstadoTransicionInvalidaException ex, HttpServletRequest request) {

        log.warn("Transición de estado inválida: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new ErrorResponse(
                        HttpStatus.CONFLICT.value(),
                        ex.getErrorCode(),
                        ex.getMessage(),
                        request.getRequestURI()));
    }

    /**
     * Maneja: EmpleadoNoElegibleException
     * Caso: empleado intenta enrolarse en beneficio sin cumplir criterios.
     * HTTP: 403 Forbidden
     */
    @ExceptionHandler(EmpleadoNoElegibleException.class)
    public ResponseEntity<ErrorResponse> handleNoElegible(
            EmpleadoNoElegibleException ex, HttpServletRequest request) {

        log.warn("Empleado no elegible: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(new ErrorResponse(
                        HttpStatus.FORBIDDEN.value(),
                        ex.getErrorCode(),
                        ex.getMessage(),
                        request.getRequestURI()));
    }

    /**
     * Fallback para cualquier otra DomainException no capturada arriba.
     * HTTP: 400 Bad Request
     */
    @ExceptionHandler(DomainException.class)
    public ResponseEntity<ErrorResponse> handleDomainException(
            DomainException ex, HttpServletRequest request) {

        log.warn("Error de dominio [{}]: {}", ex.getErrorCode(), ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(
                        HttpStatus.BAD_REQUEST.value(),
                        ex.getErrorCode(),
                        ex.getMessage(),
                        request.getRequestURI()));
    }

    // ============================================================
    // 2. ERRORES DE VALIDACIÓN (@Valid en DTOs)
    // ============================================================

    /**
     * Maneja: MethodArgumentNotValidException
     * Caso: el body del request no pasa las validaciones de @NotNull, @Size, etc.
     * HTTP: 400 Bad Request con lista de todos los campos inválidos.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationErrors(
            MethodArgumentNotValidException ex, HttpServletRequest request) {

        List<String> fieldErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.toList());

        log.warn("Error de validación en {}: {}", request.getRequestURI(), fieldErrors);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(
                        HttpStatus.BAD_REQUEST.value(),
                        ErrorCode.VALIDATION_ERROR,
                        "La solicitud contiene campos inválidos.",
                        request.getRequestURI(),
                        fieldErrors));
    }

    // ============================================================
    // 3. RECURSO NO ENCONTRADO
    // ============================================================

    /**
     * Maneja: ResourceNotFoundException (definida en esta misma capa)
     * HTTP: 404 Not Found
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(
            ResourceNotFoundException ex, HttpServletRequest request) {

        log.info("Recurso no encontrado: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(
                        HttpStatus.NOT_FOUND.value(),
                        ErrorCode.NOT_FOUND,
                        ex.getMessage(),
                        request.getRequestURI()));
    }

    // ============================================================
    // 4. CONFLICTOS (datos duplicados, etc.)
    // ============================================================

    /**
     * Maneja: ConflictException
     * Caso: email duplicado, código de empleado ya existente, etc.
     * HTTP: 409 Conflict
     */
    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ErrorResponse> handleConflict(
            ConflictException ex, HttpServletRequest request) {

        log.warn("Conflicto de datos: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new ErrorResponse(
                        HttpStatus.CONFLICT.value(),
                        ErrorCode.CONFLICT_ERROR,
                        ex.getMessage(),
                        request.getRequestURI()));
    }

    // ============================================================
    // 5. FALLBACK GENÉRICO (cualquier Exception no capturada)
    // ============================================================

    /**
     * Captura cualquier excepción inesperada.
     * NUNCA expone el stack trace al cliente (seguridad).
     * HTTP: 500 Internal Server Error
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
            Exception ex, HttpServletRequest request) {

        // Log completo en servidor para diagnóstico
        log.error("Error inesperado en {}: {}", request.getRequestURI(), ex.getMessage(), ex);

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(
                        HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        ErrorCode.INTERNAL_ERROR,
                        "Ocurrió un error interno. Contacte al administrador.",
                        request.getRequestURI()));
    }
}
