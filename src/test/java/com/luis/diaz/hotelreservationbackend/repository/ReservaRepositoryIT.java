package com.luis.diaz.hotelreservationbackend.repository;

import com.luis.diaz.hotelreservationbackend.model.Cliente;
import com.luis.diaz.hotelreservationbackend.model.Habitacion;
import com.luis.diaz.hotelreservationbackend.model.Reserva;
import com.luis.diaz.hotelreservationbackend.model.TipoHabitacion;
import com.luis.diaz.hotelreservationbackend.model.EstadoReserva;
import com.luis.diaz.hotelreservationbackend.model.EstadoHabitacion;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
public class ReservaRepositoryIT {

    @Autowired
    private ReservaRepository reservaRepository;

    @Autowired
    private HabitacionRepository habitacionRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Test
    @DisplayName("existsOverlappingReservation detecta solapamiento correctamente")
    void existsOverlappingReservation() {
        Habitacion h = new Habitacion();
        h.setNumero("401");
        h.setTipo(TipoHabitacion.SIMPLE);
        h.setPrecioBase(new BigDecimal("120.00"));
        h.setEstado(EstadoHabitacion.DISPONIBLE);
        h = habitacionRepository.save(h);

        Cliente c = new Cliente();
        c.setNombre("Cliente");
        c.setEmail("c@example.com");
        c.setDocumento("DOC");
        c.setTelefono("555");
        c = clienteRepository.save(c);

        LocalDate a = LocalDate.now().plusDays(1);
        LocalDate b = a.plusDays(3);

        // Reserva existente que ocupa [a,b)
        Reserva r = new Reserva();
        r.setHabitacion(h);
        r.setCliente(c);
        r.setFechaEntrada(a);
        r.setFechaSalida(b);
        r.setValorTotal(new BigDecimal("360.00"));
        r.setEstadoReserva(EstadoReserva.CONFIRMADA);
        reservaRepository.save(r);

        // Caso solapado (entra antes de salida)
        boolean overlapping1 = reservaRepository.existsOverlappingReservation(h, a.plusDays(1), b.plusDays(1));
        assertTrue(overlapping1);

        // Caso no solapado (completamente antes)
        boolean overlapping2 = reservaRepository.existsOverlappingReservation(h, a.minusDays(5), a.minusDays(1));
        assertFalse(overlapping2);
    }

    @Test
    void existsOverlappingReservation_reservaCancelada_retornaFalse() {
        Habitacion habitacion = guardarHabitacion("402");
        Cliente cliente = guardarCliente("cancelada@example.com");
        LocalDate entrada = LocalDate.now().plusDays(10);
        LocalDate salida = entrada.plusDays(3);
        guardarReserva(habitacion, cliente, entrada, salida, EstadoReserva.CANCELADA);

        boolean solapada = reservaRepository.existsOverlappingReservation(
                habitacion,
                entrada.plusDays(1),
                salida.plusDays(1)
        );

        assertFalse(solapada);
    }

    @Test
    void existsOverlappingReservation_reservaFinalizada_retornaFalse() {
        Habitacion habitacion = guardarHabitacion("403");
        Cliente cliente = guardarCliente("finalizada@example.com");
        LocalDate entrada = LocalDate.now().plusDays(10);
        LocalDate salida = entrada.plusDays(3);
        guardarReserva(habitacion, cliente, entrada, salida, EstadoReserva.FINALIZADA);

        boolean solapada = reservaRepository.existsOverlappingReservation(
                habitacion,
                entrada.plusDays(1),
                salida.plusDays(1)
        );

        assertFalse(solapada);
    }

    @Test
    void existsOverlappingReservation_fechasSeparadas_retornaFalse() {
        Habitacion habitacion = guardarHabitacion("404");
        Cliente cliente = guardarCliente("separada@example.com");
        LocalDate entrada = LocalDate.now().plusDays(10);
        LocalDate salida = entrada.plusDays(3);
        guardarReserva(habitacion, cliente, entrada, salida, EstadoReserva.CONFIRMADA);

        boolean solapada = reservaRepository.existsOverlappingReservation(
                habitacion,
                salida.plusDays(1),
                salida.plusDays(3)
        );

        assertFalse(solapada);
    }

    private Habitacion guardarHabitacion(String numero) {
        Habitacion habitacion = new Habitacion();
        habitacion.setNumero(numero);
        habitacion.setTipo(TipoHabitacion.SIMPLE);
        habitacion.setPrecioBase(new BigDecimal("120.00"));
        habitacion.setEstado(EstadoHabitacion.DISPONIBLE);
        habitacion.setActivo(true);
        return habitacionRepository.save(habitacion);
    }

    private Cliente guardarCliente(String email) {
        Cliente cliente = new Cliente();
        cliente.setNombre("Cliente");
        cliente.setEmail(email);
        cliente.setDocumento("DOC-" + email);
        cliente.setTelefono("555");
        cliente.setActivo(true);
        return clienteRepository.save(cliente);
    }

    private Reserva guardarReserva(
            Habitacion habitacion,
            Cliente cliente,
            LocalDate entrada,
            LocalDate salida,
            EstadoReserva estado
    ) {
        Reserva reserva = new Reserva();
        reserva.setHabitacion(habitacion);
        reserva.setCliente(cliente);
        reserva.setFechaEntrada(entrada);
        reserva.setFechaSalida(salida);
        reserva.setValorTotal(new BigDecimal("360.00"));
        reserva.setEstadoReserva(estado);
        return reservaRepository.save(reserva);
    }
}
