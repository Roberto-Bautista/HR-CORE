-- ============================================================
-- V10__Seed_Employees.sql
-- Migración: Semilla de datos para pruebas locales
-- ============================================================

-- Insertar Gerente (Elegible para EPS Premium)
INSERT INTO employees (id, codigo, nombre, apellido, email, telefono, genero, cargo, departamento, salario, fecha_alta, status)
VALUES (
    '3ba37893-a42d-4d4a-8897-043a1dd2d98f',
    'EMP-001',
    'Roberto',
    'Bautista',
    'roberto.bautista@empresa.com',
    '999888777',
    'MASCULINO',
    'Gerente',
    'Tecnología',
    9500.00,
    '2024-01-15',
    'ACTIVO'
) ON CONFLICT (codigo) DO NOTHING;

-- Insertar Analista Junior (Elegible para Vales Sodexo, No elegible para Bono TI por antigüedad)
INSERT INTO employees (id, codigo, nombre, apellido, email, telefono, genero, cargo, departamento, salario, fecha_alta, status)
VALUES (
    '6ffc2f98-a513-469a-9984-fcc043fdf8fd',
    'EMP-002',
    'Diana',
    'Prince',
    'diana.prince@empresa.com',
    '999777666',
    'FEMENINO',
    'Analista',
    'Recursos Humanos',
    3800.00,
    -- Antigüedad corta (ej: hace 2 meses respecto a junio 2026)
    '2026-04-10',
    'ACTIVO'
) ON CONFLICT (codigo) DO NOTHING;

-- Insertar Desarrollador Senior (Elegible para Bono TI por antigüedad)
INSERT INTO employees (id, codigo, nombre, apellido, email, telefono, genero, cargo, departamento, salario, fecha_alta, status)
VALUES (
    'f3dfc85f-53ea-4ae1-90b4-fee87428237b',
    'EMP-003',
    'Clark',
    'Kent',
    'clark.kent@empresa.com',
    '999555444',
    'MASCULINO',
    'Desarrollador',
    'Tecnología',
    6500.00,
    -- Antigüedad de 1 año (Ingresó en junio 2025)
    '2025-06-01',
    'ACTIVO'
) ON CONFLICT (codigo) DO NOTHING;
