package com.luis.diaz.hotelreservationbackend.service;

import com.luis.diaz.hotelreservationbackend.exception.ClienteNotFoundException;
import com.luis.diaz.hotelreservationbackend.exception.HabitacionNoDisponibleException;
import com.luis.diaz.hotelreservationbackend.model.EstadoHabitacion;
import com.luis.diaz.hotelreservationbackend.model.EstadoReserva;
import com.luis.diaz.hotelreservationbackend.model.Habitacion;
import com.luis.diaz.hotelreservationbackend.model.Cliente;
import com.luis.diaz.hotelreservationbackend.model.Reserva;
import com.luis.diaz.hotelreservationbackend.model.ServicioAdicional;
import com.luis.diaz.hotelreservationbackend.model.TemporadaReserva;
import com.luis.diaz.hotelreservationbackend.repository.HabitacionRepository;
import com.luis.diaz.hotelreservationbackend.repository.ClienteRepository;
import com.luis.diaz.hotelreservationbackend.repository.ReservaRepository;
import com.luis.diaz.hotelreservationbackend.repository.ServicioAdicionalRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests para ReservaService usando Mockito (sin arrancar el contexto Spring).
 */
@ExtendWith(MockitoExtension.class)
public class ReservaServiceTest {

    @Mock
    private ReservaRepository reservaRepository;

    @Mock
    private HabitacionRepository habitacionRepository;

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private ServicioAdicionalRepository servicioAdicionalRepository;

    @InjectMocks
    private ReservaService reservaService;

    @BeforeEach
    void setup() {
        // Mockito inicializa mocks e injectMocks automáticamente con @ExtendWith(MockitoExtension.class)
    }

    @Test
    void crearReserva_success() {
        // Datos
        Long habitacionId = 10L;
        Long clienteId = 20L;
        LocalDate fe = LocalDate.now().plusDays(1);
        LocalDate fs = fe.plusDays(3); // 3 noches

        Habitacion habitacion = new Habitacion();
        habitacion.setId(habitacionId);
        habitacion.setPrecioBase(new BigDecimal("100.00"));
        habitacion.setEstado(EstadoHabitacion.DISPONIBLE);

        when(habitacionRepository.findByIdForUpdate(habitacionId)).thenReturn(Optional.of(habitacion));
        Cliente cliente = new Cliente();
        cliente.setId(clienteId);
        when(clienteRepository.findById(clienteId)).thenReturn(Optional.of(cliente));
        when(reservaRepository.existsOverlappingReservation(eq(habitacion), eq(fe), eq(fs))).thenReturn(false);
        // Simular que save devuelve la misma reserva con ID asignado
        when(reservaRepository.save(any(Reserva.class))).thenAnswer(invocation -> {
            Reserva r = invocation.getArgument(0);
            r.setId(1L);
            return r;
        });

        // Crear request
        Reserva req = new Reserva();
        req.setHabitacion(new Habitacion()); req.getHabitacion().setId(habitacionId);
        req.setCliente(new Cliente()); req.getCliente().setId(clienteId);
        req.setFechaEntrada(fe);
        req.setFechaSalida(fs);

        // Ejecutar
        Reserva saved = reservaService.crearReserva(req);

        // Verificaciones
        assertNotNull(saved);
        assertEquals(1L, saved.getId());
        // valor total = precioBase * noches = 100 * 3 = 300.00
        assertNotNull(saved.getValorTotal());
        assertEquals(new BigDecimal("300.00"), saved.getValorTotal());

        // verificar que se guardÃ³ la reserva y se cambiÃ³ estado de la habitaciÃ³n a OCUPADA
        verify(reservaRepository, times(1)).save(any(Reserva.class));
        ArgumentCaptor<Habitacion> habCaptor = ArgumentCaptor.forClass(Habitacion.class);
        verify(habitacionRepository, times(1)).save(habCaptor.capture());
        Habitacion savedHab = habCaptor.getValue();
        assertEquals(EstadoHabitacion.OCUPADA, savedHab.getEstado());
    }

    @Test
    void crearReserva_habitacionEstadoNoDisponible_throws() {
        Long habitacionId = 11L;
        Long clienteId = 21L;
        LocalDate fe = LocalDate.now().plusDays(5);
        LocalDate fs = fe.plusDays(2);

        Habitacion habitacion = new Habitacion();
        habitacion.setId(habitacionId);
        habitacion.setPrecioBase(new BigDecimal("80.00"));
        habitacion.setEstado(EstadoHabitacion.MANTENIMIENTO); // no disponible

        when(habitacionRepository.findByIdForUpdate(habitacionId)).thenReturn(Optional.of(habitacion));

        Reserva req = new Reserva();
        req.setHabitacion(new Habitacion()); req.getHabitacion().setId(habitacionId);
        req.setCliente(new Cliente()); req.getCliente().setId(clienteId);
        req.setFechaEntrada(fe);
        req.setFechaSalida(fs);

        assertThrows(HabitacionNoDisponibleException.class, () -> reservaService.crearReserva(req));

        verify(reservaRepository, never()).save(any());
    }

