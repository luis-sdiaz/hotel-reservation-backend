package com.luis.diaz.hotelreservationbackend.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "reservas")
public class Reserva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "habitacion_id", nullable = false)
    private Habitacion habitacion;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @Column(name = "fecha_entrada", nullable = false)
    private LocalDate fechaEntrada;

    @Column(name = "fecha_salida", nullable = false)
    private LocalDate fechaSalida;

    @Column(name = "valor_total", precision = 12, scale = 2, nullable = false)
    private BigDecimal valorTotal;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_reserva", nullable = false, length = 20)
    private EstadoReserva estadoReserva;

    @Enumerated(EnumType.STRING)
    @Column(name = "temporada_reserva", nullable = false, length = 30)
    private TemporadaReserva temporadaReserva = TemporadaReserva.TEMPORADA_BAJA;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "reserva_servicios_adicionales",
            joinColumns = @JoinColumn(name = "reserva_id"),
            inverseJoinColumns = @JoinColumn(name = "servicio_adicional_id")
    )
    private Set<ServicioAdicional> serviciosAdicionales = new HashSet<>();

    public Reserva() {
    }

    public Reserva(
            Long id,
            Habitacion habitacion,
            Cliente cliente,
            LocalDate fechaEntrada,
            LocalDate fechaSalida,
            BigDecimal valorTotal,
            EstadoReserva estadoReserva,
            TemporadaReserva temporadaReserva,
            Set<ServicioAdicional> serviciosAdicionales
    ) {
        this.id = id;
        this.habitacion = habitacion;
        this.cliente = cliente;
        this.fechaEntrada = fechaEntrada;
        this.fechaSalida = fechaSalida;
        this.valorTotal = valorTotal;
        this.estadoReserva = estadoReserva;
        this.temporadaReserva = temporadaReserva;
        this.serviciosAdicionales = serviciosAdicionales;
    }

    public Long getId() {
        return id;
    }

    public Habitacion getHabitacion() {
        return habitacion;
    }

    public Cliente getCliente() {
        return cliente;
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

    public Set<ServicioAdicional> getServiciosAdicionales() {
        return serviciosAdicionales;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setHabitacion(Habitacion habitacion) {
        this.habitacion = habitacion;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
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

    public void setServiciosAdicionales(Set<ServicioAdicional> serviciosAdicionales) {
        this.serviciosAdicionales = serviciosAdicionales;
    }
}
