package com.luis.diaz.hotelreservationbackend.dto;

import com.luis.diaz.hotelreservationbackend.model.EstadoHabitacion;
import com.luis.diaz.hotelreservationbackend.model.TipoHabitacion;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

@Schema(name = "HabitacionDTO", description = "DTO que representa una habitación")
public class HabitacionDTO {

    @Schema(description = "Identificador de la habitación", example = "1")
    private Long id;

    @NotNull
    @Schema(description = "Número de habitación", example = "101")
    private String numero;

    @NotNull
    @Schema(description = "Tipo de habitación", example = "SIMPLE")
    private TipoHabitacion tipo;

    @NotNull
    @Positive
    @Schema(description = "Precio base por noche", example = "120.50")
    private BigDecimal precioBase;

    @Schema(description = "Estado actual de la habitación", example = "DISPONIBLE")
    private EstadoHabitacion estado;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

