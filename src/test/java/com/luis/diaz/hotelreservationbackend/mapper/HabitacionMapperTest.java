package com.luis.diaz.hotelreservationbackend.mapper;

import com.luis.diaz.hotelreservationbackend.dto.HabitacionResponseDTO;
import com.luis.diaz.hotelreservationbackend.model.EstadoHabitacion;
import com.luis.diaz.hotelreservationbackend.model.Habitacion;
import com.luis.diaz.hotelreservationbackend.model.TipoHabitacion;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HabitacionMapperTest {

    @Test
    void toResponse_habitacion_mapeaCamposCorrectamente() {
        Habitacion habitacion = new Habitacion();
        habitacion.setId(1L);
        habitacion.setNumero("101");
        habitacion.setTipo(TipoHabitacion.DOUBLE);
        habitacion.setPrecioBase(new BigDecimal("50000.00"));
        habitacion.setEstado(EstadoHabitacion.DISPONIBLE);
        habitacion.setActivo(true);

        HabitacionResponseDTO dto = HabitacionMapper.toResponse(habitacion);

        assertEquals(1L, dto.getId());
        assertEquals("101", dto.getNumero());
        assertEquals(TipoHabitacion.DOUBLE, dto.getTipo());
        assertEquals(new BigDecimal("50000.00"), dto.getPrecioBase());
        assertEquals(EstadoHabitacion.DISPONIBLE, dto.getEstado());
        assertTrue(dto.isActivo());
    }
}
