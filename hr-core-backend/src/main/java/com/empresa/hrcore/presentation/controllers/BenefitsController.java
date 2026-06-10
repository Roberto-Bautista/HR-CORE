package com.empresa.hrcore.presentation.controllers;

import com.empresa.hrcore.application.usecases.BenefitsUseCase;
import com.empresa.hrcore.domain.entities.Beneficio;
import com.empresa.hrcore.domain.entities.Enrolamiento;
import com.empresa.hrcore.presentation.dtos.BenefitResponse;
import com.empresa.hrcore.presentation.dtos.EnrollBenefitRequest;
import com.empresa.hrcore.presentation.dtos.EnrollmentResponse;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * CONTROLADOR REST — API de Beneficios
 *
 * Endpoints:
 *   GET    /v1/benefits?employeeId={uuid}  → Catálogo de beneficios (opcionalmente evalúa elegibilidad y enrolamiento)
 *   POST   /v1/benefits/enroll            → Enrolar empleado en beneficio
 *   DELETE /v1/benefits/enrollments/{id}   → Cancelar enrolamiento
 *   GET    /v1/benefits/employees/{id}    → Listar enrolamientos de un empleado
 *   POST   /v1/benefits                   → Crear nuevo beneficio (Catálogo - Admin)
 */
@RestController
@RequestMapping("/v1/benefits")
@CrossOrigin(origins = "*")
public class BenefitsController {

    private static final Logger log = LoggerFactory.getLogger(BenefitsController.class);

    private final BenefitsUseCase benefitsUseCase;

    public BenefitsController(BenefitsUseCase benefitsUseCase) {
        this.benefitsUseCase = benefitsUseCase;
    }

    // ==========================================================
    // GET /v1/benefits — Obtener catálogo
    // ==========================================================
    @GetMapping
    public ResponseEntity<List<BenefitResponse>> getBenefitsCatalog(
            @RequestParam(required = false) UUID employeeId) {
        
        log.info("GET /v1/benefits — Listar catálogo (employeeId={})", employeeId);

        List<Beneficio> catalog = benefitsUseCase.listarCatalogo();

        // Si se provee employeeId, enriquecer el DTO con banderas de elegibilidad y enrolamiento
        if (employeeId != null) {
            List<Enrolamiento> activeEnrollments = benefitsUseCase.listarEnrolamientosEmpleado(employeeId);

            List<BenefitResponse> response = catalog.stream().map(b -> {
                BenefitResponse dto = toResponse(b);
                
                // Verificar si ya está enrolado
                Optional<Enrolamiento> enrollmentOpt = activeEnrollments.stream()
                        .filter(e -> e.getBenefitId().equals(b.getId()))
                        .findFirst();

                boolean yaEnrolado = enrollmentOpt.isPresent();
                dto.setYaEnrolado(yaEnrolado);
                if (yaEnrolado) {
                    dto.setEnrolamientoId(enrollmentOpt.get().getId());
                }

                // Evaluar elegibilidad
                dto.setEsElegible(benefitsUseCase.esElegible(employeeId, b.getId()));
                return dto;
            }).collect(Collectors.toList());

            return ResponseEntity.ok(response);
        }

        // De lo contrario, devolver catálogo plano
        List<BenefitResponse> response = catalog.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    // ==========================================================
    // POST /v1/benefits/enroll — Afiliar empleado
    // ==========================================================
    @PostMapping("/enroll")
    public ResponseEntity<EnrollmentResponse> enrollEmployee(
            @Valid @RequestBody EnrollBenefitRequest request) {

        log.info("POST /v1/benefits/enroll — Enrolar empleado {} en beneficio {}", 
                request.getEmployeeId(), request.getBenefitId());

        Enrolamiento enrollment = benefitsUseCase.enrolar(request.getEmployeeId(), request.getBenefitId());
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(enrollment));
    }

    // ==========================================================
    // DELETE /v1/benefits/enrollments/{id} — Cancelar afiliación
    // ==========================================================
    @DeleteMapping("/enrollments/{enrollmentId}")
    public ResponseEntity<Void> unenrollEmployee(@PathVariable UUID enrollmentId) {
        log.info("DELETE /v1/benefits/enrollments/{} — Desafiliar", enrollmentId);
        benefitsUseCase.desenrolar(enrollmentId);
        return ResponseEntity.noContent().build();
    }

    // ==========================================================
    // GET /v1/benefits/employees/{id} — Afiliaciones de un empleado
    // ==========================================================
    @GetMapping("/employees/{employeeId}")
    public ResponseEntity<List<EnrollmentResponse>> getEmployeeEnrollments(@PathVariable UUID employeeId) {
        log.info("GET /v1/benefits/employees/{} — Listar afiliaciones", employeeId);
        List<Enrolamiento> list = benefitsUseCase.listarEnrolamientosEmpleado(employeeId);
        List<EnrollmentResponse> response = list.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    // ==========================================================
    // POST /v1/benefits — Crear beneficio (Admin/Mantenimiento)
    // ==========================================================
    @PostMapping
    public ResponseEntity<BenefitResponse> createBenefit(@RequestBody @Valid Beneficio request) {
        log.info("POST /v1/benefits — Crear beneficio: {}", request.getNombre());
        Beneficio created = benefitsUseCase.crearBeneficio(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(created));
    }

    // ==========================================================
    // MAPEADORES
    // ==========================================================

    private BenefitResponse toResponse(Beneficio domain) {
        if (domain == null) return null;
        BenefitResponse dto = new BenefitResponse();
        dto.setId(domain.getId());
        dto.setNombre(domain.getNombre());
        dto.setDescripcion(domain.getDescripcion());
        dto.setCosto(domain.getCosto());
        dto.setActivo(domain.isActivo());
        dto.setRequiereSalarioMin(domain.getRequiereSalarioMin());
        dto.setRequiereSalarioMax(domain.getRequiereSalarioMax());
        dto.setRequiereAntiguedadMeses(domain.getRequiereAntiguedadMeses());
        dto.setRequiereCargo(domain.getRequiereCargo());
        return dto;
    }

    private EnrollmentResponse toResponse(Enrolamiento domain) {
        if (domain == null) return null;
        EnrollmentResponse dto = new EnrollmentResponse();
        dto.setId(domain.getId());
        dto.setEmployeeId(domain.getEmployeeId());
        dto.setBenefitId(domain.getBenefitId());
        dto.setFechaEnrolamiento(domain.getFechaEnrolamiento());
        dto.setEstado(domain.getEstado());
        if (domain.getBeneficio() != null) {
            dto.setBeneficio(toResponse(domain.getBeneficio()));
        }
        return dto;
    }
}
