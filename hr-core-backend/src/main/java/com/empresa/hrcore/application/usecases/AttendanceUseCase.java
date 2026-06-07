package com.empresa.hrcore.application.usecases;

import com.empresa.hrcore.domain.entities.Marcacion;
import com.empresa.hrcore.domain.entities.RegistroTiempo;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface AttendanceUseCase {
    
    /**
     * Registra una marcación de forma rápida (validando en caché y enviando a Kafka).
     */
    void registrarMarcacion(UUID empleadoId, Marcacion.TipoMarcacion tipo);

    /**
     * Obtiene el historial de marcaciones de un empleado en un día.
     */
    List<Marcacion> obtenerMarcaciones(UUID empleadoId, LocalDate fecha);

    /**
     * Obtiene (o calcula on-the-fly si no existe) el registro de tiempo consolidado de un día.
     */
    RegistroTiempo obtenerRegistroDiario(UUID empleadoId, LocalDate fecha);

    /**
     * Obtiene el historial de registros diarios entre dos fechas.
     */
    List<RegistroTiempo> obtenerHistorial(UUID empleadoId, LocalDate desde, LocalDate hasta);
}
