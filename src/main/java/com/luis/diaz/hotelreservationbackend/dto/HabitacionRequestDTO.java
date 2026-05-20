package com.luis.diaz.hotelreservationbackend.dto;

import com.luis.diaz.hotelreservationbackend.model.EstadoHabitacion;
import com.luis.diaz.hotelreservationbackend.model.TipoHabitacion;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

@Schema(name = "HabitacionRequest", description = "Datos necesarios para crear o actualizar una habitacion")
public class HabitacionRequestDTO {

    @NotBlank(message = "El numero de habitacion es obligatorio")
    @Size(min = 1, max = 10, message = "El numero debe tener entre 1 y 10 caracteres")
    @Schema(description = "Numero de habitacion", example = "101", required = true)
    private String numero;

    @NotNull(message = "El tipo de habitacion es obligatorio")
    @Schema(description = "Tipo de habitacion", example = "SIMPLE", required = true)
    private TipoHabitacion tipo;

    @NotNull(message = "El precio base es obligatorio")
    @Positive(message = "El precio base debe ser positivo")
    @Schema(description = "Precio base por noche", example = "120.50", required = true)
    private BigDecimal precioBase;

    @Schema(description = "Estado actual de la habitacion", example = "DISPONIBLE")
    private EstadoHabitacion estado;

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public TipoHabitacion getTipo() {
        return tipo;
    }

    public void setTipo(TipoHabitacion tipo) {
        this.tipo = tipo;
    }

    public BigDecimal getPrecioBase() {
        return precioBase;
    }

    public void setPrecioBase(BigDecimal precioBase) {
        this.precioBase = precioBase;
    }

    public EstadoHabitacion getEstado() {
        return estado;
    }

    public void setEstado(EstadoHabitacion estado) {
        this.estado = estado;
    }
}

