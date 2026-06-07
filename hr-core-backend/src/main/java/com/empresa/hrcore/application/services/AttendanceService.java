package com.empresa.hrcore.application.services;

import com.empresa.hrcore.application.usecases.AttendanceUseCase;
import com.empresa.hrcore.domain.entities.Empleado;
import com.empresa.hrcore.domain.entities.Marcacion;
import com.empresa.hrcore.domain.entities.RegistroTiempo;
import com.empresa.hrcore.domain.exceptions.AttendanceValidationException;
import com.empresa.hrcore.domain.patterns.strategy.attendance.CalculadoraHoras;
import com.empresa.hrcore.domain.patterns.strategy.attendance.HorasExtraStrategy;
import com.empresa.hrcore.domain.patterns.strategy.attendance.HorasNormalesStrategy;
import com.empresa.hrcore.domain.patterns.strategy.attendance.PenalidadesStrategy;
import com.empresa.hrcore.domain.ports.AttendanceEventPublisher;
import com.empresa.hrcore.domain.ports.EmployeeCachePort;
import com.empresa.hrcore.domain.ports.IEmployeeRepository;
import com.empresa.hrcore.domain.ports.MarcacionRepository;
import com.empresa.hrcore.domain.ports.RegistroTiempoRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
public class AttendanceService implements AttendanceUseCase {

    private final IEmployeeRepository employeeRepository;
    private final EmployeeCachePort employeeCachePort;
    private final AttendanceEventPublisher eventPublisher;
    private final MarcacionRepository marcacionRepository;
    private final RegistroTiempoRepository registroTiempoRepository;
    
    private final List<CalculadoraHoras> estrategias;

    public AttendanceService(IEmployeeRepository employeeRepository,
                             EmployeeCachePort employeeCachePort,
                             AttendanceEventPublisher eventPublisher,
                             MarcacionRepository marcacionRepository,
                             RegistroTiempoRepository registroTiempoRepository) {
        this.employeeRepository = employeeRepository;
        this.employeeCachePort = employeeCachePort;
        this.eventPublisher = eventPublisher;
        this.marcacionRepository = marcacionRepository;
        this.registroTiempoRepository = registroTiempoRepository;
        
        // Orden crítico:
        // 1. HorasExtraStrategy  → calcula tardanzaMin
        // 2. PenalidadesStrategy → calcula salidaAnticipadaMin y tiempoFueraDeHorarioMin
        // 3. HorasNormalesStrategy → usa tardanza + salidaAnticipada para calcular horasEfectivasMin
        this.estrategias = Arrays.asList(
            new HorasExtraStrategy(),
            new PenalidadesStrategy(),
            new HorasNormalesStrategy()
        );
    }

    @Override
    public void registrarMarcacion(UUID empleadoId, Marcacion.TipoMarcacion tipo) {
        // Validar que el empleado exista (Rápido, desde caché Redis)
        Empleado empleado = employeeCachePort.getEmployeeFromCache(empleadoId)
                .orElseGet(() -> {
                    // Si no está en caché, lo busco en BD y lo guardo en caché
                    Empleado emp = employeeRepository.findById(empleadoId)
                            .orElseThrow(() -> new AttendanceValidationException("Empleado no encontrado con ID: " + empleadoId));
                    employeeCachePort.saveEmployeeToCache(emp);
                    return emp;
                });

        if (!empleado.estaActivo()) {
            throw new AttendanceValidationException("No se puede registrar marcación de un empleado inactivo.");
        }

        LocalDate hoy = LocalDate.now();
        List<Marcacion> marcacionesHoy = marcacionRepository.findByEmpleadoIdAndFecha(empleadoId, hoy);
        validarSecuenciaMarcacion(tipo, marcacionesHoy);

        // Crear la marcación
        Marcacion marcacion = new Marcacion(empleadoId, tipo, LocalDateTime.now());

        // Guardar sincrónicamente para evitar pérdida de datos si Kafka falla (Opcional, en sistemas muy concurrentes
        // podríamos solo mandar a Kafka y no guardar aquí. Para este caso, guardaremos y publicaremos).
        marcacionRepository.save(marcacion);

        // Mantener el resumen consistente aunque Kafka tarde o no este disponible.
        obtenerRegistroDiario(empleadoId, hoy);
        
        // Publicar el evento en Kafka para procesamiento asíncrono
        eventPublisher.publishMarcacionEvent(marcacion);
    }

    @Override
    public List<Marcacion> obtenerMarcaciones(UUID empleadoId, LocalDate fecha) {
        return marcacionRepository.findByEmpleadoIdAndFecha(empleadoId, fecha);
    }

    @Override
    public RegistroTiempo obtenerRegistroDiario(UUID empleadoId, LocalDate fecha) {
        // Calcular siempre desde las marcaciones del dia para evitar resumenes obsoletos.
        List<Marcacion> marcacionesDelDia = marcacionRepository.findByEmpleadoIdAndFecha(empleadoId, fecha);
        
        RegistroTiempo nuevoRegistro = new RegistroTiempo(empleadoId, fecha);
        
        for (CalculadoraHoras estrategia : estrategias) {
            estrategia.calcular(marcacionesDelDia, nuevoRegistro);
        }

        return registroTiempoRepository.save(nuevoRegistro);
    }

    private void validarSecuenciaMarcacion(Marcacion.TipoMarcacion tipo, List<Marcacion> marcacionesHoy) {
        if (marcacionesHoy.isEmpty()) {
            if (tipo != Marcacion.TipoMarcacion.ENTRADA) {
                throw new AttendanceValidationException("Primero debes marcar entrada.");
            }
            return;
        }

        Marcacion ultimaMarcacion = marcacionesHoy.get(marcacionesHoy.size() - 1);

        if (ultimaMarcacion.getTipo() == Marcacion.TipoMarcacion.ENTRADA) {
            if (tipo == Marcacion.TipoMarcacion.ENTRADA) {
                throw new AttendanceValidationException("Ya tienes una entrada registrada. Marca salida para cerrar la jornada.");
            }
            return;
        }

        throw new AttendanceValidationException("La jornada de hoy ya fue completada.");
    }

    @Override
    public List<RegistroTiempo> obtenerHistorial(UUID empleadoId, LocalDate desde, LocalDate hasta) {
        return registroTiempoRepository.findByEmpleadoIdBetweenDates(empleadoId, desde, hasta);
    }
}
