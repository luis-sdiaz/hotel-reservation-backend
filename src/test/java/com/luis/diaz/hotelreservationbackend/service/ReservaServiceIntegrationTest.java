package com.luis.diaz.hotelreservationbackend.service;

import com.luis.diaz.hotelreservationbackend.exception.ClienteNotFoundException;
import com.luis.diaz.hotelreservationbackend.model.EstadoHabitacion;
import com.luis.diaz.hotelreservationbackend.model.Habitacion;
import com.luis.diaz.hotelreservationbackend.model.Cliente;
import com.luis.diaz.hotelreservationbackend.model.Reserva;
import com.luis.diaz.hotelreservationbackend.model.TipoHabitacion;
import com.luis.diaz.hotelreservationbackend.repository.HabitacionRepository;
import com.luis.diaz.hotelreservationbackend.repository.ClienteRepository;
import com.luis.diaz.hotelreservationbackend.repository.ReservaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Tests de integración que usan H2 (configurada en application-test.properties).
 * NOTA: NO stubeamos habitacionRepository ni clienteRepository (son reales).
 * Solo mockeamos ReservaRepository para forzar fallo en save().
 */
@SpringBootTest
@ActiveProfiles("test")
public class ReservaServiceIntegrationTest {

    @Autowired
    private ReservaService reservaService;

    @Autowired
    private HabitacionRepository habitacionRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @MockBean
    private ReservaRepository reservaRepository; // mockeado intencionadamente

    @Test
    @DisplayName("Si falla al persistir reserva, la transacción se revierte y la habitación permanece DISPONIBLE")
    void whenSaveThrows_thenTransactionRolledBack() {
        // Preparar datos reales en BD (H2)
        Habitacion habitacion = new Habitacion();
        habitacion.setNumero("501");
        habitacion.setTipo(TipoHabitacion.SIMPLE);
        habitacion.setPrecioBase(new BigDecimal("100.00"));
        habitacion.setEstado(EstadoHabitacion.DISPONIBLE);
        habitacion = habitacionRepository.save(habitacion);

        Cliente cliente = new Cliente();
        cliente.setNombre("Rollback Test");
        cliente.setEmail("rb@example.com");
        cliente.setDocumento("DOCRB");
        cliente.setTelefono("555");
        cliente = clienteRepository.save(cliente);

        LocalDate fe = LocalDate.now().plusDays(1);
        LocalDate fs = fe.plusDays(2);

        // ReservaRepository (mock) : no solapamiento
        when(reservaRepository.existsOverlappingReservation(any(Habitacion.class), any(LocalDate.class), any(LocalDate.class))).thenReturn(false);
        // Forzar que save lanza excepción simulando fallo DB
        when(reservaRepository.save(any())).thenThrow(new RuntimeException("Simulated DB failure"));

        // Construir petición
        Reserva req = new Reserva();
        req.setHabitacion(new Habitacion()); req.getHabitacion().setId(habitacion.getId());
        req.setCliente(new Cliente()); req.getCliente().setId(cliente.getId());
        req.setFechaEntrada(fe);
        req.setFechaSalida(fs);

        // Ejecutar y esperar excepción
        RuntimeException ex = assertThrows(RuntimeException.class, () -> reservaService.crearReserva(req));
        assertTrue(ex.getMessage().contains("Simulated DB failure"));

        // Verificar que no se cambió el estado de la habitación en la BD (se hizo rollback)
        Habitacion reloaded = habitacionRepository.findById(habitacion.getId()).orElseThrow();
        assertEquals(EstadoHabitacion.DISPONIBLE, reloaded.getEstado());
    }

    @Test
    @DisplayName("Si el cliente no existe, lanza ClienteNotFoundException")
    void whenClienteNotFound_thenThrow() {
        Habitacion habitacion = new Habitacion();
        habitacion.setNumero("502");
        habitacion.setTipo(TipoHabitacion.SIMPLE);
        habitacion.setPrecioBase(new BigDecimal("90.00"));
        habitacion.setEstado(EstadoHabitacion.DISPONIBLE);
        habitacion = habitacionRepository.save(habitacion);

        Long nonExistentClienteId = 99999L;

        Reserva req = new Reserva();
        req.setHabitacion(new Habitacion()); req.getHabitacion().setId(habitacion.getId());
        req.setCliente(new Cliente()); req.getCliente().setId(nonExistentClienteId);
        req.setFechaEntrada(LocalDate.now().plusDays(1));
        req.setFechaSalida(LocalDate.now().plusDays(2));

        assertThrows(ClienteNotFoundException.class, () -> reservaService.crearReserva(req));

        // Verificar que no se guardó ninguna reserva (mock reservaRepository)
        verify(reservaRepository, never()).save(any());
    }
}
