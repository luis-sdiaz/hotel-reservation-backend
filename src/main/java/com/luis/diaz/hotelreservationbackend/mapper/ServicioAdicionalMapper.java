package com.luis.diaz.hotelreservationbackend.mapper;

import com.luis.diaz.hotelreservationbackend.dto.ServicioAdicionalRequestDTO;
import com.luis.diaz.hotelreservationbackend.dto.ServicioAdicionalResponseDTO;
import com.luis.diaz.hotelreservationbackend.model.ServicioAdicional;

public class ServicioAdicionalMapper {

    private ServicioAdicionalMapper() {
    }

    public static ServicioAdicional toEntity(ServicioAdicionalRequestDTO dto) {
        ServicioAdicional entity = new ServicioAdicional();
        entity.setNombre(dto.getNombre());
        entity.setDescripcion(dto.getDescripcion());
        entity.setPrecio(dto.getPrecio());
        entity.setActivo(true);
        return entity;
    }

    public static ServicioAdicionalResponseDTO toResponse(ServicioAdicional entity) {
        ServicioAdicionalResponseDTO dto = new ServicioAdicionalResponseDTO();
        dto.setId(entity.getId());
        dto.setNombre(entity.getNombre());
        dto.setDescripcion(entity.getDescripcion());
        dto.setPrecio(entity.getPrecio());
        dto.setActivo(Boolean.TRUE.equals(entity.getActivo()));
        return dto;
    }
}