    @Test
    void crearReserva_habitacionSolapada_throws() {
        Long habitacionId = 12L;
        Long clienteId = 22L;
        LocalDate fe = LocalDate.now().plusDays(2);
        LocalDate fs = fe.plusDays(2);

        Habitacion habitacion = new Habitacion();
        habitacion.setId(habitacionId);
        habitacion.setPrecioBase(new BigDecimal("120.00"));
        habitacion.setEstado(EstadoHabitacion.DISPONIBLE);

        when(habitacionRepository.findByIdForUpdate(habitacionId)).thenReturn(Optional.of(habitacion));
        Cliente cliente = new Cliente();
        cliente.setId(clienteId);
        when(clienteRepository.findById(clienteId)).thenReturn(Optional.of(cliente));
        // Simular solapamiento
        when(reservaRepository.existsOverlappingReservation(eq(habitacion), eq(fe), eq(fs))).thenReturn(true);

        Reserva req = new Reserva();
        req.setHabitacion(new Habitacion()); req.getHabitacion().setId(habitacionId);
        req.setCliente(new Cliente()); req.getCliente().setId(clienteId);
        req.setFechaEntrada(fe);
        req.setFechaSalida(fs);

        assertThrows(HabitacionNoDisponibleException.class, () -> reservaService.crearReserva(req));

        verify(reservaRepository, never()).save(any());
    }

    @Test
    void crearReserva_fechasInvalidas_throws() {
        Long habitacionId = 13L;
        Long clienteId = 23L;
        LocalDate fe = LocalDate.now().plusDays(1);
        LocalDate fs = fe; // misma fecha -> invalido

        Reserva req = new Reserva();
        req.setHabitacion(new Habitacion()); req.getHabitacion().setId(habitacionId);
        req.setCliente(new Cliente()); req.getCliente().setId(clienteId);
        req.setFechaEntrada(fe);
        req.setFechaSalida(fs);

        assertThrows(IllegalArgumentException.class, () -> reservaService.crearReserva(req));

        verifyNoInteractions(habitacionRepository, clienteRepository, reservaRepository);
    }

    @Test
    void crearReserva_fechaSalidaAntesDeEntrada_deberiaFallar() {
        Reserva req = reservaBasica(13L, 23L, LocalDate.now().plusDays(4), LocalDate.now().plusDays(2));

        assertThrows(IllegalArgumentException.class, () -> reservaService.crearReserva(req));

        verifyNoInteractions(habitacionRepository, clienteRepository, reservaRepository);
    }

    @Test
    void crearReserva_habitacionInactiva_deberiaFallar() {
        Long habitacionId = 14L;
        Reserva req = reservaBasica(habitacionId, 24L, LocalDate.now().plusDays(1), LocalDate.now().plusDays(3));
        Habitacion habitacion = habitacion(habitacionId, EstadoHabitacion.DISPONIBLE, new BigDecimal("100.00"));
        habitacion.setActivo(false);

        when(habitacionRepository.findByIdForUpdate(habitacionId)).thenReturn(Optional.of(habitacion));

        assertThrows(HabitacionNoDisponibleException.class, () -> reservaService.crearReserva(req));

        verify(clienteRepository, never()).findById(anyLong());
        verify(reservaRepository, never()).save(any());
    }

    @Test
    void crearReserva_clienteInactivo_deberiaFallar() {
        Long habitacionId = 15L;
        Long clienteId = 25L;
        LocalDate fe = LocalDate.now().plusDays(1);
        LocalDate fs = fe.plusDays(2);
        Reserva req = reservaBasica(habitacionId, clienteId, fe, fs);
        Habitacion habitacion = habitacion(habitacionId, EstadoHabitacion.DISPONIBLE, new BigDecimal("100.00"));
        Cliente cliente = cliente(clienteId);
        cliente.setActivo(false);

        when(habitacionRepository.findByIdForUpdate(habitacionId)).thenReturn(Optional.of(habitacion));
        when(clienteRepository.findById(clienteId)).thenReturn(Optional.of(cliente));

        assertThrows(ClienteNotFoundException.class, () -> reservaService.crearReserva(req));

        verify(reservaRepository, never()).existsOverlappingReservation(any(), any(), any());
        verify(reservaRepository, never()).save(any());
    }

