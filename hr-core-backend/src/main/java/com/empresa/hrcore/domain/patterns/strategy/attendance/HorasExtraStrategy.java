package com.empresa.hrcore.domain.patterns.strategy.attendance;

import com.empresa.hrcore.domain.entities.Marcacion;
import com.empresa.hrcore.domain.entities.RegistroTiempo;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * STRATEGY: Tardanza
 *
 * Reglas:
 *   - Tolerancia: hasta 08:05 sin tardanza.
 *   - Si hora_ingreso_reconocida > 08:05 → tardanza = reconocida - 08:05
 *   - Si no hay ENTRADA → tardanza = 0
 *
 * IMPORTANTE: Esta estrategia debe ejecutarse ANTES de HorasNormalesStrategy
 * porque esta necesita el valor de tardanzaMin para calcular horasEfectivas.
 */
public class HorasExtraStrategy extends HorasNormalesStrategy {

    private static final LocalTime LIMITE_TOLERANCIA_ENTRADA = HorasNormalesStrategy.HORA_ENTRADA_OFICIAL.plusMinutes(5); // 08:05

    @Override
    public void calcular(List<Marcacion> marcacionesDelDia, RegistroTiempo registro) {
        // La hora reconocida ya fue calculada por HorasNormalesStrategy
        // Pero en el orden de ejecución, esta estrategia puede ejecutarse antes,
        // así que recalculamos la reconocida de forma defensiva.
        LocalTime reconocida = marcacionesDelDia.stream()
                .filter(m -> m.getTipo() == Marcacion.TipoMarcacion.ENTRADA)
                .findFirst()
                .map(e -> {
                    LocalTime real = e.getTimestamp().toLocalTime();
                    return real.isBefore(HorasNormalesStrategy.HORA_ENTRADA_OFICIAL)
                            ? HorasNormalesStrategy.HORA_ENTRADA_OFICIAL
                            : real;
                })
                .orElse(null);

        if (reconocida == null) {
            registro.setTardanzaMin(0);
            return;
        }

        if (reconocida.isAfter(LIMITE_TOLERANCIA_ENTRADA)) {
            int tardanza = (int) ChronoUnit.MINUTES.between(LIMITE_TOLERANCIA_ENTRADA, reconocida);
            registro.setTardanzaMin(tardanza);
        } else {
            registro.setTardanzaMin(0);
        }
    }
}
