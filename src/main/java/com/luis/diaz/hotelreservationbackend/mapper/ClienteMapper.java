package com.luis.diaz.hotelreservationbackend.mapper;

import com.luis.diaz.hotelreservationbackend.dto.ClienteRequestDTO;
import com.luis.diaz.hotelreservationbackend.dto.ClienteResponseDTO;
import com.luis.diaz.hotelreservationbackend.model.Cliente;

public class ClienteMapper {

    private ClienteMapper() {
    }

    public static Cliente toEntity(ClienteRequestDTO dto) {
        Cliente c = new Cliente();
        c.setNombre(dto.getNombre());
        c.setEmail(dto.getEmail());
        c.setDocumento(dto.getDocumento());
        c.setTelefono(dto.getTelefono());
        return c;
    }

    public static ClienteResponseDTO toResponse(Cliente entity) {
        ClienteResponseDTO dto = new ClienteResponseDTO();
        dto.setId(entity.getId());
        dto.setNombre(entity.getNombre());
        dto.setEmail(entity.getEmail());
        dto.setDocumento(entity.getDocumento());
        dto.setTelefono(entity.getTelefono());
        dto.setActivo(Boolean.TRUE.equals(entity.getActivo()));
        return dto;
    }
}

