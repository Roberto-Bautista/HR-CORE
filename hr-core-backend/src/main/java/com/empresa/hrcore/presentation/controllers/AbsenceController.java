package com.empresa.hrcore.presentation.controllers;

import com.empresa.hrcore.application.services.AbsenceService;
import com.empresa.hrcore.domain.entities.SaldoVacacion;
import com.empresa.hrcore.domain.entities.SolicitudVacacion;
import com.empresa.hrcore.presentation.dtos.SolicitudVacacionRequestDTO;
import com.empresa.hrcore.presentation.dtos.SolicitudVacacionResponseDTO;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * CONTROLADOR REST — API de Vacaciones
 *
 * Endpoints:
 *   POST   /v1/absences                     → Crear solicitud
 *   GET    /v1/absences                     → Listar todas (admin)
 *   GET    /v1/absences/employee/{empId}    → Listar por empleado
 *   GET    /v1/absences/pending             → Listar pendientes (bandeja)
 *   PATCH  /v1/absences/{id}/enviar         → Enviar solicitud
 *   PATCH  /v1/absences/{id}/aprobar        → Aprobar solicitud
 *   PATCH  /v1/absences/{id}/rechazar       → Rechazar solicitud
 *   DELETE /v1/absences/{id}                → Eliminar borrador
 *   GET    /v1/absences/balance/{empId}     → Obtener saldo
 *   GET    /v1/absences/stats               → Estadísticas
 */
@RestController
@RequestMapping("/v1/absences")
@CrossOrigin(origins = "*")
public class AbsenceController {

    private static final Logger log = LoggerFactory.getLogger(AbsenceController.class);

    private final AbsenceService absenceService;

    public AbsenceController(AbsenceService absenceService) {
        this.absenceService = absenceService;
    }

    // ==========================================================
    // POST /v1/absences — Crear Solicitud
    // ==========================================================

    @PostMapping
    public ResponseEntity<SolicitudVacacionResponseDTO> createAbsence(
            @Valid @RequestBody SolicitudVacacionRequestDTO request) {

        log.info("POST /v1/absences — Crear solicitud para empleado: {}", request.getEmpleadoId());

        SolicitudVacacion created = absenceService.crearSolicitud(
                request.getEmpleadoId(),
                LocalDate.parse(request.getFechaInicio()),
                LocalDate.parse(request.getFechaFin())
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(created));
    }

    // ==========================================================
    // GET /v1/absences — Listar Todas (Admin)
    // ==========================================================

