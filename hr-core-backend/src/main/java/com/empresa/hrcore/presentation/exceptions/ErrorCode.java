package com.empresa.hrcore.presentation.exceptions;

/**
 * Catálogo de todos los códigos de error de la API.
 * Permite al cliente front-end manejar errores de forma programática
 * sin depender solo del mensaje de texto (que puede cambiar).
 */
public final class ErrorCode {

    private ErrorCode() {} // Utilidad: no se instancia

    // --- Errores de Empleados ---
    public static final String EMPLOYEE_NOT_FOUND    = "EMPLOYEE_001";
    public static final String EMPLOYEE_EMAIL_EXISTS = "EMPLOYEE_002";
    public static final String EMPLOYEE_CODE_EXISTS  = "EMPLOYEE_003";

    // --- Errores de Ausencias / Vacaciones ---
    public static final String ABSENCE_INSUFFICIENT_BALANCE  = "ABSENCE_001";
    public static final String ABSENCE_INVALID_STATE         = "ABSENCE_002";
    public static final String ABSENCE_NOT_FOUND             = "ABSENCE_003";
    public static final String ABSENCE_DATE_CONFLICT         = "ABSENCE_004";

    // --- Errores de Asistencia ---
    public static final String ATTENDANCE_ALREADY_CHECKED_IN  = "ATTENDANCE_001";
    public static final String ATTENDANCE_NO_CHECKIN_FOUND    = "ATTENDANCE_002";

    // --- Errores de Beneficios ---
    public static final String BENEFITS_NOT_ELIGIBLE     = "BENEFITS_001";
    public static final String BENEFITS_ALREADY_ENROLLED = "BENEFITS_002";
    public static final String BENEFITS_NOT_FOUND        = "BENEFITS_003";

    // --- Errores de Seguridad / RBAC ---
    public static final String AUTH_UNAUTHORIZED  = "AUTH_001";
    public static final String AUTH_FORBIDDEN     = "AUTH_002";
    public static final String AUTH_TOKEN_INVALID = "AUTH_003";

    // --- Errores genéricos ---
    public static final String VALIDATION_ERROR  = "VALIDATION_001";
    public static final String CONFLICT_ERROR    = "CONFLICT_001";
    public static final String INTERNAL_ERROR    = "INTERNAL_001";
    public static final String NOT_FOUND         = "NOTFOUND_001";
}
