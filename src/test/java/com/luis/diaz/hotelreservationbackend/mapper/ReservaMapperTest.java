package com.luis.diaz.hotelreservationbackend.mapper;

import com.luis.diaz.hotelreservationbackend.dto.ReservaResponseDTO;
import com.luis.diaz.hotelreservationbackend.model.Cliente;
import com.luis.diaz.hotelreservationbackend.model.EstadoHabitacion;
import com.luis.diaz.hotelreservationbackend.model.EstadoReserva;
import com.luis.diaz.hotelreservationbackend.model.Habitacion;
import com.luis.diaz.hotelreservationbackend.model.Reserva;
import com.luis.diaz.hotelreservationbackend.model.ServicioAdicional;
import com.luis.diaz.hotelreservationbackend.model.TemporadaReserva;
import com.luis.diaz.hotelreservationbackend.model.TipoHabitacion;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ReservaMapperTest {

    @Test
    void toResponse_reservaCompleta_mapeaCamposCorrectamente() {
        Habitacion habitacion = new Habitacion();
        habitacion.setId(10L);
        habitacion.setNumero("101");
        habitacion.setTipo(TipoHabitacion.SIMPLE);
        habitacion.setPrecioBase(new BigDecimal("100.00"));
        habitacion.setEstado(EstadoHabitacion.DISPONIBLE);

        Cliente cliente = new Cliente();
        cliente.setId(20L);
        cliente.setNombre("Cliente Test");

        ServicioAdicional servicio = new ServicioAdicional(30L, "DESAYUNO", "Desayuno", new BigDecimal("20.00"), true);

        Reserva reserva = new Reserva();
        reserva.setId(1L);
        reserva.setHabitacion(habitacion);
        reserva.setCliente(cliente);
        reserva.setFechaEntrada(LocalDate.of(2026, 6, 1));
        reserva.setFechaSalida(LocalDate.of(2026, 6, 3));
        reserva.setValorTotal(new BigDecimal("260.00"));
        reserva.setEstadoReserva(EstadoReserva.CONFIRMADA);
        reserva.setTemporadaReserva(TemporadaReserva.TEMPORADA_ALTA);
        reserva.setServiciosAdicionales(Set.of(servicio));

        ReservaResponseDTO dto = ReservaMapper.toResponse(reserva);

        assertEquals(1L, dto.getId());
        assertEquals(10L, dto.getHabitacionId());
        assertEquals(20L, dto.getClienteId());
        assertEquals(new BigDecimal("260.00"), dto.getValorTotal());
        assertEquals(EstadoReserva.CONFIRMADA, dto.getEstadoReserva());
        assertEquals(TemporadaReserva.TEMPORADA_ALTA, dto.getTemporadaReserva());
        assertTrue(dto.getServiciosAdicionalesIds().contains(30L));
    }
}
