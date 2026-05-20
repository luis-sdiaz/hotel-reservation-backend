package com.luis.diaz.hotelreservationbackend.dto;

import com.luis.diaz.hotelreservationbackend.model.EstadoReserva;
import com.luis.diaz.hotelreservationbackend.model.TemporadaReserva;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Schema(
        name = "ResumenPagoResponse",
        description = "Resumen de pago o factura de una reserva"
)
public class ResumenPagoResponseDTO {

    private Long reservaId;

    private Long clienteId;
    private String clienteNombre;
    private String clienteDocumento;
    private String clienteEmail;

    private Long habitacionId;
    private String habitacionNumero;
    private String habitacionTipo;

    private LocalDate fechaEntrada;
    private LocalDate fechaSalida;
    private Long numeroNoches;

    private TemporadaReserva temporadaReserva;
    private BigDecimal multiplicadorTemporada;

    private BigDecimal precioBaseHabitacion;
    private BigDecimal subtotalHabitacion;
    private BigDecimal subtotalServicios;
    private BigDecimal valorTotal;

    private EstadoReserva estadoReserva;

    private List<ServicioAdicionalResponseDTO> serviciosAdicionales = new ArrayList<>();

    public ResumenPagoResponseDTO() {
    }

    public Long getReservaId() {
        return reservaId;
    }

    public void setReservaId(Long reservaId) {
        this.reservaId = reservaId;
    }

    public Long getClienteId() {
        return clienteId;
    }

    public void setClienteId(Long clienteId) {
        this.clienteId = clienteId;
    }

    public String getClienteNombre() {
        return clienteNombre;
    }

    public void setClienteNombre(String clienteNombre) {
        this.clienteNombre = clienteNombre;
    }

    public String getClienteDocumento() {
        return clienteDocumento;
    }

    public void setClienteDocumento(String clienteDocumento) {
        this.clienteDocumento = clienteDocumento;
    }

    public String getClienteEmail() {
        return clienteEmail;
    }

    public void setClienteEmail(String clienteEmail) {
        this.clienteEmail = clienteEmail;
    }

    public Long getHabitacionId() {
        return habitacionId;
    }

    public void setHabitacionId(Long habitacionId) {
        this.habitacionId = habitacionId;
    }

    public String getHabitacionNumero() {
        return habitacionNumero;
    }

    public void setHabitacionNumero(String habitacionNumero) {
        this.habitacionNumero = habitacionNumero;
    }

    public String getHabitacionTipo() {
        return habitacionTipo;
    }

    public void setHabitacionTipo(String habitacionTipo) {
        this.habitacionTipo = habitacionTipo;
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

    public Long getNumeroNoches() {
        return numeroNoches;
    }

    public void setNumeroNoches(Long numeroNoches) {
        this.numeroNoches = numeroNoches;
    }

    public TemporadaReserva getTemporadaReserva() {
        return temporadaReserva;
    }

    public void setTemporadaReserva(TemporadaReserva temporadaReserva) {
        this.temporadaReserva = temporadaReserva;
    }

    public BigDecimal getMultiplicadorTemporada() {
        return multiplicadorTemporada;
    }

    public void setMultiplicadorTemporada(BigDecimal multiplicadorTemporada) {
        this.multiplicadorTemporada = multiplicadorTemporada;
    }

    public BigDecimal getPrecioBaseHabitacion() {
        return precioBaseHabitacion;
    }

    public void setPrecioBaseHabitacion(BigDecimal precioBaseHabitacion) {
        this.precioBaseHabitacion = precioBaseHabitacion;
    }

    public BigDecimal getSubtotalHabitacion() {
        return subtotalHabitacion;
    }

    public void setSubtotalHabitacion(BigDecimal subtotalHabitacion) {
        this.subtotalHabitacion = subtotalHabitacion;
    }

    public BigDecimal getSubtotalServicios() {
        return subtotalServicios;
    }

    public void setSubtotalServicios(BigDecimal subtotalServicios) {
        this.subtotalServicios = subtotalServicios;
    }

    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(BigDecimal valorTotal) {
        this.valorTotal = valorTotal;
    }

    public EstadoReserva getEstadoReserva() {
        return estadoReserva;
    }

    public void setEstadoReserva(EstadoReserva estadoReserva) {
        this.estadoReserva = estadoReserva;
    }

    public List<ServicioAdicionalResponseDTO> getServiciosAdicionales() {
        return serviciosAdicionales;
    }

    public void setServiciosAdicionales(List<ServicioAdicionalResponseDTO> serviciosAdicionales) {
        this.serviciosAdicionales = serviciosAdicionales;
    }
}