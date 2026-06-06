package com.empresa.hrcore.domain.exceptions;

/**
 * Se lanza cuando un empleado no tiene saldo suficiente de vacaciones
 * para cubrir los días solicitados.
 *
 * Ejemplo: empleado solicita 10 días pero solo tiene 5 disponibles.
 */
public class SaldoInsuficienteException extends DomainException {

    private static final String ERROR_CODE = "ABSENCE_001";

    public SaldoInsuficienteException(int diasSolicitados, int diasDisponibles) {
        super(ERROR_CODE,
              String.format("Saldo insuficiente: se solicitaron %d días pero solo hay %d disponibles.",
                            diasSolicitados, diasDisponibles));
    }
}
