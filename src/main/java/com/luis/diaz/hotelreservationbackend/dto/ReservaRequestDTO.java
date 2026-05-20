package com.luis.diaz.hotelreservationbackend.dto;

import com.luis.diaz.hotelreservationbackend.model.TemporadaReserva;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Schema(
        name = "ReservaRequest",
        description = "Datos necesarios para crear una reserva"
)
public class ReservaRequestDTO {

    @NotNull(message = "El id de habitacion es obligatorio")
    @Positive(message = "El id de habitacion debe ser positivo")
    @Schema(description = "Id de la habitacion a reservar", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long habitacionId;

    @NotNull(message = "El id de cliente es obligatorio")
    @Positive(message = "El id de cliente debe ser positivo")
    @Schema(description = "Id del cliente que realiza la reserva", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long clienteId;

    @NotNull(message = "La fecha de entrada es obligatoria")
    @FutureOrPresent(message = "La fecha de entrada debe ser hoy o futura")
    @Schema(description = "Fecha de entrada de la reserva", example = "2026-06-01", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDate fechaEntrada;

    @NotNull(message = "La fecha de salida es obligatoria")
    @Future(message = "La fecha de salida debe ser futura")
    @Schema(description = "Fecha de salida de la reserva", example = "2026-06-04", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDate fechaSalida;

    @Schema(description = "Temporada aplicada a la reserva", example = "TEMPORADA_BAJA")
    private TemporadaReserva temporadaReserva = TemporadaReserva.TEMPORADA_BAJA;

    @Schema(description = "Ids de servicios adicionales asociados a la reserva", example = "[1, 2]")
    private List<Long> serviciosAdicionalesIds = new ArrayList<>();

    public ReservaRequestDTO() {
    }

    public Long getHabitacionId() {
        return habitacionId;
    }

    public void setHabitacionId(Long habitacionId) {
        this.habitacionId = habitacionId;
    }

    public Long getClienteId() {
        return clienteId;
    }

    public void setClienteId(Long clienteId) {
        this.clienteId = clienteId;
    }

    public LocalDate getFechaEntrada() {
        return fechaEntrada;
    }

    public void setFechaEntrada(LocalDate fechaEntrada) {
        this.fechaEntrada = fechaEntrada;
    }

    public LocalDate getFechaSalida() {
        return fechaSalida;
    }

    public void setFechaSalida(LocalDate fechaSalida) {
        this.fechaSalida = fechaSalida;
    }

    public TemporadaReserva getTemporadaReserva() {
        return temporadaReserva;
    }

    public void setTemporadaReserva(TemporadaReserva temporadaReserva) {
        this.temporadaReserva = temporadaReserva;
    }

    public List<Long> getServiciosAdicionalesIds() {
        return serviciosAdicionalesIds;
    }

    public void setServiciosAdicionalesIds(List<Long> serviciosAdicionalesIds) {
        this.serviciosAdicionalesIds = serviciosAdicionalesIds;
    }
}