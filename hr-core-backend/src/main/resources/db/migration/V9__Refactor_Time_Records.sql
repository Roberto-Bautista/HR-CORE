-- V9__Refactor_Time_Records.sql
-- Refactor completo de time_records para las nuevas reglas de negocio de asistencia.
-- Se eliminan las columnas antiguas y se agregan los campos auditables y calculados.

ALTER TABLE time_records
    DROP COLUMN IF EXISTS normal_hours,
    DROP COLUMN IF EXISTS extra_hours,
    DROP COLUMN IF EXISTS penalty_hours,
    ADD COLUMN hora_ingreso_real       TIME,
    ADD COLUMN hora_ingreso_reconocida TIME,
    ADD COLUMN hora_salida_real        TIME,
    ADD COLUMN tardanza_min            INTEGER NOT NULL DEFAULT 0,
    ADD COLUMN salida_anticipada_min   INTEGER NOT NULL DEFAULT 0,
    ADD COLUMN tiempo_fuera_horario_min INTEGER NOT NULL DEFAULT 0,
    ADD COLUMN horas_efectivas_min     INTEGER NOT NULL DEFAULT 0;