    @GetMapping
    public ResponseEntity<List<SolicitudVacacionResponseDTO>> getAllAbsences() {
        log.info("GET /v1/absences — Listar todas las solicitudes");

        List<SolicitudVacacionResponseDTO> response = absenceService.listarTodas()
                .stream().map(this::toResponse).collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    // ==========================================================
    // GET /v1/absences/employee/{empId} — Listar por Empleado
    // ==========================================================

    @GetMapping("/employee/{empId}")
    public ResponseEntity<List<SolicitudVacacionResponseDTO>> getAbsencesByEmployee(
            @PathVariable UUID empId) {

        log.info("GET /v1/absences/employee/{} — Listar solicitudes del empleado", empId);

        List<SolicitudVacacionResponseDTO> response = absenceService.listarPorEmpleado(empId)
                .stream().map(this::toResponse).collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    // ==========================================================
    // GET /v1/absences/pending — Listar Pendientes (Bandeja Admin)
    // ==========================================================

    @GetMapping("/pending")
    public ResponseEntity<List<SolicitudVacacionResponseDTO>> getPendingAbsences() {
        log.info("GET /v1/absences/pending — Listar solicitudes pendientes");

        List<SolicitudVacacionResponseDTO> response = absenceService.listarPendientes()
                .stream().map(this::toResponse).collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    // ==========================================================
    // PATCH /v1/absences/{id}/enviar — Enviar Solicitud
    // ==========================================================

    @PatchMapping("/{id}/enviar")
    public ResponseEntity<SolicitudVacacionResponseDTO> sendAbsence(@PathVariable UUID id) {
        log.info("PATCH /v1/absences/{}/enviar", id);

        SolicitudVacacion updated = absenceService.enviarSolicitud(id);
        return ResponseEntity.ok(toResponse(updated));
    }

    // ==========================================================
    // PATCH /v1/absences/{id}/aprobar — Aprobar Solicitud
    // ==========================================================

    @PatchMapping("/{id}/aprobar")
    public ResponseEntity<SolicitudVacacionResponseDTO> approveAbsence(@PathVariable UUID id) {
        log.info("PATCH /v1/absences/{}/aprobar", id);

        SolicitudVacacion updated = absenceService.aprobarSolicitud(id);
        return ResponseEntity.ok(toResponse(updated));
    }

    // ==========================================================
    // PATCH /v1/absences/{id}/rechazar — Rechazar Solicitud
    // ==========================================================

    @PatchMapping("/{id}/rechazar")
    public ResponseEntity<SolicitudVacacionResponseDTO> rejectAbsence(
            @PathVariable UUID id,
            @RequestBody Map<String, String> body) {

        String motivo = body.getOrDefault("motivo", "Sin motivo especificado");
        log.info("PATCH /v1/absences/{}/rechazar — Motivo: {}", id, motivo);

        SolicitudVacacion updated = absenceService.rechazarSolicitud(id, motivo);
        return ResponseEntity.ok(toResponse(updated));
    }

    // ==========================================================
    // DELETE /v1/absences/{id} — Eliminar Borrador
    // ==========================================================

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDraft(@PathVariable UUID id) {
        log.info("DELETE /v1/absences/{} — Eliminar borrador", id);

        absenceService.eliminarBorrador(id);
        return ResponseEntity.noContent().build();
    }

    // ==========================================================
    // GET /v1/absences/balance/{empId} — Obtener Saldo
    // ==========================================================

    @GetMapping("/balance/{empId}")
    public ResponseEntity<BalanceResponse> getBalance(
            @PathVariable UUID empId,
            @RequestParam(required = false) Integer anio) {

        int year = (anio != null) ? anio : LocalDate.now().getYear();
        log.info("GET /v1/absences/balance/{} — Año: {}", empId, year);

        SaldoVacacion saldo = absenceService.obtenerSaldo(empId, year);

        BalanceResponse response = new BalanceResponse();
        response.empleadoId = empId;
        response.anio = year;
        response.diasTotales = saldo.getDiasTotales();
        response.diasUsados = saldo.getDiasUsados();
        response.diasDisponibles = saldo.getDiasDisponibles();

        return ResponseEntity.ok(response);
    }

    // ==========================================================
    // GET /v1/absences/stats — Estadísticas
    // ==========================================================

    @GetMapping("/stats")
    public ResponseEntity<StatsResponse> getStats() {
        StatsResponse stats = new StatsResponse();
        stats.pendientes = absenceService.contarPendientes();
        stats.aprobadas = absenceService.contarAprobadas();
        stats.rechazadas = absenceService.contarRechazadas();
        return ResponseEntity.ok(stats);
    }

    // ==========================================================
    // Clases internas de respuesta
    // ==========================================================

    static class BalanceResponse {
        public UUID empleadoId;
        public int anio;
        public int diasTotales;
        public int diasUsados;
        public int diasDisponibles;
    }

    static class StatsResponse {
        public long pendientes;
        public long aprobadas;
        public long rechazadas;
    }

    // ==========================================================
    // MAPEADOR — Dominio → DTO de Respuesta
    // ==========================================================

    private SolicitudVacacionResponseDTO toResponse(SolicitudVacacion domain) {
        SolicitudVacacionResponseDTO dto = new SolicitudVacacionResponseDTO();
        dto.setId(domain.getId());
        dto.setEmpleadoId(domain.getEmpleadoId());
        dto.setNombreEmpleado(domain.getNombreEmpleado());
        dto.setFechaInicio(domain.getFechaInicio() != null ? domain.getFechaInicio().toString() : null);
        dto.setFechaFin(domain.getFechaFin() != null ? domain.getFechaFin().toString() : null);
        dto.setDiasSolicitados(domain.getDiasSolicitados());
        dto.setEstado(domain.getEstadoNombre());
        dto.setMotivoRechazo(domain.getMotivoRechazo());
        dto.setCreatedAt(domain.getCreatedAt() != null ? domain.getCreatedAt().toString() : null);
        dto.setUpdatedAt(domain.getUpdatedAt() != null ? domain.getUpdatedAt().toString() : null);
        return dto;
    }
}
