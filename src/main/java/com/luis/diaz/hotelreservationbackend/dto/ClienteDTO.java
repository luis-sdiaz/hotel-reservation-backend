package com.luis.diaz.hotelreservationbackend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(name = "ClienteDTO", description = "DTO que representa un cliente")
public class ClienteDTO {

    @Schema(description = "Identificador del cliente", example = "1")
    private Long id;

    @NotBlank
    @Schema(description = "Nombre completo del cliente", example = "Juan Pérez")
    private String nombre;

    @NotBlank
    @Email
    @Schema(description = "Correo electrónico", example = "juan.perez@example.com")
    private String email;

    @NotBlank
    @Schema(description = "Documento de identidad", example = "C.C. 12345678")
    private String documento;

    @Schema(description = "Teléfono de contacto", example = "+57 300 1234567")
    private String telefono;

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
}

