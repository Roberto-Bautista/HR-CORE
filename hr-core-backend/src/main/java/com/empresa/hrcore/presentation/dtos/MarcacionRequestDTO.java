package com.empresa.hrcore.presentation.dtos;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public class MarcacionRequestDTO {
    
    @NotNull(message = "El empleadoId es obligatorio")
    private UUID empleadoId;
    
    @NotNull(message = "El tipo de marcacion es obligatorio (ENTRADA o SALIDA)")
    private String tipo;

    public UUID getEmpleadoId() {
        return empleadoId;
    }

    public void setEmpleadoId(UUID empleadoId) {
        this.empleadoId = empleadoId;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
}
