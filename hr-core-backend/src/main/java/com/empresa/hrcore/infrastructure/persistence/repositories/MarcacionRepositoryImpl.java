package com.empresa.hrcore.infrastructure.persistence.repositories;

import com.empresa.hrcore.domain.entities.Marcacion;
import com.empresa.hrcore.domain.ports.MarcacionRepository;
import com.empresa.hrcore.infrastructure.persistence.jpa.MarcacionJpaEntity;
import com.empresa.hrcore.infrastructure.persistence.jpa.MarcacionJpaRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class MarcacionRepositoryImpl implements MarcacionRepository {

    private final MarcacionJpaRepository jpaRepository;

    public MarcacionRepositoryImpl(MarcacionJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Marcacion save(Marcacion marcacion) {
        MarcacionJpaEntity entity = toEntity(marcacion);
        MarcacionJpaEntity saved = jpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public List<Marcacion> findByEmpleadoIdAndFecha(UUID empleadoId, LocalDate fecha) {
        LocalDateTime startOfDay = fecha.atStartOfDay();
        LocalDateTime endOfDay = fecha.atTime(LocalTime.MAX);
        
        List<MarcacionJpaEntity> entities = jpaRepository.findByEmpleadoIdAndTimestampBetweenOrderByTimestampAsc(
                empleadoId, startOfDay, endOfDay);
                
        return entities.stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Marcacion> findByEmpleadoIdBetweenDates(UUID empleadoId, LocalDate desde, LocalDate hasta) {
        LocalDateTime startOfFrom = desde.atStartOfDay();
        LocalDateTime endOfTo = hasta.atTime(LocalTime.MAX);

        List<MarcacionJpaEntity> entities = jpaRepository.findByEmpleadoIdAndTimestampBetweenOrderByTimestampAsc(
                empleadoId, startOfFrom, endOfTo);

        return entities.stream().map(this::toDomain).collect(Collectors.toList());
    }

    private MarcacionJpaEntity toEntity(Marcacion domain) {
        MarcacionJpaEntity entity = new MarcacionJpaEntity();
        entity.setId(domain.getId());
        entity.setEmpleadoId(domain.getEmpleadoId());
        entity.setTipo(domain.getTipo().name());
        entity.setTimestamp(domain.getTimestamp());
        return entity;
    }

    private Marcacion toDomain(MarcacionJpaEntity entity) {
        Marcacion domain = new Marcacion();
        domain.setId(entity.getId());
        domain.setEmpleadoId(entity.getEmpleadoId());
        domain.setTipo(Marcacion.TipoMarcacion.valueOf(entity.getTipo()));
        domain.setTimestamp(entity.getTimestamp());
        return domain;
    }
}
