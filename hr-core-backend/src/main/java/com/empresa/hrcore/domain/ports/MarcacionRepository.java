package com.empresa.hrcore.domain.ports;

import com.empresa.hrcore.domain.entities.Marcacion;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface MarcacionRepository {
    Marcacion save(Marcacion marcacion);
    List<Marcacion> findByEmpleadoIdAndFecha(UUID empleadoId, LocalDate fecha);
    List<Marcacion> findByEmpleadoIdBetweenDates(UUID empleadoId, LocalDate desde, LocalDate hasta);
}
