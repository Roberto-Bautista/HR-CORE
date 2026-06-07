package com.empresa.hrcore.infrastructure.adapters;

import com.empresa.hrcore.domain.entities.Empleado;
import com.empresa.hrcore.domain.ports.EmployeeCachePort;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

@Component
public class EmployeeCacheRepository implements EmployeeCachePort {

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;
    private static final String CACHE_PREFIX = "employee:";
    private static final Duration CACHE_TTL = Duration.ofHours(12);

    public EmployeeCacheRepository(StringRedisTemplate redisTemplate, ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public Optional<Empleado> getEmployeeFromCache(UUID employeeId) {
        String key = CACHE_PREFIX + employeeId.toString();
        String json = redisTemplate.opsForValue().get(key);
        
        if (json != null) {
            try {
                Empleado emp = objectMapper.readValue(json, Empleado.class);
                return Optional.of(emp);
            } catch (JsonProcessingException e) {
                // Si hay error de parsing, ignorar caché
            }
        }
        return Optional.empty();
    }

    @Override
    public void saveEmployeeToCache(Empleado employee) {
        if (employee == null || employee.getId() == null) return;
        
        String key = CACHE_PREFIX + employee.getId().toString();
        try {
            String json = objectMapper.writeValueAsString(employee);
            redisTemplate.opsForValue().set(key, json, CACHE_TTL);
        } catch (JsonProcessingException e) {
            // Ignorar error de escritura en caché
        }
    }
}
