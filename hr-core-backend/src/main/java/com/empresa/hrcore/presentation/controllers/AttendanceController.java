package com.empresa.hrcore.presentation.controllers;

import com.empresa.hrcore.application.usecases.AttendanceUseCase;
import com.empresa.hrcore.domain.entities.Marcacion;
import com.empresa.hrcore.domain.entities.RegistroTiempo;
import com.empresa.hrcore.domain.patterns.strategy.attendance.HorasNormalesStrategy;
import com.empresa.hrcore.presentation.dtos.MarcacionRequestDTO;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

@RestController
@RequestMapping("/v1/attendance")
@CrossOrigin(origins = "*")
public class AttendanceController {

    // Ventanas de marcación (espejo de lo que el frontend muestra)
    private static final LocalTime VENTANA_ENTRADA_INICIO = HorasNormalesStrategy.VENTANA_ENTRADA_INICIO; // 07:55
    private static final LocalTime VENTANA_SALIDA_INICIO  = HorasNormalesStrategy.VENTANA_SALIDA_INICIO;  // 12:55

    private final AttendanceUseCase attendanceUseCase;

    public AttendanceController(AttendanceUseCase attendanceUseCase) {
        this.attendanceUseCase = attendanceUseCase;
    }

    @PostMapping("/clock")
    public ResponseEntity<?> clock(@Valid @RequestBody MarcacionRequestDTO request) {
        try {
            Marcacion.TipoMarcacion tipo = Marcacion.TipoMarcacion.valueOf(request.getTipo().toUpperCase());
            attendanceUseCase.registrarMarcacion(request.getEmpleadoId(), tipo);
            return ResponseEntity.ok().body(Map.of("message", "Marcación registrada correctamente"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Tipo de marcación inválido. Use ENTRADA o SALIDA."));
        }
    }

    @GetMapping("/employee/{id}/today")
    public ResponseEntity<?> getTodayRecords(@PathVariable UUID id) {
        LocalDate hoy = LocalDate.now();
        LocalTime ahora = LocalTime.now();

        List<Marcacion> marcaciones = attendanceUseCase.obtenerMarcaciones(id, hoy);
        RegistroTiempo registro = attendanceUseCase.obtenerRegistroDiario(id, hoy);
        String estadoJornada = determinarEstadoJornada(marcaciones);

        // Resumen con nueva nomenclatura
        Map<String, Object> resumen = new LinkedHashMap<>();
        resumen.put("horasEfectivasMin",      registro.getHorasEfectivasMin());
        resumen.put("tardanzaMin",             registro.getTardanzaMin());
        resumen.put("salidaAnticipadaMin",     registro.getSalidaAnticipadaMin());
        resumen.put("tiempoFueraDeHorarioMin", registro.getTiempoFueraDeHorarioMin());
        resumen.put("horaIngresoReal",         registro.getHoraIngresoReal() != null ? registro.getHoraIngresoReal().toString() : null);
        resumen.put("horaIngresoReconocida",   registro.getHoraIngresoReconocida() != null ? registro.getHoraIngresoReconocida().toString() : null);
        resumen.put("horaSalidaReal",          registro.getHoraSalidaReal() != null ? registro.getHoraSalidaReal().toString() : null);

        // Información de horario para el frontend
        Map<String, Object> horario = new LinkedHashMap<>();
        horario.put("entradaOficial",       HorasNormalesStrategy.HORA_ENTRADA_OFICIAL.toString());
        horario.put("salidaOficial",        HorasNormalesStrategy.HORA_SALIDA_OFICIAL.toString());
        horario.put("ventanaEntradaInicio", VENTANA_ENTRADA_INICIO.toString());
        horario.put("ventanaSalidaInicio",  VENTANA_SALIDA_INICIO.toString());
        horario.put("jornadaMinutos",       HorasNormalesStrategy.JORNADA_MINUTOS);
        // TODO [SPRINT FUTURO] Revertir a solo L-V: esDiaLaborable(hoy)
        horario.put("esDiaLaborable",       true); // TEMPORAL: todos los días para pruebas
        horario.put("horaActual",           ahora.toString());

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("fecha",              hoy);
        response.put("marcaciones",        marcaciones);
        response.put("resumenHoras",       resumen);
        response.put("estadoJornada",      estadoJornada);
        response.put("puedeMarcarEntrada", "SIN_INICIAR".equals(estadoJornada));
        response.put("puedeMarcarSalida",  "EN_CURSO".equals(estadoJornada));
        response.put("horario",            horario);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/employee/{id}/history")
    public ResponseEntity<?> getHistory(
            @PathVariable UUID id,
            @RequestParam(defaultValue = "30") int dias) {

        LocalDate hasta = LocalDate.now();
        LocalDate desde = hasta.minusDays(dias);

        List<RegistroTiempo> registros = attendanceUseCase.obtenerHistorial(id, desde, hasta);

        List<Map<String, Object>> historial = new ArrayList<>();
        for (RegistroTiempo reg : registros) {
            List<Marcacion> marcacionesDia = attendanceUseCase.obtenerMarcaciones(id, reg.getFecha());
            Map<String, Object> entrada = new LinkedHashMap<>();
            entrada.put("fecha",                   reg.getFecha());
            entrada.put("diaSemana",               reg.getFecha().getDayOfWeek().toString());
            entrada.put("horasEfectivasMin",        reg.getHorasEfectivasMin());
            entrada.put("tardanzaMin",              reg.getTardanzaMin());
            entrada.put("salidaAnticipadaMin",      reg.getSalidaAnticipadaMin());
            entrada.put("tiempoFueraDeHorarioMin",  reg.getTiempoFueraDeHorarioMin());
            entrada.put("horaIngresoReal",          reg.getHoraIngresoReal() != null ? reg.getHoraIngresoReal().toString() : null);
            entrada.put("horaIngresoReconocida",    reg.getHoraIngresoReconocida() != null ? reg.getHoraIngresoReconocida().toString() : null);
            entrada.put("horaSalidaReal",           reg.getHoraSalidaReal() != null ? reg.getHoraSalidaReal().toString() : null);
            entrada.put("marcaciones",              marcacionesDia);
            historial.add(entrada);
        }

        return ResponseEntity.ok(historial);
    }

    private String determinarEstadoJornada(List<Marcacion> marcaciones) {
        if (marcaciones.isEmpty()) return "SIN_INICIAR";
        Marcacion ultima = marcaciones.get(marcaciones.size() - 1);
        return ultima.getTipo() == Marcacion.TipoMarcacion.ENTRADA ? "EN_CURSO" : "COMPLETADA";
    }
}
