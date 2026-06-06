package com.empresa.hrcore.presentation.controllers;

import com.empresa.hrcore.application.services.EmployeeService;
import com.empresa.hrcore.domain.entities.Empleado;
import com.empresa.hrcore.domain.entities.PerfilProfesional;
import com.empresa.hrcore.presentation.dtos.CreateEmployeeRequest;
import com.empresa.hrcore.presentation.dtos.EmployeeResponse;
import com.empresa.hrcore.presentation.dtos.UpdateEmployeeRequest;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * CONTROLADOR REST — API de Empleados
 *
 * En Arquitectura Hexagonal, el Controller es un ADAPTADOR CONDUCTOR
 * (Driving Adapter): recibe peticiones HTTP del frontend y las
 * traduce a llamadas al Servicio de Aplicación (EmployeeService).
 *
 * Endpoints disponibles:
 *   POST   /v1/employees           → Crear empleado
 *   GET    /v1/employees           → Listar todos
 *   GET    /v1/employees/{id}      → Obtener por ID
 *   PUT    /v1/employees/{id}      → Actualizar empleado
 *   PATCH  /v1/employees/{id}/cese → Cesar empleado
 */
@RestController
@RequestMapping("/v1/employees")
@CrossOrigin(origins = "*")   // Permitir llamadas desde el frontend (CORS)
public class EmployeeController {

    private static final Logger log = LoggerFactory.getLogger(EmployeeController.class);

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    // ==========================================================
    // POST /v1/employees — Crear Empleado
    // ==========================================================

    @PostMapping
    public ResponseEntity<EmployeeResponse> createEmployee(
            @Valid @RequestBody CreateEmployeeRequest request) {

        log.info("POST /v1/employees — Crear empleado: {}", request.getCodigo());

        Empleado created = employeeService.crearEmpleado(
                request.getCodigo(),
                request.getNombre(),
                request.getApellido(),
                request.getEmail(),
                request.getTelefono(),
                request.getGenero(),
                request.getCargo(),
                request.getDepartamento(),
                request.getSalario(),
                LocalDate.parse(request.getFechaAlta())
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)    // HTTP 201
                .body(toResponse(created));
    }

    // ==========================================================
    // GET /v1/employees — Listar Empleados
    // ==========================================================

    @GetMapping
    public ResponseEntity<List<EmployeeResponse>> getAllEmployees(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String departamento) {

        log.info("GET /v1/employees — Listar empleados (status={}, depto={})", status, departamento);

        List<Empleado> employees;

        if (status != null && !status.isBlank()) {
            employees = employeeService.listarPorEstado(
                    Empleado.EstadoEmpleado.valueOf(status.toUpperCase()));
        } else if (departamento != null && !departamento.isBlank()) {
            employees = employeeService.listarPorDepartamento(departamento);
        } else {
            employees = employeeService.listarTodos();
        }

        List<EmployeeResponse> response = employees.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    // ==========================================================
    // GET /v1/employees/{id} — Obtener por ID
    // ==========================================================

    @GetMapping("/{id}")
    public ResponseEntity<EmployeeResponse> getEmployeeById(@PathVariable UUID id) {

        log.info("GET /v1/employees/{} — Obtener empleado", id);

        Empleado employee = employeeService.obtenerPorId(id);
        return ResponseEntity.ok(toResponse(employee));
    }

    // ==========================================================
    // PUT /v1/employees/{id} — Actualizar Empleado
    // ==========================================================

    @PutMapping("/{id}")
    public ResponseEntity<EmployeeResponse> updateEmployee(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateEmployeeRequest request) {

        log.info("PUT /v1/employees/{} — Actualizar empleado", id);

        Empleado updated = employeeService.actualizarEmpleado(
                id,
                request.getNombre(),
                request.getApellido(),
                request.getEmail(),
                request.getTelefono(),
                request.getCargo(),
                request.getDepartamento(),
                request.getSalario()
        );

        return ResponseEntity.ok(toResponse(updated));
    }

    // ==========================================================
    // PATCH /v1/employees/{id}/cese — Cesar Empleado
    // ==========================================================

    @PatchMapping("/{id}/cese")
    public ResponseEntity<EmployeeResponse> ceseEmployee(
            @PathVariable UUID id,
            @RequestParam String fechaCese) {

        log.info("PATCH /v1/employees/{}/cese — Cesar empleado (fecha: {})", id, fechaCese);

        Empleado cesado = employeeService.cesarEmpleado(id, LocalDate.parse(fechaCese));
        return ResponseEntity.ok(toResponse(cesado));
    }

    // ==========================================================
    // GET /v1/employees/stats — Estadísticas para Dashboard
    // ==========================================================

    @GetMapping("/stats")
    public ResponseEntity<StatsResponse> getStats() {
        long total   = employeeService.contarEmpleados();
        long activos = employeeService.contarEmpleadosActivos();

        StatsResponse stats = new StatsResponse();
        stats.totalEmpleados    = total;
        stats.empleadosActivos  = activos;
        stats.empleadosInactivos = total - activos;

        return ResponseEntity.ok(stats);
    }

    // Clase interna para la respuesta de estadísticas
    static class StatsResponse {
        public long totalEmpleados;
        public long empleadosActivos;
        public long empleadosInactivos;
    }

    // ==========================================================
    // MAPEADOR — Dominio → DTO de Respuesta
    // ==========================================================

    private EmployeeResponse toResponse(Empleado domain) {
        EmployeeResponse dto = new EmployeeResponse();
        dto.setId(domain.getId());
        dto.setCodigo(domain.getCodigo());
        dto.setNombre(domain.getNombre());
        dto.setApellido(domain.getApellido());
        dto.setNombreCompleto(domain.getNombre() + " " + domain.getApellido());
        dto.setEmail(domain.getEmail());
        dto.setTelefono(domain.getTelefono());
        dto.setGenero(domain.getGenero());
        dto.setCargo(domain.getCargo());
        dto.setDepartamento(domain.getDepartamento());
        dto.setSalario(domain.getSalario());
        dto.setFechaAlta(domain.getFechaAlta());
        dto.setFechaCese(domain.getFechaCese());
        dto.setStatus(domain.getStatus() != null ? domain.getStatus().name() : null);
        dto.setCreatedAt(domain.getCreatedAt());
        dto.setUpdatedAt(domain.getUpdatedAt());

        if (domain.getPerfilProfesional() != null) {
            PerfilProfesional p = domain.getPerfilProfesional();
            EmployeeResponse.PerfilResponse perfilDto = new EmployeeResponse.PerfilResponse();
            perfilDto.setNivelEducativo(p.getNivelEducativo());
            perfilDto.setEspecialidad(p.getEspecialidad());
            perfilDto.setHabilidades(p.getHabilidades());
            perfilDto.setCertificaciones(p.getCertificaciones());
            perfilDto.setLinkedinUrl(p.getLinkedinUrl());
            dto.setPerfilProfesional(perfilDto);
        }

        return dto;
    }
}
