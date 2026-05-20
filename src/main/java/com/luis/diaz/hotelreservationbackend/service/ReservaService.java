package com.luis.diaz.hotelreservationbackend.service;

import com.luis.diaz.hotelreservationbackend.dto.ResumenPagoResponseDTO;
import com.luis.diaz.hotelreservationbackend.dto.ReservaResponseDTO;
import com.luis.diaz.hotelreservationbackend.exception.ClienteNotFoundException;
import com.luis.diaz.hotelreservationbackend.exception.HabitacionNoDisponibleException;
import com.luis.diaz.hotelreservationbackend.mapper.ReservaMapper;
import com.luis.diaz.hotelreservationbackend.mapper.ServicioAdicionalMapper;
import com.luis.diaz.hotelreservationbackend.model.Cliente;
import com.luis.diaz.hotelreservationbackend.model.EstadoHabitacion;
import com.luis.diaz.hotelreservationbackend.model.EstadoReserva;
import com.luis.diaz.hotelreservationbackend.model.Habitacion;
import com.luis.diaz.hotelreservationbackend.model.Reserva;
import com.luis.diaz.hotelreservationbackend.model.ServicioAdicional;
import com.luis.diaz.hotelreservationbackend.model.TemporadaReserva;
import com.luis.diaz.hotelreservationbackend.repository.ClienteRepository;
import com.luis.diaz.hotelreservationbackend.repository.HabitacionRepository;
import com.luis.diaz.hotelreservationbackend.repository.ReservaRepository;
import com.luis.diaz.hotelreservationbackend.repository.ServicioAdicionalRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.Set;

@Service
public class ReservaService {

    private final ReservaRepository reservaRepository;
    private final HabitacionRepository habitacionRepository;
    private final ClienteRepository clienteRepository;
    private final ServicioAdicionalRepository servicioAdicionalRepository;

    public ReservaService(
            ReservaRepository reservaRepository,
            HabitacionRepository habitacionRepository,
            ClienteRepository clienteRepository,
            ServicioAdicionalRepository servicioAdicionalRepository
    ) {
        this.reservaRepository = reservaRepository;
        this.habitacionRepository = habitacionRepository;
        this.clienteRepository = clienteRepository;
        this.servicioAdicionalRepository = servicioAdicionalRepository;
    }

    @Transactional(readOnly = true)
    public Page<ReservaResponseDTO> listar(
            Pageable pageable,
            Long clienteId,
            Long habitacionId,
            EstadoReserva estado
    ) {
        return reservaRepository.findByFilters(clienteId, habitacionId, estado, pageable)
                .map(ReservaMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Reserva obtenerPorId(Long id) {
        return reservaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Reserva no encontrada con id: " + id));
    }

    @Transactional(readOnly = true)
    public Reserva obtenerPorIdConDetalles(Long id) {
        return reservaRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new EntityNotFoundException("Reserva no encontrada con id: " + id));
    }

    @Transactional
    public Reserva crearReserva(Reserva reserva) {
        validarReservaBase(reserva);

        LocalDate fechaEntrada = reserva.getFechaEntrada();
        LocalDate fechaSalida = reserva.getFechaSalida();
        Long habitacionId = reserva.getHabitacion().getId();
        Long clienteId = reserva.getCliente().getId();

        Habitacion habitacion = habitacionRepository.findByIdForUpdate(habitacionId)
                .orElseThrow(() -> new EntityNotFoundException("Habitacion no encontrada con id: " + habitacionId));

        if (!Boolean.TRUE.equals(habitacion.getActivo())) {
            throw new HabitacionNoDisponibleException("La habitacion con id " + habitacionId + " esta desactivada");
        }

        if (habitacion.getEstado() == EstadoHabitacion.MANTENIMIENTO) {
            throw new HabitacionNoDisponibleException("La habitacion con id " + habitacionId + " esta en mantenimiento");
        }

        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new ClienteNotFoundException("Cliente no encontrado con id: " + clienteId));

        if (!Boolean.TRUE.equals(cliente.getActivo())) {
            throw new ClienteNotFoundException("El cliente con id " + clienteId + " esta desactivado");
        }

        boolean existeSolapamiento = reservaRepository.existsOverlappingReservation(
                habitacion,
                fechaEntrada,
                fechaSalida
        );

        if (existeSolapamiento) {
            throw new HabitacionNoDisponibleException(
                    "La habitacion con id " + habitacionId + " ya tiene una reserva activa en las fechas solicitadas"
            );
        }

        Set<ServicioAdicional> servicios = resolverServiciosActivos(reserva.getServiciosAdicionales());

        TemporadaReserva temporada = reserva.getTemporadaReserva() != null
                ? reserva.getTemporadaReserva()
                : TemporadaReserva.TEMPORADA_BAJA;

        BigDecimal valorTotal = calcularValorTotal(
                habitacion.getPrecioBase(),
                fechaEntrada,
                fechaSalida,
                temporada,
                servicios
        );

        reserva.setHabitacion(habitacion);
        reserva.setCliente(cliente);
        reserva.setServiciosAdicionales(servicios);
        reserva.setTemporadaReserva(temporada);
        reserva.setValorTotal(valorTotal);
        reserva.setEstadoReserva(EstadoReserva.CONFIRMADA);

        Reserva reservaGuardada = reservaRepository.save(reserva);

        habitacion.setEstado(EstadoHabitacion.OCUPADA);
        habitacionRepository.save(habitacion);

        return reservaGuardada;
    }

