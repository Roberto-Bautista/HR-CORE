-- V3__Create_Attendance_Tables.sql

CREATE TABLE attendance_marks (
    id UUID PRIMARY KEY,
    employee_id UUID NOT NULL,
    mark_type VARCHAR(20) NOT NULL,
    mark_timestamp TIMESTAMP NOT NULL,
    FOREIGN KEY (employee_id) REFERENCES employees(id)
);

CREATE TABLE time_records (
    id UUID PRIMARY KEY,
    employee_id UUID NOT NULL,
    record_date DATE NOT NULL,
    normal_hours NUMERIC(5,2) DEFAULT 0,
    extra_hours NUMERIC(5,2) DEFAULT 0,
    penalty_hours NUMERIC(5,2) DEFAULT 0,
    FOREIGN KEY (employee_id) REFERENCES employees(id),
    UNIQUE (employee_id, record_date)
);

CREATE INDEX idx_attendance_marks_employee_id ON attendance_marks(employee_id);
CREATE INDEX idx_time_records_employee_id ON time_records(employee_id);
