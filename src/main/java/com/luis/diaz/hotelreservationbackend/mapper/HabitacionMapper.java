package com.luis.diaz.hotelreservationbackend.mapper;

import com.luis.diaz.hotelreservationbackend.dto.HabitacionRequestDTO;
import com.luis.diaz.hotelreservationbackend.dto.HabitacionResponseDTO;
import com.luis.diaz.hotelreservationbackend.model.EstadoHabitacion;
import com.luis.diaz.hotelreservationbackend.model.Habitacion;

public class HabitacionMapper {

    private HabitacionMapper() {
    }

    public static Habitacion toEntity(HabitacionRequestDTO dto) {
        Habitacion h = new Habitacion();
        h.setNumero(dto.getNumero());
        h.setTipo(dto.getTipo());
        h.setPrecioBase(dto.getPrecioBase());
        h.setEstado(dto.getEstado() != null ? dto.getEstado() : EstadoHabitacion.DISPONIBLE);
        return h;
    }

    public static HabitacionResponseDTO toResponse(Habitacion entity) {
        HabitacionResponseDTO dto = new HabitacionResponseDTO();
        dto.setId(entity.getId());
        dto.setNumero(entity.getNumero());
        dto.setTipo(entity.getTipo());
        dto.setPrecioBase(entity.getPrecioBase());
        dto.setEstado(entity.getEstado());
        dto.setActivo(Boolean.TRUE.equals(entity.getActivo()));
        return dto;
    }
}

