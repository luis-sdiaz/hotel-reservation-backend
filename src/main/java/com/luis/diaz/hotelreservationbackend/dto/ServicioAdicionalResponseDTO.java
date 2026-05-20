package com.luis.diaz.hotelreservationbackend.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(
        name = "ServicioAdicionalResponse",
        description = "Datos retornados al consultar un servicio adicional"
)
public class ServicioAdicionalResponseDTO {

    @Schema(description = "Identificador del servicio adicional", example = "1")
    private Long id;

    @Schema(description = "Nombre del servicio adicional", example = "DESAYUNO")
    private String nombre;

    @Schema(description = "Descripcion del servicio adicional", example = "Desayuno tipo buffet")
    private String descripcion;

    @Schema(description = "Precio del servicio adicional", example = "35000")
    private BigDecimal precio;

    @Schema(description = "Indica si el servicio adicional esta activo", example = "true")
    private Boolean activo;

    public ServicioAdicionalResponseDTO() {
    }

    public Long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }
}