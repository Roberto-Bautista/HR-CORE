-- ============================================================
-- V1__Create_Employees_Table.sql
-- Migración: Tablas del módulo "Ciclo de Vida del Empleado"
-- ============================================================

-- ============================================================
-- Tabla principal de empleados
-- ============================================================
CREATE TABLE IF NOT EXISTS employees (
    id              UUID            PRIMARY KEY DEFAULT gen_random_uuid(),
    codigo          VARCHAR(20)     NOT NULL UNIQUE,
    nombre          VARCHAR(100)    NOT NULL,
    apellido        VARCHAR(100)    NOT NULL,
    email           VARCHAR(150)    NOT NULL UNIQUE,
    telefono        VARCHAR(20),
    genero          VARCHAR(20),

    -- Datos laborales
    cargo           VARCHAR(100)    NOT NULL,
    departamento    VARCHAR(100)    NOT NULL,
    salario         DECIMAL(12, 2)  NOT NULL CHECK (salario > 0),
    fecha_alta      DATE            NOT NULL,
    fecha_cese      DATE,
    status          VARCHAR(20)     NOT NULL DEFAULT 'ACTIVO',

    -- Metadatos de auditoría
    created_at      TIMESTAMP       NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP       NOT NULL DEFAULT NOW(),
    created_by      VARCHAR(150),
    updated_by      VARCHAR(150)
);

-- ============================================================
-- Tabla de perfil profesional del empleado (1 a 1)
-- ============================================================
CREATE TABLE IF NOT EXISTS professional_profiles (
    id              UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    employee_id     UUID        NOT NULL UNIQUE,
    nivel_educativo VARCHAR(50),
    especialidad    VARCHAR(200),
    habilidades     TEXT[],
    certificaciones TEXT[],
    linkedin_url    VARCHAR(300),
    updated_at      TIMESTAMP   NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_profile_employee
        FOREIGN KEY (employee_id) REFERENCES employees(id)
        ON DELETE CASCADE
);

-- ============================================================
-- Índices para optimizar búsquedas frecuentes
-- ============================================================
CREATE INDEX IF NOT EXISTS idx_employees_email        ON employees(email);
CREATE INDEX IF NOT EXISTS idx_employees_status       ON employees(status);
CREATE INDEX IF NOT EXISTS idx_employees_departamento ON employees(departamento);
CREATE INDEX IF NOT EXISTS idx_employees_fecha_alta   ON employees(fecha_alta);
