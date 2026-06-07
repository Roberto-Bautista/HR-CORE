package com.empresa.hrcore.domain.ports;

import com.empresa.hrcore.domain.entities.Marcacion;

public interface AttendanceEventPublisher {
    void publishMarcacionEvent(Marcacion marcacion);
}