    @Test
    void crearReserva_conReservaCanceladaPrevia_deberiaPermitir() {
        Long habitacionId = 16L;
        Long clienteId = 26L;
        LocalDate fe = LocalDate.now().plusDays(1);
        LocalDate fs = fe.plusDays(2);
        Reserva req = reservaBasica(habitacionId, clienteId, fe, fs);
        Habitacion habitacion = habitacion(habitacionId, EstadoHabitacion.DISPONIBLE, new BigDecimal("100.00"));

        when(habitacionRepository.findByIdForUpdate(habitacionId)).thenReturn(Optional.of(habitacion));
        when(clienteRepository.findById(clienteId)).thenReturn(Optional.of(cliente(clienteId)));
        when(reservaRepository.existsOverlappingReservation(habitacion, fe, fs)).thenReturn(false);
        when(reservaRepository.save(any(Reserva.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Reserva saved = reservaService.crearReserva(req);

        assertEquals(EstadoReserva.CONFIRMADA, saved.getEstadoReserva());
        verify(reservaRepository).save(any(Reserva.class));
    }

    @Test
    void cancelarReserva_confirmada_deberiaCambiarEstadoACancelada() {
        Reserva reserva = reservaPersistida(30L, EstadoReserva.CONFIRMADA);
        when(reservaRepository.findByIdWithDetails(30L)).thenReturn(Optional.of(reserva));
        when(reservaRepository.save(any(Reserva.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(reservaRepository.existsAnotherActiveReservationForHabitacion(reserva.getHabitacion(), 30L)).thenReturn(false);

        Reserva actualizada = reservaService.cancelarReserva(30L);

        assertEquals(EstadoReserva.CANCELADA, actualizada.getEstadoReserva());
        assertEquals(EstadoHabitacion.DISPONIBLE, actualizada.getHabitacion().getEstado());
    }

    @Test
    void finalizarReserva_confirmada_deberiaCambiarEstadoAFinalizada() {
        Reserva reserva = reservaPersistida(31L, EstadoReserva.CONFIRMADA);
        when(reservaRepository.findByIdWithDetails(31L)).thenReturn(Optional.of(reserva));
        when(reservaRepository.save(any(Reserva.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(reservaRepository.existsAnotherActiveReservationForHabitacion(reserva.getHabitacion(), 31L)).thenReturn(false);

        Reserva actualizada = reservaService.finalizarReserva(31L);

        assertEquals(EstadoReserva.FINALIZADA, actualizada.getEstadoReserva());
        assertEquals(EstadoHabitacion.DISPONIBLE, actualizada.getHabitacion().getEstado());
    }

    @Test
    void crearReserva_conTemporadaYServicios_deberiaCalcularValorTotal() {
        Long habitacionId = 17L;
        Long clienteId = 27L;
        LocalDate fe = LocalDate.now().plusDays(1);
        LocalDate fs = fe.plusDays(2);
        Reserva req = reservaBasica(habitacionId, clienteId, fe, fs);
        req.setTemporadaReserva(TemporadaReserva.TEMPORADA_ALTA);
        ServicioAdicional solicitado = new ServicioAdicional();
        solicitado.setId(1L);
        req.setServiciosAdicionales(new HashSet<>(Set.of(solicitado)));

        Habitacion habitacion = habitacion(habitacionId, EstadoHabitacion.DISPONIBLE, new BigDecimal("100.00"));
        ServicioAdicional servicio = new ServicioAdicional(1L, "DESAYUNO", "Desayuno", new BigDecimal("30.00"), true);

        when(habitacionRepository.findByIdForUpdate(habitacionId)).thenReturn(Optional.of(habitacion));
        when(clienteRepository.findById(clienteId)).thenReturn(Optional.of(cliente(clienteId)));
        when(reservaRepository.existsOverlappingReservation(habitacion, fe, fs)).thenReturn(false);
        when(servicioAdicionalRepository.findById(1L)).thenReturn(Optional.of(servicio));
        when(reservaRepository.save(any(Reserva.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Reserva saved = reservaService.crearReserva(req);

        assertEquals(new BigDecimal("290.00"), saved.getValorTotal());
    }

    private Reserva reservaBasica(Long habitacionId, Long clienteId, LocalDate fechaEntrada, LocalDate fechaSalida) {
        Reserva req = new Reserva();
        req.setHabitacion(new Habitacion());
        req.getHabitacion().setId(habitacionId);
        req.setCliente(new Cliente());
        req.getCliente().setId(clienteId);
        req.setFechaEntrada(fechaEntrada);
        req.setFechaSalida(fechaSalida);
        return req;
    }

    private Habitacion habitacion(Long id, EstadoHabitacion estado, BigDecimal precioBase) {
        Habitacion habitacion = new Habitacion();
        habitacion.setId(id);
        habitacion.setPrecioBase(precioBase);
        habitacion.setEstado(estado);
        habitacion.setActivo(true);
        return habitacion;
    }

    private Cliente cliente(Long id) {
        Cliente cliente = new Cliente();
        cliente.setId(id);
        cliente.setActivo(true);
        return cliente;
    }

    private Reserva reservaPersistida(Long id, EstadoReserva estadoReserva) {
        Habitacion habitacion = habitacion(100L + id, EstadoHabitacion.OCUPADA, new BigDecimal("100.00"));
        Reserva reserva = reservaBasica(habitacion.getId(), 200L + id, LocalDate.now().plusDays(1), LocalDate.now().plusDays(2));
        reserva.setId(id);
        reserva.setHabitacion(habitacion);
        reserva.setCliente(cliente(200L + id));
        reserva.setValorTotal(new BigDecimal("100.00"));
        reserva.setEstadoReserva(estadoReserva);
        reserva.setTemporadaReserva(TemporadaReserva.TEMPORADA_BAJA);
        return reserva;
    }
}
