package com.luis.diaz.hotelreservationbackend.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "ClienteResponse", description = "Datos devueltos al consultar un cliente")
public class ClienteResponseDTO {

    @Schema(description = "Identificador del cliente", example = "1")
    private Long id;

    @Schema(description = "Nombre completo del cliente", example = "Juan Perez")
    private String nombre;

    @Schema(description = "Correo electronico", example = "juan.perez@example.com")
    private String email;

    @Schema(description = "Documento de identidad", example = "CC-12345678")
    private String documento;

    @Schema(description = "Telefono de contacto", example = "+57 300 1234567")
    private String telefono;

    @Schema(description = "Indica si el cliente esta activo", example = "true")
    private boolean activo;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDocumento() {
        return documento;
    }

    public void setDocumento(String documento) {
        this.documento = documento;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }
}

