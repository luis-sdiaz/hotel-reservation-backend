package com.luis.diaz.hotelreservationbackend.mapper;

import com.luis.diaz.hotelreservationbackend.dto.ReservaRequestDTO;
import com.luis.diaz.hotelreservationbackend.dto.ReservaResponseDTO;
import com.luis.diaz.hotelreservationbackend.model.Cliente;
import com.luis.diaz.hotelreservationbackend.model.Habitacion;
import com.luis.diaz.hotelreservationbackend.model.Reserva;
import com.luis.diaz.hotelreservationbackend.model.ServicioAdicional;
import com.luis.diaz.hotelreservationbackend.model.TemporadaReserva;

import java.util.HashSet;
import java.util.Set;

public class ReservaMapper {

    private ReservaMapper() {
    }

    public static Reserva toEntity(ReservaRequestDTO dto) {
        Reserva reserva = new Reserva();

        Habitacion habitacion = new Habitacion();
        habitacion.setId(dto.getHabitacionId());
        reserva.setHabitacion(habitacion);

        Cliente cliente = new Cliente();
        cliente.setId(dto.getClienteId());
        reserva.setCliente(cliente);

        reserva.setFechaEntrada(dto.getFechaEntrada());
        reserva.setFechaSalida(dto.getFechaSalida());

        reserva.setTemporadaReserva(
                dto.getTemporadaReserva() != null
                        ? dto.getTemporadaReserva()
                        : TemporadaReserva.TEMPORADA_BAJA
        );

        Set<ServicioAdicional> servicios = new HashSet<>();

        if (dto.getServiciosAdicionalesIds() != null) {
            for (Long servicioId : dto.getServiciosAdicionalesIds()) {
                ServicioAdicional servicio = new ServicioAdicional();
                servicio.setId(servicioId);
                servicios.add(servicio);
            }
        }

        reserva.setServiciosAdicionales(servicios);

        return reserva;
    }

    public static ReservaResponseDTO toResponse(Reserva entity) {
        ReservaResponseDTO response = new ReservaResponseDTO();

        response.setId(entity.getId());

        if (entity.getHabitacion() != null) {
            response.setHabitacionId(entity.getHabitacion().getId());
        }

        if (entity.getCliente() != null) {
            response.setClienteId(entity.getCliente().getId());
        }

        response.setFechaEntrada(entity.getFechaEntrada());
        response.setFechaSalida(entity.getFechaSalida());
        response.setValorTotal(entity.getValorTotal());
        response.setEstadoReserva(entity.getEstadoReserva());
        response.setTemporadaReserva(entity.getTemporadaReserva());

        if (entity.getServiciosAdicionales() != null) {
            response.setServiciosAdicionalesIds(
                    entity.getServiciosAdicionales()
                            .stream()
                            .map(ServicioAdicional::getId)
                            .toList()
            );
        }

        return response;
    }
}