package com.empresa.hrcore.infrastructure.persistence.repositories;

import com.empresa.hrcore.domain.entities.RegistroTiempo;
import com.empresa.hrcore.domain.ports.RegistroTiempoRepository;
import com.empresa.hrcore.infrastructure.persistence.jpa.RegistroTiempoJpaEntity;
import com.empresa.hrcore.infrastructure.persistence.jpa.RegistroTiempoJpaRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class RegistroTiempoRepositoryImpl implements RegistroTiempoRepository {

    private final RegistroTiempoJpaRepository jpaRepository;

    public RegistroTiempoRepositoryImpl(RegistroTiempoJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public RegistroTiempo save(RegistroTiempo registroTiempo) {
        RegistroTiempoJpaEntity entity = toEntity(registroTiempo);

        // Si ya existe un registro para la misma fecha y empleado → actualizar (no duplicar)
        jpaRepository.findByEmpleadoIdAndFecha(registroTiempo.getEmpleadoId(), registroTiempo.getFecha())
                .ifPresent(existing -> entity.setId(existing.getId()));

        RegistroTiempoJpaEntity saved = jpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<RegistroTiempo> findByEmpleadoIdAndFecha(UUID empleadoId, LocalDate fecha) {
        return jpaRepository.findByEmpleadoIdAndFecha(empleadoId, fecha)
                .map(this::toDomain);
    }

    @Override
    public List<RegistroTiempo> findByEmpleadoIdBetweenDates(UUID empleadoId, LocalDate desde, LocalDate hasta) {
        return jpaRepository.findByEmpleadoIdAndFechaBetweenOrderByFechaAsc(empleadoId, desde, hasta)
                .stream().map(this::toDomain).collect(Collectors.toList());
    }

    private RegistroTiempoJpaEntity toEntity(RegistroTiempo d) {
        RegistroTiempoJpaEntity e = new RegistroTiempoJpaEntity();
        e.setId(d.getId());
        e.setEmpleadoId(d.getEmpleadoId());
        e.setFecha(d.getFecha());
        e.setHoraIngresoReal(d.getHoraIngresoReal());
        e.setHoraIngresoReconocida(d.getHoraIngresoReconocida());
        e.setHoraSalidaReal(d.getHoraSalidaReal());
        e.setTardanzaMin(d.getTardanzaMin());
        e.setSalidaAnticipadaMin(d.getSalidaAnticipadaMin());
        e.setTiempoFueraDeHorarioMin(d.getTiempoFueraDeHorarioMin());
        e.setHorasEfectivasMin(d.getHorasEfectivasMin());
        return e;
    }

    private RegistroTiempo toDomain(RegistroTiempoJpaEntity e) {
        RegistroTiempo d = new RegistroTiempo();
        d.setId(e.getId());
        d.setEmpleadoId(e.getEmpleadoId());
        d.setFecha(e.getFecha());
        d.setHoraIngresoReal(e.getHoraIngresoReal());
        d.setHoraIngresoReconocida(e.getHoraIngresoReconocida());
        d.setHoraSalidaReal(e.getHoraSalidaReal());
        d.setTardanzaMin(e.getTardanzaMin());
        d.setSalidaAnticipadaMin(e.getSalidaAnticipadaMin());
        d.setTiempoFueraDeHorarioMin(e.getTiempoFueraDeHorarioMin());
        d.setHorasEfectivasMin(e.getHorasEfectivasMin());
        return d;
    }
}
