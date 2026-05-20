package com.luis.diaz.hotelreservationbackend.dto;

import com.luis.diaz.hotelreservationbackend.model.EstadoReserva;
import com.luis.diaz.hotelreservationbackend.model.TemporadaReserva;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Schema(
        name = "ReservaResponse",
        description = "Datos retornados al consultar una reserva"
)
public class ReservaResponseDTO {

    @Schema(description = "Identificador de la reserva", example = "1")
    private Long id;

    @Schema(description = "Id de la habitacion reservada", example = "1")
    private Long habitacionId;

    @Schema(description = "Id del cliente asociado", example = "1")
    private Long clienteId;

    @Schema(description = "Fecha de entrada", example = "2026-06-01")
    private LocalDate fechaEntrada;

    @Schema(description = "Fecha de salida", example = "2026-06-04")
    private LocalDate fechaSalida;

    @Schema(description = "Valor total calculado de la reserva", example = "435000")
    private BigDecimal valorTotal;

    @Schema(description = "Estado actual de la reserva", example = "CONFIRMADA")
    private EstadoReserva estadoReserva;

    @Schema(description = "Temporada aplicada a la reserva", example = "TEMPORADA_ALTA")
    private TemporadaReserva temporadaReserva;

    @Schema(description = "Ids de servicios adicionales asociados", example = "[1, 2]")
    private List<Long> serviciosAdicionalesIds = new ArrayList<>();

    public ReservaResponseDTO() {
    }

    public Long getId() {
        return id;
    }

    public Long getHabitacionId() {
        return habitacionId;
    }

    public Long getClienteId() {
        return clienteId;
    }

    public LocalDate getFechaEntrada() {
        return fechaEntrada;
    }

    public LocalDate getFechaSalida() {
        return fechaSalida;
    }

    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public EstadoReserva getEstadoReserva() {
        return estadoReserva;
    }

    public TemporadaReserva getTemporadaReserva() {
        return temporadaReserva;
    }

    public List<Long> getServiciosAdicionalesIds() {
        return serviciosAdicionalesIds;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setHabitacionId(Long habitacionId) {
        this.habitacionId = habitacionId;
    }

    public void setClienteId(Long clienteId) {
        this.clienteId = clienteId;
    }

    public void setFechaEntrada(LocalDate fechaEntrada) {
        this.fechaEntrada = fechaEntrada;
    }

    public void setFechaSalida(LocalDate fechaSalida) {
        this.fechaSalida = fechaSalida;
    }

    public void setValorTotal(BigDecimal valorTotal) {
        this.valorTotal = valorTotal;
    }

    public void setEstadoReserva(EstadoReserva estadoReserva) {
        this.estadoReserva = estadoReserva;
    }

    public void setTemporadaReserva(TemporadaReserva temporadaReserva) {
        this.temporadaReserva = temporadaReserva;
    }

    public void setServiciosAdicionalesIds(List<Long> serviciosAdicionalesIds) {
        this.serviciosAdicionalesIds = serviciosAdicionalesIds;
    }
}