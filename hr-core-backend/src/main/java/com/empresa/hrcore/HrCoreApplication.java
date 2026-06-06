package com.empresa.hrcore;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Clase principal de HR-Core.
 * Punto de entrada de la aplicación Spring Boot.
 *
 * @SpringBootApplication activa:
 *   - @ComponentScan: escanea todos los beans del paquete
 *   - @EnableAutoConfiguration: configura automáticamente Spring
 *   - @Configuration: permite definir beans adicionales
 *
 * @EnableScheduling: habilita los @Scheduled jobs (Fase 4+)
 *   - Consolidado nómina mensual
 *   - Limpieza DLQ
 *   - Expiración de vacaciones
 */
@SpringBootApplication
@EnableScheduling
public class HrCoreApplication {

    public static void main(String[] args) {
        SpringApplication.run(HrCoreApplication.class, args);
    }
}
