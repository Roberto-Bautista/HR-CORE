-- ============================================================
-- V1__Create_Employees_Table.sql
-- Migración: Tablas del módulo "Ciclo de Vida del Empleado"
-- ============================================================

-- Tipo ENUM para el estado activo del empleado
CREATE TYPE employee_status AS ENUM ('ACTIVO', 'INACTIVO', 'CESADO');

-- Tipo ENUM para el género
CREATE TYPE employee_gender AS ENUM ('MASCULINO', 'FEMENINO', 'OTRO', 'PREFIERO_NO_DECIR');

-- ============================================================
-- Tabla principal de empleados
-- ============================================================
CREATE TABLE employees (
    id              UUID            PRIMARY KEY DEFAULT gen_random_uuid(),
    codigo          VARCHAR(20)     NOT NULL UNIQUE,            -- Ej: EMP-0001
    nombre          VARCHAR(100)    NOT NULL,
    apellido        VARCHAR(100)    NOT NULL,
    email           VARCHAR(150)    NOT NULL UNIQUE,
    telefono        VARCHAR(20),
    genero          employee_gender,
    fecha_nacimiento DATE,

    -- Datos laborales
    cargo           VARCHAR(100)    NOT NULL,
    departamento    VARCHAR(100)    NOT NULL,
    salario         DECIMAL(12, 2)  NOT NULL CHECK (salario > 0),
    fecha_alta      DATE            NOT NULL,                   -- Fecha de ingreso
    fecha_cese      DATE,                                       -- NULL si sigue activo
    status          employee_status NOT NULL DEFAULT 'ACTIVO',

    -- Metadatos de auditoría
    created_at      TIMESTAMP       NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP       NOT NULL DEFAULT NOW(),
    created_by      VARCHAR(150),                               -- Email del usuario que creó
    updated_by      VARCHAR(150)
);

-- ============================================================
-- Tabla de perfil profesional del empleado
-- (habilidades, certificaciones, nivel educativo)
-- ============================================================
CREATE TABLE professional_profiles (
    id              UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    employee_id     UUID        NOT NULL UNIQUE,               -- 1-a-1 con employees
    nivel_educativo VARCHAR(50),                               -- Ej: LICENCIATURA, MAESTRIA
    especialidad    VARCHAR(200),
    habilidades     TEXT[],                                    -- Array de habilidades (Java, SQL...)
    certificaciones TEXT[],                                    -- Array de certificaciones
    linkedin_url    VARCHAR(300),
    updated_at      TIMESTAMP   NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_profile_employee
        FOREIGN KEY (employee_id) REFERENCES employees(id)
        ON DELETE CASCADE
);

-- ============================================================
-- Índices para optimizar búsquedas frecuentes
-- ============================================================
CREATE INDEX idx_employees_email        ON employees(email);
CREATE INDEX idx_employees_status       ON employees(status);
CREATE INDEX idx_employees_departamento ON employees(departamento);
CREATE INDEX idx_employees_fecha_alta   ON employees(fecha_alta);
