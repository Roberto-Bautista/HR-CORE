-- ============================================================
-- V4__Create_Benefits_Tables.sql
-- Migración: Tablas del módulo de "Gestión de Beneficios"
-- ============================================================

-- ============================================================
-- Tabla de beneficios (Catálogo)
-- ============================================================
CREATE TABLE IF NOT EXISTS benefits (
    id                          UUID            PRIMARY KEY DEFAULT gen_random_uuid(),
    nombre                      VARCHAR(100)    NOT NULL UNIQUE,
    descripcion                 TEXT,
    costo                       DECIMAL(12, 2)  NOT NULL DEFAULT 0.00 CHECK (costo >= 0),
    activo                      BOOLEAN         NOT NULL DEFAULT TRUE,
    
    -- Reglas de elegibilidad (Strategy)
    requiere_salario_min        DECIMAL(12, 2)  DEFAULT NULL,
    requiere_salario_max        DECIMAL(12, 2)  DEFAULT NULL,
    requiere_antiguedad_meses   INTEGER         DEFAULT NULL,
    requiere_cargo              VARCHAR(100)    DEFAULT NULL,

    -- Metadatos de auditoría
    created_at                  TIMESTAMP       NOT NULL DEFAULT NOW(),
    updated_at                  TIMESTAMP       NOT NULL DEFAULT NOW()
);

-- ============================================================
-- Tabla de enrolamiento a beneficios (Mapeo N a N)
-- ============================================================
CREATE TABLE IF NOT EXISTS benefit_enrollments (
    id                  UUID            PRIMARY KEY DEFAULT gen_random_uuid(),
    employee_id         UUID            NOT NULL,
    benefit_id          UUID            NOT NULL,
    fecha_enrolamiento  DATE            NOT NULL,
    estado              VARCHAR(20)     NOT NULL DEFAULT 'ACTIVO', -- ACTIVO, INACTIVO
    created_at          TIMESTAMP       NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_enrollment_employee
        FOREIGN KEY (employee_id) REFERENCES employees(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_enrollment_benefit
        FOREIGN KEY (benefit_id) REFERENCES benefits(id)
        ON DELETE CASCADE,

    CONSTRAINT uq_employee_benefit
        UNIQUE (employee_id, benefit_id)
);

-- ============================================================
-- Índices para optimizar búsquedas
-- ============================================================
CREATE INDEX IF NOT EXISTS idx_enrollments_employee ON benefit_enrollments(employee_id);
CREATE INDEX IF NOT EXISTS idx_enrollments_benefit  ON benefit_enrollments(benefit_id);

-- ============================================================
-- Inserción del catálogo base de beneficios
-- ============================================================
INSERT INTO benefits (nombre, descripcion, costo, requiere_cargo)
VALUES ('Seguro de Salud EPS Premium', 'Seguro de salud EPS de categoría Premium con cobertura familiar al 100%. Exclusivo para Gerentes.', 150.00, 'Gerente')
ON CONFLICT (nombre) DO NOTHING;

INSERT INTO benefits (nombre, descripcion, costo, requiere_antiguedad_meses)
VALUES ('Bono de Capacitación TI', 'Subvención anual del 80% en certificaciones y cursos de tecnología. Requiere un mínimo de 6 meses en la empresa.', 500.00, 6)
ON CONFLICT (nombre) DO NOTHING;

INSERT INTO benefits (nombre, descripcion, costo, requiere_salario_max)
VALUES ('Vales de Alimentación Sodexo', 'Tarjeta de alimentación con S/.400 mensuales. Exclusivo para colaboradores con sueldo menor o igual a S/.4,000.', 0.00, 4000.00)
ON CONFLICT (nombre) DO NOTHING;

INSERT INTO benefits (nombre, descripcion, costo)
VALUES ('Programa de Mentoría Colectiva', 'Acceso a talleres semanales de desarrollo profesional y mentorías uno a uno. Libre para todos.', 0.00)
ON CONFLICT (nombre) DO NOTHING;