    @Transactional
    public Reserva cancelarReserva(Long id) {
        Reserva reserva = obtenerPorIdConDetalles(id);

        if (reserva.getEstadoReserva() == EstadoReserva.CANCELADA) {
            return reserva;
        }

        reserva.setEstadoReserva(EstadoReserva.CANCELADA);
        Reserva actualizada = reservaRepository.save(reserva);

        liberarHabitacionSiNoTieneReservasActivas(actualizada);

        return actualizada;
    }

    @Transactional
    public Reserva finalizarReserva(Long id) {
        Reserva reserva = obtenerPorIdConDetalles(id);

        if (reserva.getEstadoReserva() == EstadoReserva.FINALIZADA) {
            return reserva;
        }

        if (reserva.getEstadoReserva() == EstadoReserva.CANCELADA) {
            throw new IllegalArgumentException("No se puede finalizar una reserva cancelada");
        }

        reserva.setEstadoReserva(EstadoReserva.FINALIZADA);
        Reserva actualizada = reservaRepository.save(reserva);

        liberarHabitacionSiNoTieneReservasActivas(actualizada);

        return actualizada;
    }

    @Transactional(readOnly = true)
    public ResumenPagoResponseDTO generarResumenPago(Long id) {
        Reserva reserva = obtenerPorIdConDetalles(id);

        Habitacion habitacion = reserva.getHabitacion();
        Cliente cliente = reserva.getCliente();

        long noches = calcularNoches(reserva.getFechaEntrada(), reserva.getFechaSalida());

        BigDecimal subtotalHabitacion = habitacion.getPrecioBase()
                .multiply(BigDecimal.valueOf(noches))
                .multiply(reserva.getTemporadaReserva().getMultiplicador())
                .setScale(2, RoundingMode.HALF_UP);

        BigDecimal subtotalServicios = reserva.getServiciosAdicionales()
                .stream()
                .map(ServicioAdicional::getPrecio)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);

        ResumenPagoResponseDTO dto = new ResumenPagoResponseDTO();
        dto.setReservaId(reserva.getId());

        dto.setClienteId(cliente.getId());
        dto.setClienteNombre(cliente.getNombre());
        dto.setClienteDocumento(cliente.getDocumento());
        dto.setClienteEmail(cliente.getEmail());

        dto.setHabitacionId(habitacion.getId());
        dto.setHabitacionNumero(habitacion.getNumero());
        dto.setHabitacionTipo(habitacion.getTipo().name());

        dto.setFechaEntrada(reserva.getFechaEntrada());
        dto.setFechaSalida(reserva.getFechaSalida());
        dto.setNumeroNoches(noches);

        dto.setTemporadaReserva(reserva.getTemporadaReserva());
        dto.setMultiplicadorTemporada(reserva.getTemporadaReserva().getMultiplicador());

