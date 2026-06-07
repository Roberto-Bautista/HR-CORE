package com.empresa.hrcore.unit;

import com.empresa.hrcore.application.services.AbsenceService;
import com.empresa.hrcore.domain.entities.Empleado;
import com.empresa.hrcore.domain.entities.SaldoVacacion;
import com.empresa.hrcore.domain.entities.SolicitudVacacion;
import com.empresa.hrcore.domain.exceptions.SaldoInsuficienteException;
import com.empresa.hrcore.domain.exceptions.EstadoTransicionInvalidaException;
import com.empresa.hrcore.domain.ports.IEmployeeRepository;
import com.empresa.hrcore.infrastructure.persistence.jpa.SaldoVacacionJpaEntity;
import com.empresa.hrcore.infrastructure.persistence.jpa.SaldoVacacionJpaRepository;
import com.empresa.hrcore.infrastructure.persistence.jpa.SolicitudVacacionJpaEntity;
import com.empresa.hrcore.infrastructure.persistence.jpa.SolicitudVacacionJpaRepository;
import com.empresa.hrcore.presentation.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import org.mockito.ArgumentCaptor;

@ExtendWith(MockitoExtension.class)
public class AbsenceServiceTest {

    @Mock
    private SolicitudVacacionJpaRepository solicitudRepository;

    @Mock
    private SaldoVacacionJpaRepository saldoRepository;

    @Mock
    private IEmployeeRepository employeeRepository;

    @InjectMocks
    private AbsenceService absenceService;

    private UUID empleadoId;
    private Empleado empleado;
    private SaldoVacacionJpaEntity saldoJpa;

    @BeforeEach
    void setUp() {
        empleadoId = UUID.randomUUID();
        
        empleado = new Empleado();
        empleado.setId(empleadoId);
        empleado.setNombre("Juan");
        empleado.setApellido("Perez");
        empleado.setStatus(Empleado.EstadoEmpleado.ACTIVO);
        
        saldoJpa = new SaldoVacacionJpaEntity();
        saldoJpa.setId(UUID.randomUUID());
        saldoJpa.setEmployeeId(empleadoId);
        saldoJpa.setAnio(LocalDate.now().getYear());
        saldoJpa.setDiasTotales(30);
        saldoJpa.setDiasUsados(0);
    }

    @Test
    void testCrearSolicitudExitoso() {
        when(employeeRepository.findById(empleadoId)).thenReturn(Optional.of(empleado));
        when(saldoRepository.findByEmployeeIdAndAnio(empleadoId, LocalDate.now().getYear()))
                .thenReturn(Optional.of(saldoJpa));
        
        when(solicitudRepository.save(any(SolicitudVacacionJpaEntity.class))).thenAnswer(invocation -> {
            SolicitudVacacionJpaEntity entity = invocation.getArgument(0);
            return entity;
        });

        LocalDate inicio = LocalDate.now().plusDays(1);
        LocalDate fin = LocalDate.now().plusDays(5); // 5 dias (inclusive)

        SolicitudVacacion resultado = absenceService.crearSolicitud(empleadoId, inicio, fin);

        assertNotNull(resultado);
        assertEquals(empleadoId, resultado.getEmpleadoId());
        assertEquals(5, resultado.getDiasSolicitados());
        assertEquals("BORRADOR", resultado.getEstadoNombre());
        verify(solicitudRepository, times(1)).save(any(SolicitudVacacionJpaEntity.class));
    }

    @Test
    void testCrearSolicitudSaldoInsuficiente() {
        when(employeeRepository.findById(empleadoId)).thenReturn(Optional.of(empleado));
        
        saldoJpa.setDiasTotales(5);
        saldoJpa.setDiasUsados(3); // 2 dias disponibles
        
        when(saldoRepository.findByEmployeeIdAndAnio(empleadoId, LocalDate.now().getYear()))
                .thenReturn(Optional.of(saldoJpa));

        LocalDate inicio = LocalDate.now().plusDays(1);
        LocalDate fin = LocalDate.now().plusDays(5); // 5 dias (inclusive)

        assertThrows(SaldoInsuficienteException.class, () -> {
            absenceService.crearSolicitud(empleadoId, inicio, fin);
        });
        verify(solicitudRepository, never()).save(any());
    }

