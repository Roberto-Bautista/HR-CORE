-- ============================================================
-- V2__Create_Absences_Tables.sql
-- Migración: Tablas del módulo "Gestión de Vacaciones"
-- Patrón State: el campo 'estado' almacena el nombre del estado actual
-- ============================================================

-- ============================================================
-- Tabla de solicitudes de vacaciones
-- ============================================================
CREATE TABLE IF NOT EXISTS vacation_requests (
    id                UUID            PRIMARY KEY DEFAULT gen_random_uuid(),
    employee_id       UUID            NOT NULL,
    fecha_inicio      DATE            NOT NULL,
    fecha_fin         DATE            NOT NULL,
    dias_solicitados  INT             NOT NULL CHECK (dias_solicitados > 0),
    estado            VARCHAR(30)     NOT NULL DEFAULT 'BORRADOR',
    motivo_rechazo    TEXT,
    created_at        TIMESTAMP       NOT NULL DEFAULT NOW(),
    updated_at        TIMESTAMP       NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_vacation_employee
        FOREIGN KEY (employee_id) REFERENCES employees(id)
        ON DELETE CASCADE
);

-- ============================================================
-- Tabla de saldos de vacaciones por empleado y año
-- ============================================================
CREATE TABLE IF NOT EXISTS vacation_balances (
    id              UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    employee_id     UUID        NOT NULL,
    anio            INT         NOT NULL,
    dias_totales    INT         NOT NULL DEFAULT 30,
    dias_usados     INT         NOT NULL DEFAULT 0,

    CONSTRAINT fk_balance_employee
        FOREIGN KEY (employee_id) REFERENCES employees(id)
        ON DELETE CASCADE,

    CONSTRAINT uq_balance_employee_anio
        UNIQUE (employee_id, anio)
);

-- ============================================================
-- Índices para optimizar consultas frecuentes
-- ============================================================
CREATE INDEX IF NOT EXISTS idx_vacation_requests_employee   ON vacation_requests(employee_id);
CREATE INDEX IF NOT EXISTS idx_vacation_requests_estado     ON vacation_requests(estado);
CREATE INDEX IF NOT EXISTS idx_vacation_requests_fecha      ON vacation_requests(fecha_inicio);
CREATE INDEX IF NOT EXISTS idx_vacation_balances_employee   ON vacation_balances(employee_id);
