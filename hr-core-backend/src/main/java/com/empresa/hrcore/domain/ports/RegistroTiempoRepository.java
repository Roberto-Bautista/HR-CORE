package com.empresa.hrcore.domain.ports;

import com.empresa.hrcore.domain.entities.RegistroTiempo;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RegistroTiempoRepository {
    RegistroTiempo save(RegistroTiempo registroTiempo);
    Optional<RegistroTiempo> findByEmpleadoIdAndFecha(UUID empleadoId, LocalDate fecha);
    List<RegistroTiempo> findByEmpleadoIdBetweenDates(UUID empleadoId, LocalDate desde, LocalDate hasta);
}
