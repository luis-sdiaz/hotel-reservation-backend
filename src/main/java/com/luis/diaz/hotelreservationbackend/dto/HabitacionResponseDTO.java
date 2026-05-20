package com.luis.diaz.hotelreservationbackend.dto;

import com.luis.diaz.hotelreservationbackend.model.EstadoHabitacion;
import com.luis.diaz.hotelreservationbackend.model.TipoHabitacion;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(name = "HabitacionResponse", description = "Datos devueltos al consultar una habitacion")
public class HabitacionResponseDTO {

    @Schema(description = "Identificador de la habitacion", example = "1")
    private Long id;

    @Schema(description = "Numero de habitacion", example = "101")
    private String numero;

    @Schema(description = "Tipo de habitacion", example = "SIMPLE")
    private TipoHabitacion tipo;

    @Schema(description = "Precio base por noche", example = "120.50")
    private BigDecimal precioBase;

    @Schema(description = "Estado actual de la habitacion", example = "DISPONIBLE")
    private EstadoHabitacion estado;

    @Schema(description = "Indica si la habitacion esta activa", example = "true")
    private boolean activo;

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

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }
}

