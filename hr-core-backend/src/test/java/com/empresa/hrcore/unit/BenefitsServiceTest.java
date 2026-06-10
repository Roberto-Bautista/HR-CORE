package com.empresa.hrcore.unit;

import com.empresa.hrcore.application.services.BenefitsService;
import com.empresa.hrcore.domain.entities.Empleado;
import com.empresa.hrcore.domain.entities.Beneficio;
import com.empresa.hrcore.domain.entities.Enrolamiento;
import com.empresa.hrcore.domain.exceptions.EmpleadoNoElegibleException;
import com.empresa.hrcore.domain.ports.IBenefitsRepository;
import com.empresa.hrcore.domain.ports.IEmployeeRepository;
import com.empresa.hrcore.presentation.exceptions.ConflictException;
import com.empresa.hrcore.presentation.exceptions.ResourceNotFoundException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BenefitsServiceTest {

    @Mock
    private IBenefitsRepository benefitsRepository;

    @Mock
    private IEmployeeRepository employeeRepository;

    @InjectMocks
    private BenefitsService benefitsService;

    private Empleado empleadoComun;
    private Empleado gerente;
    private Beneficio beneficioGerente;
    private Beneficio beneficioAntiguedad;
    private Beneficio beneficioSueldo;
    private Beneficio beneficioLibre;

    @BeforeEach
    void setUp() {
        // 1. Crear Empleados
        empleadoComun = new Empleado();
        empleadoComun.setId(UUID.randomUUID());
        empleadoComun.setNombre("Carlos");
        empleadoComun.setApellido("Soto");
        empleadoComun.setSalario(new BigDecimal("3500.00"));
        empleadoComun.setCargo("Analista");
        empleadoComun.setFechaAlta(LocalDate.now().minusMonths(3)); // 3 meses de antigüedad
        empleadoComun.setStatus(Empleado.EstadoEmpleado.ACTIVO);

        gerente = new Empleado();
        gerente.setId(UUID.randomUUID());
        gerente.setNombre("Marta");
        gerente.setApellido("Gomez");
        gerente.setSalario(new BigDecimal("9500.00"));
        gerente.setCargo("Gerente");
        gerente.setFechaAlta(LocalDate.now().minusYears(2)); // 24 meses de antigüedad
        gerente.setStatus(Empleado.EstadoEmpleado.ACTIVO);

        // 2. Crear Beneficios
        beneficioGerente = new Beneficio(UUID.randomUUID(), "EPS Premium", "Seguro de salud EPS para Gerentes", new BigDecimal("150.00"), true);
        beneficioGerente.setRequiereCargo("Gerente");

        beneficioAntiguedad = new Beneficio(UUID.randomUUID(), "Bono TI", "Bono para certificaciones", new BigDecimal("0.00"), true);
        beneficioAntiguedad.setRequiereAntiguedadMeses(6);

        beneficioSueldo = new Beneficio(UUID.randomUUID(), "Vales Sodexo", "Vales de alimentación", new BigDecimal("0.00"), true);
        beneficioSueldo.setRequiereSalarioMax(new BigDecimal("4000.00"));

        beneficioLibre = new Beneficio(UUID.randomUUID(), "Mentoría", "Programa libre de mentoría", new BigDecimal("0.00"), true);
    }

    @Test
    void testElegibilidadPorCargoExitoso() {
        when(employeeRepository.findById(gerente.getId())).thenReturn(Optional.of(gerente));
        when(benefitsRepository.findById(beneficioGerente.getId())).thenReturn(Optional.of(beneficioGerente));

        assertTrue(benefitsService.esElegible(gerente.getId(), beneficioGerente.getId()));
    }

    @Test
    void testElegibilidadPorCargoRechazado() {
        when(employeeRepository.findById(empleadoComun.getId())).thenReturn(Optional.of(empleadoComun));
        when(benefitsRepository.findById(beneficioGerente.getId())).thenReturn(Optional.of(beneficioGerente));

        assertFalse(benefitsService.esElegible(empleadoComun.getId(), beneficioGerente.getId()));
    }

    @Test
    void testElegibilidadPorAntiguedadExitoso() {
        when(employeeRepository.findById(gerente.getId())).thenReturn(Optional.of(gerente));
        when(benefitsRepository.findById(beneficioAntiguedad.getId())).thenReturn(Optional.of(beneficioAntiguedad));

        assertTrue(benefitsService.esElegible(gerente.getId(), beneficioAntiguedad.getId()));
    }

    @Test
    void testElegibilidadPorAntiguedadRechazado() {
        when(employeeRepository.findById(empleadoComun.getId())).thenReturn(Optional.of(empleadoComun));
        when(benefitsRepository.findById(beneficioAntiguedad.getId())).thenReturn(Optional.of(beneficioAntiguedad));

        assertFalse(benefitsService.esElegible(empleadoComun.getId(), beneficioAntiguedad.getId()));
    }

    @Test
    void testElegibilidadPorSalarioExitoso() {
        when(employeeRepository.findById(empleadoComun.getId())).thenReturn(Optional.of(empleadoComun));
        when(benefitsRepository.findById(beneficioSueldo.getId())).thenReturn(Optional.of(beneficioSueldo));

        assertTrue(benefitsService.esElegible(empleadoComun.getId(), beneficioSueldo.getId()));
    }

    @Test
    void testElegibilidadPorSalarioRechazado() {
        when(employeeRepository.findById(gerente.getId())).thenReturn(Optional.of(gerente));
        when(benefitsRepository.findById(beneficioSueldo.getId())).thenReturn(Optional.of(beneficioSueldo));

        assertFalse(benefitsService.esElegible(gerente.getId(), beneficioSueldo.getId()));
    }

    @Test
    void testElegibilidadLibre() {
        when(employeeRepository.findById(empleadoComun.getId())).thenReturn(Optional.of(empleadoComun));
        when(benefitsRepository.findById(beneficioLibre.getId())).thenReturn(Optional.of(beneficioLibre));

        assertTrue(benefitsService.esElegible(empleadoComun.getId(), beneficioLibre.getId()));
    }

    @Test
    void testEnrolarExitoso() {
        UUID empId = empleadoComun.getId();
        UUID benId = beneficioSueldo.getId();

        when(employeeRepository.findById(empId)).thenReturn(Optional.of(empleadoComun));
        when(benefitsRepository.findById(benId)).thenReturn(Optional.of(beneficioSueldo));
        when(benefitsRepository.existsEnrollmentByEmpleadoIdAndBeneficioId(empId, benId)).thenReturn(false);
        
        when(benefitsRepository.saveEnrollment(any(Enrolamiento.class))).thenAnswer(inv -> {
            Enrolamiento e = inv.getArgument(0);
            e.setId(UUID.randomUUID());
            return e;
        });

        Enrolamiento result = benefitsService.enrolar(empId, benId);

        assertNotNull(result);
        assertEquals(empId, result.getEmployeeId());
        assertEquals(benId, result.getBenefitId());
        assertEquals("ACTIVO", result.getEstado());
        verify(benefitsRepository, times(1)).saveEnrollment(any(Enrolamiento.class));
    }

    @Test
    void testEnrolarEmpleadoNoElegible() {
        UUID empId = empleadoComun.getId();
        UUID benId = beneficioGerente.getId();

        when(employeeRepository.findById(empId)).thenReturn(Optional.of(empleadoComun));
        when(benefitsRepository.findById(benId)).thenReturn(Optional.of(beneficioGerente));
        when(benefitsRepository.existsEnrollmentByEmpleadoIdAndBeneficioId(empId, benId)).thenReturn(false);

        assertThrows(EmpleadoNoElegibleException.class, () -> {
            benefitsService.enrolar(empId, benId);
        });

        verify(benefitsRepository, never()).saveEnrollment(any(Enrolamiento.class));
    }

    @Test
    void testEnrolarYaAfiliado() {
        UUID empId = empleadoComun.getId();
        UUID benId = beneficioSueldo.getId();

        when(employeeRepository.findById(empId)).thenReturn(Optional.of(empleadoComun));
        when(benefitsRepository.findById(benId)).thenReturn(Optional.of(beneficioSueldo));
        when(benefitsRepository.existsEnrollmentByEmpleadoIdAndBeneficioId(empId, benId)).thenReturn(true);

        assertThrows(ConflictException.class, () -> {
            benefitsService.enrolar(empId, benId);
        });

        verify(benefitsRepository, never()).saveEnrollment(any(Enrolamiento.class));
    }

    @Test
    void testEnrolarEmpleadoInactivo() {
        UUID empId = empleadoComun.getId();
        UUID benId = beneficioLibre.getId();

        empleadoComun.setStatus(Empleado.EstadoEmpleado.CESADO);

        when(employeeRepository.findById(empId)).thenReturn(Optional.of(empleadoComun));

        assertThrows(EmpleadoNoElegibleException.class, () -> {
            benefitsService.enrolar(empId, benId);
        });

        verify(benefitsRepository, never()).saveEnrollment(any(Enrolamiento.class));
    }

    @Test
    void testDesenrolarExitoso() {
        UUID enrollmentId = UUID.randomUUID();
        Enrolamiento enrol = new Enrolamiento(empleadoComun.getId(), beneficioLibre.getId());
        enrol.setId(enrollmentId);

        when(benefitsRepository.findEnrollmentById(enrollmentId)).thenReturn(Optional.of(enrol));

        benefitsService.desenrolar(enrollmentId);

        verify(benefitsRepository, times(1)).deleteEnrollmentById(enrollmentId);
    }

    @Test
    void testDesenrolarNoEncontrado() {
        UUID enrollmentId = UUID.randomUUID();
        when(benefitsRepository.findEnrollmentById(enrollmentId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            benefitsService.desenrolar(enrollmentId);
        });

        verify(benefitsRepository, never()).deleteEnrollmentById(any(UUID.class));
    }
}
