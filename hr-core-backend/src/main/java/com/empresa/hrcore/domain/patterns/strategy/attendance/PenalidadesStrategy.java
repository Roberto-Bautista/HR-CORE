package com.empresa.hrcore.domain.patterns.strategy.attendance;

import com.empresa.hrcore.domain.entities.Marcacion;
import com.empresa.hrcore.domain.entities.RegistroTiempo;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * STRATEGY: Salida Anticipada y Tiempo Fuera de Horario
 *
 * Reglas:
 *   - Si hora_salida_real < 13:00  → salidaAnticipadaMin = 13:00 - salida_real
 *   - Si hora_salida_real > 13:00  → tiempoFueraDeHorarioMin = salida_real - 13:00
 *   - El tiempo fuera de horario es SOLO informativo.
 *     No incrementa horasEfectivas ni compensa tardanzas.
 *   - Si no hay SALIDA → ambos = 0
 *
 * IMPORTANTE: Esta estrategia debe ejecutarse ANTES de HorasNormalesStrategy
 * porque esta necesita salidaAnticipadaMin para calcular horasEfectivas.
 */
public class PenalidadesStrategy implements CalculadoraHoras {

    @Override
    public void calcular(List<Marcacion> marcacionesDelDia, RegistroTiempo registro) {
        LocalTime salidaReal = marcacionesDelDia.stream()
                .filter(m -> m.getTipo() == Marcacion.TipoMarcacion.SALIDA)
                .findFirst()
                .map(s -> s.getTimestamp().toLocalTime())
                .orElse(null);

        if (salidaReal == null) {
            registro.setSalidaAnticipadaMin(0);
            registro.setTiempoFueraDeHorarioMin(0);
            return;
        }

        if (salidaReal.isBefore(HorasNormalesStrategy.HORA_SALIDA_OFICIAL)) {
            // Salida anticipada
            int anticipada = (int) ChronoUnit.MINUTES.between(salidaReal, HorasNormalesStrategy.HORA_SALIDA_OFICIAL);
            registro.setSalidaAnticipadaMin(anticipada);
            registro.setTiempoFueraDeHorarioMin(0);
        } else if (salidaReal.isAfter(HorasNormalesStrategy.HORA_SALIDA_OFICIAL)) {
            // Tiempo fuera de horario (SOLO informativo)
            int fueraHorario = (int) ChronoUnit.MINUTES.between(HorasNormalesStrategy.HORA_SALIDA_OFICIAL, salidaReal);
            registro.setTiempoFueraDeHorarioMin(fueraHorario);
            registro.setSalidaAnticipadaMin(0);
        } else {
            // Salida exacta en punto
            registro.setSalidaAnticipadaMin(0);
            registro.setTiempoFueraDeHorarioMin(0);
        }
    }
}
