package com.empresa.hrcore.infrastructure.adapters;

import com.empresa.hrcore.domain.entities.Marcacion;
import com.empresa.hrcore.domain.ports.AttendanceEventPublisher;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class AttendanceKafkaProducer implements AttendanceEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private static final String TOPIC = "attendance.marks";

    public AttendanceKafkaProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void publishMarcacionEvent(Marcacion marcacion) {
        // Enviar la marcación al topic de Kafka. 
        // La key puede ser el empleadoId para asegurar orden.
        kafkaTemplate.send(TOPIC, marcacion.getEmpleadoId().toString(), marcacion);
    }
}
