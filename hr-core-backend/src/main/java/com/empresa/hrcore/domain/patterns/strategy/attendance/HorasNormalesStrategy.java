package com.empresa.hrcore.domain.patterns.strategy.attendance;

import com.empresa.hrcore.domain.entities.Marcacion;
import com.empresa.hrcore.domain.entities.RegistroTiempo;

import java.time.LocalTime;
import java.util.List;

/**
 * STRATEGY: Horas Efectivas
 *
 * Reglas:
 *   - Jornada laboral = 300 minutos (08:00 – 13:00)
 *   - Hora ingreso reconocida:
 *       Si real < 08:00  → reconocida = 08:00
 *       Si real >= 08:00 → reconocida = real
 *   - horasEfectivasMin = 300 - tardanzaMin - salidaAnticipadaMin
 *   - El tiempo fuera de horario NO incrementa las horas efectivas.
 *
 * Esta estrategia solo calcula la hora reconocida y guarda la hora real de ingreso.
 * Las otras estrategias calculan tardanza, salida anticipada y tiempo fuera de horario.
 * La suma final de horasEfectivas se consolida aquí al final.
 */
public class HorasNormalesStrategy implements CalculadoraHoras {

    // Horario oficial
    public static final LocalTime HORA_ENTRADA_OFICIAL = LocalTime.of(8, 0);
    public static final LocalTime HORA_SALIDA_OFICIAL  = LocalTime.of(13, 0);
    public static final int       JORNADA_MINUTOS      = 300; // 5h = 300 min

    // Ventanas de marcación (con tolerancia de ±5 min)
    public static final LocalTime VENTANA_ENTRADA_INICIO = HORA_ENTRADA_OFICIAL.minusMinutes(5); // 07:55
    public static final LocalTime VENTANA_SALIDA_INICIO  = HORA_SALIDA_OFICIAL.minusMinutes(5);  // 12:55

    @Override
    public void calcular(List<Marcacion> marcacionesDelDia, RegistroTiempo registro) {
        // Registrar hora real de ingreso
        marcacionesDelDia.stream()
                .filter(m -> m.getTipo() == Marcacion.TipoMarcacion.ENTRADA)
                .findFirst()
                .ifPresent(entrada -> {
                    LocalTime horaReal = entrada.getTimestamp().toLocalTime();
                    registro.setHoraIngresoReal(horaReal);

                    // Regla: si llegó antes de las 08:00, la hora reconocida es 08:00
                    LocalTime reconocida = horaReal.isBefore(HORA_ENTRADA_OFICIAL)
                            ? HORA_ENTRADA_OFICIAL
                            : horaReal;
                    registro.setHoraIngresoReconocida(reconocida);
                });

        // Registrar hora real de salida
        marcacionesDelDia.stream()
                .filter(m -> m.getTipo() == Marcacion.TipoMarcacion.SALIDA)
                .findFirst()
                .ifPresent(salida -> registro.setHoraSalidaReal(salida.getTimestamp().toLocalTime()));

        // horasEfectivasMin = 300 - tardanza - salidaAnticipada
        // (tardanza y salidaAnticipada son calculadas por las otras estrategias,
        //  por eso esta estrategia debe ejecutarse ÚLTIMA)
        int efectivos = JORNADA_MINUTOS - registro.getTardanzaMin() - registro.getSalidaAnticipadaMin();
        registro.setHorasEfectivasMin(Math.max(0, efectivos));
    }
}