        dto.setPrecioBaseHabitacion(habitacion.getPrecioBase());
        dto.setSubtotalHabitacion(subtotalHabitacion);
        dto.setSubtotalServicios(subtotalServicios);
        dto.setValorTotal(reserva.getValorTotal());
        dto.setEstadoReserva(reserva.getEstadoReserva());

        dto.setServiciosAdicionales(
                reserva.getServiciosAdicionales()
                        .stream()
                        .map(ServicioAdicionalMapper::toResponse)
                        .toList()
        );

        return dto;
    }

    private void validarReservaBase(Reserva reserva) {
        if (reserva == null) {
            throw new IllegalArgumentException("La reserva no puede ser null");
        }

        if (reserva.getFechaEntrada() == null || reserva.getFechaSalida() == null) {
            throw new IllegalArgumentException("Las fechas de entrada y salida son requeridas");
        }

        if (!reserva.getFechaSalida().isAfter(reserva.getFechaEntrada())) {
            throw new IllegalArgumentException("La fecha de salida debe ser posterior a la fecha de entrada");
        }

        if (reserva.getHabitacion() == null || reserva.getHabitacion().getId() == null) {
            throw new IllegalArgumentException("La reserva debe contener referencia a la habitacion con su id");
        }

        if (reserva.getCliente() == null || reserva.getCliente().getId() == null) {
            throw new IllegalArgumentException("La reserva debe contener referencia al cliente con su id");
        }
    }

    private Set<ServicioAdicional> resolverServiciosActivos(Set<ServicioAdicional> serviciosSolicitados) {
        Set<ServicioAdicional> servicios = new HashSet<>();

        if (serviciosSolicitados == null || serviciosSolicitados.isEmpty()) {
            return servicios;
        }

        for (ServicioAdicional servicioSolicitado : serviciosSolicitados) {
            Long id = servicioSolicitado.getId();

            if (id == null) {
                throw new IllegalArgumentException("El id del servicio adicional es obligatorio");
            }

            ServicioAdicional servicio = servicioAdicionalRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Servicio adicional no encontrado con id: " + id));

            if (!Boolean.TRUE.equals(servicio.getActivo())) {
                throw new IllegalArgumentException("El servicio adicional con id " + id + " esta desactivado");
            }

            servicios.add(servicio);
        }

        return servicios;
    }

    private BigDecimal calcularValorTotal(
            BigDecimal precioBase,
            LocalDate fechaEntrada,
            LocalDate fechaSalida,
            TemporadaReserva temporada,
            Set<ServicioAdicional> servicios
    ) {
        if (precioBase == null) {
            throw new IllegalStateException("La habitacion no tiene precio base definido");
        }

        long noches = calcularNoches(fechaEntrada, fechaSalida);

        BigDecimal subtotalHabitacion = precioBase
                .multiply(BigDecimal.valueOf(noches))
                .multiply(temporada.getMultiplicador());

        BigDecimal subtotalServicios = servicios
                .stream()
                .map(ServicioAdicional::getPrecio)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return subtotalHabitacion
                .add(subtotalServicios)
                .setScale(2, RoundingMode.HALF_UP);
    }

    private long calcularNoches(LocalDate fechaEntrada, LocalDate fechaSalida) {
        long noches = ChronoUnit.DAYS.between(fechaEntrada, fechaSalida);

        if (noches <= 0) {
            throw new IllegalArgumentException("El periodo de la reserva debe tener al menos una noche");
        }

        return noches;
    }

    private void liberarHabitacionSiNoTieneReservasActivas(Reserva reserva) {
        Habitacion habitacion = reserva.getHabitacion();

        boolean tieneOtraReservaActiva = reservaRepository.existsAnotherActiveReservationForHabitacion(
                habitacion,
                reserva.getId()
        );

        if (!tieneOtraReservaActiva && habitacion.getEstado() != EstadoHabitacion.MANTENIMIENTO) {
            habitacion.setEstado(EstadoHabitacion.DISPONIBLE);
            habitacionRepository.save(habitacion);
        }
    }
}
