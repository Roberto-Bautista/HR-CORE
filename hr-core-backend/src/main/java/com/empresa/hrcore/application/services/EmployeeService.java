package com.empresa.hrcore.application.services;

import com.empresa.hrcore.domain.entities.Empleado;
import com.empresa.hrcore.domain.entities.PerfilProfesional;
import com.empresa.hrcore.domain.ports.IEmployeeRepository;
import com.empresa.hrcore.presentation.exceptions.ConflictException;
import com.empresa.hrcore.presentation.exceptions.ResourceNotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * SERVICIO DE APLICACIÓN — Casos de Uso del Empleado
 *
 * En Arquitectura Hexagonal, esta clase orquesta la lógica:
 *   1. Recibe datos del Controller (capa de presentación)
 *   2. Valida reglas de negocio usando las entidades de dominio
 *   3. Llama al repositorio (puerto de salida) para persistir
 *
 * Esta clase NO contiene lógica de negocio compleja
 * (eso va en las entidades de dominio).
 * Solo coordina el flujo.
 */
@Service
@Transactional
public class EmployeeService {

    private static final Logger log = LoggerFactory.getLogger(EmployeeService.class);

    private final IEmployeeRepository employeeRepository;

    /**
     * Inyección de dependencias por constructor.
     * Spring inyecta aquí la implementación de IEmployeeRepository
     * (que es EmployeeRepositoryImpl de la capa de infraestructura).
     */
    public EmployeeService(IEmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    // ==========================================================
    // CASO DE USO 1: Crear Empleado
    // ==========================================================

    public Empleado crearEmpleado(String codigo, String nombre, String apellido, String email,
                                   String telefono, String genero, String cargo,
                                   String departamento, BigDecimal salario, LocalDate fechaAlta) {

        log.info("Creando empleado: {} {} ({})", nombre, apellido, codigo);

        // Validar que no exista un empleado con el mismo email
        if (employeeRepository.existsByEmail(email)) {
            throw new ConflictException("Empleado", "email", email);
        }

        // Validar que no exista un empleado con el mismo código
        if (employeeRepository.existsByCodigo(codigo)) {
            throw new ConflictException("Empleado", "codigo", codigo);
        }

        // Crear la entidad de dominio
        Empleado empleado = new Empleado(codigo, nombre, apellido, email,
                                          cargo, departamento, salario, fechaAlta);
        empleado.setTelefono(telefono);
        empleado.setGenero(genero);

        // Crear perfil profesional vacío (se llenará después)
        PerfilProfesional perfil = new PerfilProfesional(empleado.getId());
        empleado.setPerfilProfesional(perfil);

        // Persistir
        Empleado saved = employeeRepository.save(empleado);
        log.info("Empleado creado exitosamente: {} ({})", saved.getNombre(), saved.getCodigo());

        return saved;
    }

    // ==========================================================
    // CASO DE USO 2: Obtener Empleado por ID
    // ==========================================================

    @Transactional(readOnly = true)
    public Empleado obtenerPorId(UUID id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Empleado", id));
    }

    // ==========================================================
    // CASO DE USO 3: Listar Empleados
    // ==========================================================

    @Transactional(readOnly = true)
    public List<Empleado> listarTodos() {
        return employeeRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Empleado> listarPorEstado(Empleado.EstadoEmpleado status) {
        return employeeRepository.findByStatus(status);
    }

    @Transactional(readOnly = true)
    public List<Empleado> listarPorDepartamento(String departamento) {
        return employeeRepository.findByDepartamento(departamento);
    }

    // ==========================================================
    // CASO DE USO 4: Actualizar Datos del Empleado
    // ==========================================================

    public Empleado actualizarEmpleado(UUID id, String nombre, String apellido, String email,
                                        String telefono, String cargo, String departamento,
                                        BigDecimal salario) {

        Empleado empleado = obtenerPorId(id);

        // Validar email si cambió
        if (!empleado.getEmail().equals(email) && employeeRepository.existsByEmail(email)) {
            throw new ConflictException("Empleado", "email", email);
        }

        empleado.setNombre(nombre);
        empleado.setApellido(apellido);
        empleado.setEmail(email);
        empleado.setTelefono(telefono);
        empleado.actualizarDatosLaborales(cargo, departamento, salario);

        log.info("Empleado actualizado: {} {}", nombre, apellido);
        return employeeRepository.save(empleado);
    }

    // ==========================================================
    // CASO DE USO 5: Actualizar Perfil Profesional
    // ==========================================================

    public Empleado actualizarPerfil(UUID empleadoId, String nivelEducativo,
                                      String especialidad, String linkedinUrl,
                                      List<String> habilidades, List<String> certificaciones) {

        Empleado empleado = obtenerPorId(empleadoId);
        PerfilProfesional perfil = empleado.getPerfilProfesional();

        if (perfil == null) {
            perfil = new PerfilProfesional(empleadoId);
            empleado.setPerfilProfesional(perfil);
        }

        perfil.actualizarPerfil(nivelEducativo, especialidad, linkedinUrl);

        if (habilidades != null)      perfil.setHabilidades(habilidades);
        if (certificaciones != null)  perfil.setCertificaciones(certificaciones);

        log.info("Perfil profesional actualizado para: {} {}", empleado.getNombre(), empleado.getApellido());
        return employeeRepository.save(empleado);
    }

    // ==========================================================
    // CASO DE USO 6: Cesar Empleado
    // ==========================================================

    public Empleado cesarEmpleado(UUID id, LocalDate fechaCese) {
        Empleado empleado = obtenerPorId(id);
        empleado.cesar(fechaCese);  // Lógica de negocio dentro de la entidad de dominio

        log.info("Empleado cesado: {} {} (fecha: {})", empleado.getNombre(), empleado.getApellido(), fechaCese);
        return employeeRepository.save(empleado);
    }

    // ==========================================================
    // CASO DE USO 7: Estadísticas para el Dashboard
    // ==========================================================

    @Transactional(readOnly = true)
    public long contarEmpleados() {
        return employeeRepository.count();
    }

    @Transactional(readOnly = true)
    public long contarEmpleadosActivos() {
        return employeeRepository.findByStatus(Empleado.EstadoEmpleado.ACTIVO).size();
    }
}
