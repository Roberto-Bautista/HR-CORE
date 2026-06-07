-- ============================================================
-- V8__Create_Employee_History_Table.sql
-- Migración: Tabla de historial de cambios del empleado
-- Registra cada evento significativo del ciclo de vida
-- ============================================================

CREATE TABLE IF NOT EXISTS employee_history (
    id              UUID            PRIMARY KEY DEFAULT gen_random_uuid(),
    employee_id     UUID            NOT NULL,
    tipo_cambio     VARCHAR(50)     NOT NULL,
    campo           VARCHAR(100),
    valor_anterior  TEXT,
    valor_nuevo     TEXT,
    descripcion     TEXT,
    fecha           TIMESTAMP       NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_history_employee
        FOREIGN KEY (employee_id) REFERENCES employees(id)
        ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_history_employee_id ON employee_history(employee_id);
CREATE INDEX IF NOT EXISTS idx_history_fecha       ON employee_history(fecha);
