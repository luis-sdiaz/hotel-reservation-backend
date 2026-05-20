package com.luis.diaz.hotelreservationbackend.service;

import com.luis.diaz.hotelreservationbackend.exception.HabitacionNoDisponibleException;
import com.luis.diaz.hotelreservationbackend.model.EstadoHabitacion;
import com.luis.diaz.hotelreservationbackend.model.TipoHabitacion;
import com.luis.diaz.hotelreservationbackend.model.Habitacion;
import com.luis.diaz.hotelreservationbackend.model.Cliente;
import com.luis.diaz.hotelreservationbackend.model.Reserva;
import com.luis.diaz.hotelreservationbackend.repository.HabitacionRepository;
import com.luis.diaz.hotelreservationbackend.repository.ClienteRepository;
import com.luis.diaz.hotelreservationbackend.repository.ReservaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test de integración que simula dos transacciones concurrentes intentando reservar la misma habitación.
 * Debe estar en src/test/java para que Maven/JUnit lo ejecute.
 *
 * NOTA: Para reproducir locking real es preferible usar Testcontainers con MySQL/Postgres.
 */
@SpringBootTest
@ActiveProfiles("test")
public class ReservaServiceConcurrencyTest {

    @Autowired
    private ReservaService reservaService;

    @Autowired
    private HabitacionRepository habitacionRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private ReservaRepository reservaRepository;

    @BeforeEach
    public void setUp() {
        // limpiar tablas antes de cada prueba
        reservaRepository.deleteAll();
        habitacionRepository.deleteAll();
        clienteRepository.deleteAll();
    }

    @Test
    public void whenTwoConcurrentReservations_thenOnlyOneSucceeds() throws Exception {
        // Preparar datos
        Habitacion habitacion = new Habitacion();
        habitacion.setNumero("101");
        habitacion.setTipo(TipoHabitacion.SIMPLE);
        habitacion.setPrecioBase(new BigDecimal("100.00"));
        habitacion.setEstado(EstadoHabitacion.DISPONIBLE);
        habitacion = habitacionRepository.save(habitacion);

        Cliente cliente1 = new Cliente();
        cliente1.setNombre("Cliente Uno");
        cliente1.setEmail("c1@example.com");
        cliente1.setDocumento("DOC1");
        cliente1.setTelefono("555-0001");
        Cliente cliente2 = new Cliente();
        cliente2.setNombre("Cliente Dos");
        cliente2.setEmail("c2@example.com");
        cliente2.setDocumento("DOC2");
        cliente2.setTelefono("555-0002");
        cliente1 = clienteRepository.save(cliente1);
        cliente2 = clienteRepository.save(cliente2);

        LocalDate fechaEntrada = LocalDate.now().plusDays(1);
        LocalDate fechaSalida = fechaEntrada.plusDays(3);

        // Guardar ids en variables finales (sólo valores primitivos/immutables se capturan sin problema)
        final Long habitacionId = habitacion.getId();
        final Long cliente1Id = cliente1.getId();
        final Long cliente2Id = cliente2.getId();
        final LocalDate fe = fechaEntrada;
        final LocalDate fs = fechaSalida;

        // Preparar concurrencia
        final ExecutorService executor = Executors.newFixedThreadPool(2);
        final CountDownLatch readyLatch = new CountDownLatch(2);
        final CountDownLatch startLatch = new CountDownLatch(1);

        try {
            Callable<Boolean> task1 = () -> {
                readyLatch.countDown();
                try {
                    startLatch.await();

                    // Construir la Reserva localmente dentro de la lambda (no capturamos referencias mutables externas)
                    Reserva local = new Reserva();
                    local.setHabitacion(new Habitacion()); local.getHabitacion().setId(habitacionId);
                    local.setCliente(new Cliente()); local.getCliente().setId(cliente1Id);
                    local.setFechaEntrada(fe);
                    local.setFechaSalida(fs);

                    reservaService.crearReserva(local);
                    return true; // éxito
                } catch (HabitacionNoDisponibleException ex) {
                    return false; // no disponible
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw ie;
                }
            };

            Callable<Boolean> task2 = () -> {
                readyLatch.countDown();
                try {
                    startLatch.await();

                    Reserva local = new Reserva();
                    local.setHabitacion(new Habitacion()); local.getHabitacion().setId(habitacionId);
                    local.setCliente(new Cliente()); local.getCliente().setId(cliente2Id);
                    local.setFechaEntrada(fe);
                    local.setFechaSalida(fs);

                    reservaService.crearReserva(local);
                    return true;
                } catch (HabitacionNoDisponibleException ex) {
                    return false;
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw ie;
                }
            };

            List<Future<Boolean>> futures = new ArrayList<>();
            futures.add(executor.submit(task1));
            futures.add(executor.submit(task2));

            // Esperar a que ambos hilos estén listos y lanzar a la vez
            if (!readyLatch.await(5, TimeUnit.SECONDS)) {
                fail("Timeout esperando a que los hilos estén listos");
            }
            startLatch.countDown();

            int successCount = 0;
            int failCount = 0;
            for (Future<Boolean> f : futures) {
                try {
                    Boolean result = f.get(10, TimeUnit.SECONDS);
                    if (Boolean.TRUE.equals(result)) successCount++; else failCount++;
                } catch (ExecutionException ee) {
                    // Re-throw la causa para que JUnit informe correctamente
                    Throwable cause = ee.getCause();
                    if (cause instanceof RuntimeException) throw (RuntimeException) cause;
                    throw ee;
                } catch (TimeoutException te) {
                    fail("Timeout esperando resultado de una tarea concurrente");
                }
            }

            // Assertions
            assertEquals(1, successCount, "Solo una reserva debe haberse creado con éxito");
            assertEquals(1, failCount, "Una reserva debe haber fallado por habitación no disponible");

            long countReservas = reservaRepository.findAll()
                    .stream()
                    .filter(r -> r.getHabitacion() != null && habitacionId.equals(r.getHabitacion().getId()))
                    .count();
            assertEquals(1L, countReservas, "Debe existir exactamente 1 reserva persistida para la habitación");
        } finally {
            executor.shutdownNow();
            if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                // registrar para evitar advertencia de cuerpo vacío
                System.err.println("Executor no terminó después de shutdownNow()");
            }
        }
    }
}
