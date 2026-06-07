package com.empresa.hrcore.domain.exceptions;

public class AttendanceValidationException extends DomainException {
    public AttendanceValidationException(String message) {
        super("ATTENDANCE_VALIDATION_ERROR", message);
    }
}
