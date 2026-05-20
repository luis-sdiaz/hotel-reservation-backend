package com.luis.diaz.hotelreservationbackend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(name = "ClienteRequest", description = "Datos necesarios para crear o actualizar un cliente")
public class ClienteRequestDTO {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 2, max = 120, message = "El nombre debe tener entre 2 y 120 caracteres")
    @Schema(description = "Nombre completo del cliente", example = "Juan Perez", required = true)
    private String nombre;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email no es valido")
    @Schema(description = "Correo electronico del cliente", example = "juan.perez@example.com", required = true)
    private String email;

    @NotBlank(message = "El documento es obligatorio")
    @Size(min = 4, max = 40, message = "El documento debe tener entre 4 y 40 caracteres")
    @Schema(description = "Documento de identidad", example = "CC-12345678", required = true)
    private String documento;

    @Size(max = 30, message = "El telefono no puede superar 30 caracteres")
    @Schema(description = "Telefono de contacto", example = "+57 300 1234567")
    private String telefono;

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

