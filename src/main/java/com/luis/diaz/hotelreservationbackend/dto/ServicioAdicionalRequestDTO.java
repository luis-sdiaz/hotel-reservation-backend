package com.luis.diaz.hotelreservationbackend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

@Schema(
        name = "ServicioAdicionalRequest",
        description = "Datos para crear o actualizar un servicio adicional"
)
public class ServicioAdicionalRequestDTO {

    @NotBlank(message = "El nombre del servicio es obligatorio")
    @Size(max = 80, message = "El nombre no puede superar 80 caracteres")
    @Schema(description = "Nombre del servicio adicional", example = "DESAYUNO")
    private String nombre;

    @NotBlank(message = "La descripcion del servicio es obligatoria")
    @Size(max = 255, message = "La descripcion no puede superar 255 caracteres")
    @Schema(description = "Descripcion del servicio adicional", example = "Desayuno tipo buffet")
    private String descripcion;

    @NotNull(message = "El precio del servicio es obligatorio")
    @Positive(message = "El precio debe ser positivo")
    @Schema(description = "Precio del servicio adicional", example = "35000")
    private BigDecimal precio;

    public ServicioAdicionalRequestDTO() {
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }


    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }
}