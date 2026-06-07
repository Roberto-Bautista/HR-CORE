package com.empresa.hrcore.domain.patterns.strategy.attendance;

import com.empresa.hrcore.domain.entities.Marcacion;
import com.empresa.hrcore.domain.entities.RegistroTiempo;

import java.util.List;

/**
 * STRATEGY PATTERN - Interfaz para calcular componentes del tiempo trabajado.
 */
public interface CalculadoraHoras {

    /**
     * Calcula una parte del registro de tiempo (horas normales, extras o penalidades)
     * a partir de las marcaciones del día y actualiza el objeto RegistroTiempo.
     *
     * @param marcacionesDelDia Lista de marcaciones ordenadas cronológicamente.
     * @param registro El registro de tiempo que se está construyendo.
     */
    void calcular(List<Marcacion> marcacionesDelDia, RegistroTiempo registro);
}
