package com.empresa.hrcore.domain.ports;

import com.empresa.hrcore.domain.entities.Empleado;

import java.util.Optional;
import java.util.UUID;

public interface EmployeeCachePort {
    Optional<Empleado> getEmployeeFromCache(UUID employeeId);
    void saveEmployeeToCache(Empleado employee);
}
