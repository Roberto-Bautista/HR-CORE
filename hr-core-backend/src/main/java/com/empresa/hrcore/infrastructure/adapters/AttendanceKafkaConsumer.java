package com.empresa.hrcore.infrastructure.adapters;

import com.empresa.hrcore.application.usecases.AttendanceUseCase;
import com.empresa.hrcore.domain.entities.Marcacion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class AttendanceKafkaConsumer {

    private static final Logger logger = LoggerFactory.getLogger(AttendanceKafkaConsumer.class);
    private final AttendanceUseCase attendanceUseCase;

    public AttendanceKafkaConsumer(AttendanceUseCase attendanceUseCase) {
        this.attendanceUseCase = attendanceUseCase;
    }

    @KafkaListener(topics = "attendance.marks", groupId = "hr-core-group")
    public void consumeMarcacion(Marcacion marcacion) {
        logger.info("Kafka Event Received: Marcación procesada asíncronamente para el empleado {}", marcacion.getEmpleadoId());
        
        try {
            // Calcular/Actualizar el registro de tiempo del empleado en base a las marcaciones de hoy
            attendanceUseCase.obtenerRegistroDiario(marcacion.getEmpleadoId(), marcacion.getTimestamp().toLocalDate());
            logger.info("Registro de tiempo actualizado exitosamente para empleado {}", marcacion.getEmpleadoId());
        } catch (Exception e) {
            logger.error("Error al procesar la marcación desde Kafka: ", e);
        }
    }
}