    @Test
    void testEnviarSolicitudWorkflowState() {
        UUID solicitudId = UUID.randomUUID();
        SolicitudVacacionJpaEntity solJpa = new SolicitudVacacionJpaEntity();
        solJpa.setId(solicitudId);
        solJpa.setEmployeeId(empleadoId);
        solJpa.setFechaInicio(LocalDate.now().plusDays(2));
        solJpa.setFechaFin(LocalDate.now().plusDays(5));
        solJpa.setDiasSolicitados(4);
        solJpa.setEstado("BORRADOR");

        when(solicitudRepository.findById(solicitudId)).thenReturn(Optional.of(solJpa));
        when(solicitudRepository.save(any(SolicitudVacacionJpaEntity.class))).thenReturn(solJpa);

        SolicitudVacacion resultado = absenceService.enviarSolicitud(solicitudId);

        assertNotNull(resultado);
        assertEquals("PENDIENTE_JEFE", resultado.getEstadoNombre());
        verify(solicitudRepository, times(1)).save(solJpa);
    }

    @Test
    void testAprobarSolicitudWorkflowStateYDescuentoSaldo() {
        UUID solicitudId = UUID.randomUUID();
        SolicitudVacacionJpaEntity solJpa = new SolicitudVacacionJpaEntity();
        solJpa.setId(solicitudId);
        solJpa.setEmployeeId(empleadoId);
        solJpa.setFechaInicio(LocalDate.now().plusDays(2));
        solJpa.setFechaFin(LocalDate.now().plusDays(5));
        solJpa.setDiasSolicitados(4);
        solJpa.setEstado("PENDIENTE_JEFE");

        when(solicitudRepository.findById(solicitudId)).thenReturn(Optional.of(solJpa));
        when(saldoRepository.findByEmployeeIdAndAnio(empleadoId, solJpa.getFechaInicio().getYear()))
                .thenReturn(Optional.of(saldoJpa));
        when(solicitudRepository.save(any(SolicitudVacacionJpaEntity.class))).thenReturn(solJpa);

        SolicitudVacacion resultado = absenceService.aprobarSolicitud(solicitudId);

        assertNotNull(resultado);
        assertEquals("APROBADA", resultado.getEstadoNombre());
        verify(solicitudRepository, times(1)).save(solJpa);

        // guardarSaldo creates a NEW JPA entity, so we capture the argument
        ArgumentCaptor<SaldoVacacionJpaEntity> saldoCaptor = ArgumentCaptor.forClass(SaldoVacacionJpaEntity.class);
        verify(saldoRepository, times(1)).save(saldoCaptor.capture());
        SaldoVacacionJpaEntity savedSaldo = saldoCaptor.getValue();
        assertEquals(4, savedSaldo.getDiasUsados());
    }

    @Test
    void testRechazarSolicitudWorkflowState() {
        UUID solicitudId = UUID.randomUUID();
        SolicitudVacacionJpaEntity solJpa = new SolicitudVacacionJpaEntity();
        solJpa.setId(solicitudId);
        solJpa.setEmployeeId(empleadoId);
        solJpa.setFechaInicio(LocalDate.now().plusDays(2));
        solJpa.setFechaFin(LocalDate.now().plusDays(5));
        solJpa.setDiasSolicitados(4);
        solJpa.setEstado("PENDIENTE_JEFE");

        when(solicitudRepository.findById(solicitudId)).thenReturn(Optional.of(solJpa));
        when(solicitudRepository.save(any(SolicitudVacacionJpaEntity.class))).thenReturn(solJpa);

        SolicitudVacacion resultado = absenceService.rechazarSolicitud(solicitudId, "No es posible en estas fechas");

        assertNotNull(resultado);
        assertEquals("RECHAZADA", resultado.getEstadoNombre());
        assertEquals("No es posible en estas fechas", solJpa.getMotivoRechazo());
        verify(solicitudRepository, times(1)).save(solJpa);
    }

    @Test
    void testTransicionInvalidaDeEstado() {
        UUID solicitudId = UUID.randomUUID();
        SolicitudVacacionJpaEntity solJpa = new SolicitudVacacionJpaEntity();
        solJpa.setId(solicitudId);
        solJpa.setEmployeeId(empleadoId);
        solJpa.setFechaInicio(LocalDate.now().plusDays(2));
        solJpa.setFechaFin(LocalDate.now().plusDays(5));
        solJpa.setDiasSolicitados(4);
        solJpa.setEstado("BORRADOR");

        when(solicitudRepository.findById(solicitudId)).thenReturn(Optional.of(solJpa));

        assertThrows(EstadoTransicionInvalidaException.class, () -> {
            absenceService.aprobarSolicitud(solicitudId);
        });
        verify(solicitudRepository, never()).save(any());
    }
}
