package com.empresa.hrcore.infrastructure.persistence.jpa;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Entity
@Table(name = "time_records")
public class RegistroTiempoJpaEntity {

    @Id
    private UUID id;

    @Column(name = "employee_id", nullable = false)
    private UUID empleadoId;

    @Column(name = "record_date", nullable = false)
    private LocalDate fecha;

    @Column(name = "hora_ingreso_real")
    private LocalTime horaIngresoReal;

    @Column(name = "hora_ingreso_reconocida")
    private LocalTime horaIngresoReconocida;

    @Column(name = "hora_salida_real")
    private LocalTime horaSalidaReal;

    @Column(name = "tardanza_min", nullable = false)
    private int tardanzaMin;

    @Column(name = "salida_anticipada_min", nullable = false)
    private int salidaAnticipadaMin;

    @Column(name = "tiempo_fuera_horario_min", nullable = false)
    private int tiempoFueraDeHorarioMin;

    @Column(name = "horas_efectivas_min", nullable = false)
    private int horasEfectivasMin;

    public RegistroTiempoJpaEntity() {}

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getEmpleadoId() { return empleadoId; }
    public void setEmpleadoId(UUID empleadoId) { this.empleadoId = empleadoId; }

    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }

    public LocalTime getHoraIngresoReal() { return horaIngresoReal; }
    public void setHoraIngresoReal(LocalTime horaIngresoReal) { this.horaIngresoReal = horaIngresoReal; }

    public LocalTime getHoraIngresoReconocida() { return horaIngresoReconocida; }
    public void setHoraIngresoReconocida(LocalTime horaIngresoReconocida) { this.horaIngresoReconocida = horaIngresoReconocida; }

    public LocalTime getHoraSalidaReal() { return horaSalidaReal; }
    public void setHoraSalidaReal(LocalTime horaSalidaReal) { this.horaSalidaReal = horaSalidaReal; }

    public int getTardanzaMin() { return tardanzaMin; }
    public void setTardanzaMin(int tardanzaMin) { this.tardanzaMin = tardanzaMin; }

    public int getSalidaAnticipadaMin() { return salidaAnticipadaMin; }
    public void setSalidaAnticipadaMin(int salidaAnticipadaMin) { this.salidaAnticipadaMin = salidaAnticipadaMin; }

    public int getTiempoFueraDeHorarioMin() { return tiempoFueraDeHorarioMin; }
    public void setTiempoFueraDeHorarioMin(int tiempoFueraDeHorarioMin) { this.tiempoFueraDeHorarioMin = tiempoFueraDeHorarioMin; }

    public int getHorasEfectivasMin() { return horasEfectivasMin; }
    public void setHorasEfectivasMin(int horasEfectivasMin) { this.horasEfectivasMin = horasEfectivasMin; }
}
