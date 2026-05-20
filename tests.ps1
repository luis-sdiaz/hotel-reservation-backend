# create-tests.ps1
# Script que crea los tests (unit + integration) en sus rutas correspondientes.
# Escribe los ficheros en UTF-8 sin BOM para evitar problemas de encoding.
# Ejecuta desde la raíz del proyecto: C:\Users\luis_diaz\Desktop\hotel-reservation-backend

$base = (Get-Location).Path
Write-Host "Base path: $base"

# Función auxiliar para escribir fichero en UTF-8 sin BOM
function Write-Utf8NoBomFile([string]$path, [string]$content) {
    $dir = Split-Path -Parent $path
    if (!(Test-Path $dir)) {
        New-Item -ItemType Directory -Path $dir -Force | Out-Null
    }
    # Usar .NET API para escribir UTF8 sin BOM
    [System.IO.File]::WriteAllText($path, $content, (New-Object System.Text.UTF8Encoding($false)))
    Write-Host "Wrote $path"
}

### 1) Unit test: ReservaServiceTest.java
$path = Join-Path $base "src\test\java\com\luis\diaz\hotelreservationbackend\service\ReservaServiceTest.java"
$content = @'
package com.luis.diaz.hotelreservationbackend.service;

import com.luis.diaz.hotelreservationbackend.exception.HabitacionNoDisponibleException;
import com.luis.diaz.hotelreservationbackend.model.EstadoHabitacion;
import com.luis.diaz.hotelreservationbackend.model.EstadoReserva;
import com.luis.diaz.hotelreservationbackend.model.Habitacion;
import com.luis.diaz.hotelreservationbackend.model.Cliente;
import com.luis.diaz.hotelreservationbackend.model.Reserva;
import com.luis.diaz.hotelreservationbackend.repository.HabitacionRepository;
import com.luis.diaz.hotelreservationbackend.repository.ClienteRepository;
import com.luis.diaz.hotelreservationbackend.repository.ReservaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

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

        Cliente cliente = new Cliente();
        cliente.setId(clienteId);

        // Comportamiento de mocks
        when(habitacionRepository.findByIdForUpdate(habitacionId)).thenReturn(Optional.of(habitacion));
        when(clienteRepository.findById(clienteId)).thenReturn(Optional.of(cliente));
        when(reservaRepository.existsOverlappingReservation(eq(habitacion), eq(fe), eq(fs))).thenReturn(false);
        // Simular que save devuelve la misma reserva con id asignado
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

        // verificar que se guardó la reserva y se cambió estado de la habitación a OCUPADA
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
        when(clienteRepository.findById(clienteId)).thenReturn(Optional.of(new Cliente(null, "x","x","x","x")));

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
        when(clienteRepository.findById(clienteId)).thenReturn(Optional.of(new Cliente()));
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
        LocalDate fs = fe; // misma fecha -> inválido

        Reserva req = new Reserva();
        req.setHabitacion(new Habitacion()); req.getHabitacion().setId(habitacionId);
        req.setCliente(new Cliente()); req.getCliente().setId(clienteId);
        req.setFechaEntrada(fe);
        req.setFechaSalida(fs);

        assertThrows(IllegalArgumentException.class, () -> reservaService.crearReserva(req));

        verifyNoInteractions(habitacionRepository, clienteRepository, reservaRepository);
    }
}
'@

Write-Utf8NoBomFile $path $content

### 2) Integration test: ClienteRepositoryIT.java
$path = Join-Path $base "src\test\java\com\luis\diaz\hotelreservationbackend\repository\ClienteRepositoryIT.java"
$content = @'
package com.luis.diaz.hotelreservationbackend.repository;

import com.luis.diaz.hotelreservationbackend.model.Cliente;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class ClienteRepositoryIT {

    @Autowired
    private ClienteRepository clienteRepository;

    @Test
    @DisplayName("Guardar y buscar cliente por email")
    void saveAndFindByEmail() {
        Cliente c = new Cliente();
        c.setNombre("Test");
        c.setEmail("test@example.com");
        c.setDocumento("DOC123");
        c.setTelefono("555-0000");

        Cliente saved = clienteRepository.save(c);
        assertNotNull(saved.getId());

        Optional<Cliente> byEmail = clienteRepository.findByEmail("test@example.com");
        assertTrue(byEmail.isPresent());
        assertEquals(saved.getId(), byEmail.get().getId());
    }
}
'@

Write-Utf8NoBomFile $path $content

### 3) Integration test: HabitacionRepositoryIT.java
$path = Join-Path $base "src\test\java\com\luis\diaz\hotelreservationbackend\repository\HabitacionRepositoryIT.java"
$content = @'
package com.luis.diaz.hotelreservationbackend.repository;

import com.luis.diaz.hotelreservationbackend.model.EstadoHabitacion;
import com.luis.diaz.hotelreservationbackend.model.Habitacion;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import jakarta.persistence.EntityManager;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class HabitacionRepositoryIT {

    @Autowired
    private HabitacionRepository habitacionRepository;

    @Autowired
    private EntityManager em;

    @Test
    @DisplayName("findByNumero retorna la habitación guardada")
    void findByNumero() {
        Habitacion h = new Habitacion();
        h.setNumero("201");
        h.setTipo(null);
        h.setPrecioBase(new BigDecimal("150.00"));
        h.setEstado(EstadoHabitacion.DISPONIBLE);

        habitacionRepository.save(h);

        Optional<Habitacion> found = habitacionRepository.findByNumero("201");
        assertTrue(found.isPresent());
        assertEquals("201", found.get().getNumero());
    }

    @Test
    @DisplayName("findByIdForUpdate aplica lock (se puede invocar dentro de transacción)")
    @Transactional
    void findByIdForUpdate_withLock() {
        Habitacion h = new Habitacion();
        h.setNumero("301");
        h.setTipo(null);
        h.setPrecioBase(new BigDecimal("200.00"));
        h.setEstado(EstadoHabitacion.DISPONIBLE);
        h = habitacionRepository.save(h);

        // Llamada que utiliza @Lock(LockModeType.PESSIMISTIC_WRITE)
        Optional<Habitacion> locked = habitacionRepository.findByIdForUpdate(h.getId());
        assertTrue(locked.isPresent());
        assertEquals(h.getId(), locked.get().getId());

        // modificar y flush para verificar que la entidad está gestionada
        locked.get().setEstado(EstadoHabitacion.MANTENIMIENTO);
        em.flush();

        Habitacion reloaded = habitacionRepository.findById(h.getId()).orElseThrow();
        assertEquals(EstadoHabitacion.MANTENIMIENTO, reloaded.getEstado());
    }
}
'@

Write-Utf8NoBomFile $path $content

### 4) Integration test: ReservaRepositoryIT.java
$path = Join-Path $base "src\test\java\com\luis\diaz\hotelreservationbackend\repository\ReservaRepositoryIT.java"
$content = @'
package com.luis.diaz.hotelreservationbackend.repository;

import com.luis.diaz.hotelreservationbackend.model.Cliente;
import com.luis.diaz.hotelreservationbackend.model.Habitacion;
import com.luis.diaz.hotelreservationbackend.model.Reserva;
import com.luis.diaz.hotelreservationbackend.model.TipoHabitacion;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
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
        h.setEstado(null);
        h = habitacionRepository.save(h);

        Cliente c = new Cliente(null, "Cliente", "c@example.com", "DOC", "555");
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
        r.setEstadoReserva(null);
        reservaRepository.save(r);

        // Caso solapado (entra antes de salida)
        boolean overlapping1 = reservaRepository.existsOverlappingReservation(h, a.plusDays(1), b.plusDays(1));
        assertTrue(overlapping1);

        // Caso no solapado (completamente antes)
        boolean overlapping2 = reservaRepository.existsOverlappingReservation(h, a.minusDays(5), a.minusDays(1));
        assertFalse(overlapping2);
    }
}
'@

Write-Utf8NoBomFile $path $content

### 5) Integration test: ReservaServiceIntegrationTest.java
$path = Join-Path $base "src\test\java\com\luis\diaz\hotelreservationbackend\service\ReservaServiceIntegrationTest.java"
$content = @'
package com.luis.diaz.hotelreservationbackend.service;

import com.luis.diaz.hotelreservationbackend.exception.ClienteNotFoundException;
import com.luis.diaz.hotelreservationbackend.exception.HabitacionNoDisponibleException;
import com.luis.diaz.hotelreservationbackend.model.EstadoHabitacion;
import com.luis.diaz.hotelreservationbackend.model.Habitacion;
import com.luis.diaz.hotelreservationbackend.model.Cliente;
import com.luis.diaz.hotelreservationbackend.model.Reserva;
import com.luis.diaz.hotelreservationbackend.repository.HabitacionRepository;
import com.luis.diaz.hotelreservationbackend.repository.ClienteRepository;
import com.luis.diaz.hotelreservationbackend.repository.ReservaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class ReservaServiceIntegrationTest {

    @Autowired
    private ReservaService reservaService;

    @Autowired
    private HabitacionRepository habitacionRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @MockBean
    private ReservaRepository reservaRepository; // mockeamos para forzar fallos en puntos específicos

    @Test
    @DisplayName("Si falla al persistir reserva, la transacción se revierte y la habitación permanece DISPONIBLE")
    void whenSaveThrows_thenTransactionRolledBack() {
        // Preparar datos reales en BD (H2)
        Habitacion habitacion = new Habitacion();
        habitacion.setNumero("501");
        habitacion.setPrecioBase(new BigDecimal("100.00"));
        habitacion.setEstado(EstadoHabitacion.DISPONIBLE);
        habitacion = habitacionRepository.save(habitacion);

        Cliente cliente = new Cliente(null, "Rollback Test", "rb@example.com", "DOCRB", "555");
        cliente = clienteRepository.save(cliente);

        LocalDate fe = LocalDate.now().plusDays(1);
        LocalDate fs = fe.plusDays(2);

        // Mocks: obtener habitacion con lock y cliente ok, y no solapamiento
        when(habitacionRepository.findByIdForUpdate(habitacion.getId())).thenReturn(Optional.of(habitacion));
        when(clienteRepository.findById(cliente.getId())).thenReturn(Optional.of(cliente));
        when(reservaRepository.existsOverlappingReservation(any(Habitacion.class), any(LocalDate.class), any(LocalDate.class))).thenReturn(false);

        // Forzar que save lanza excepción simulando fallo DB
        when(reservaRepository.save(any())).thenThrow(new RuntimeException("Simulated DB failure"));

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
        habitacion.setPrecioBase(new BigDecimal("90.00"));
        habitacion.setEstado(EstadoHabitacion.DISPONIBLE);
        habitacion = habitacionRepository.save(habitacion);

        Long nonExistentClienteId = 99999L;

        // Mock habitacion locked
        when(habitacionRepository.findByIdForUpdate(habitacion.getId())).thenReturn(Optional.of(habitacion));
        // clienteRepository devuelve empty
        when(clienteRepository.findById(nonExistentClienteId)).thenReturn(Optional.empty());

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
'@

Write-Utf8NoBomFile $path $content

Write-Host "`nAll test files created. You can now run: ./mvnw.cmd -U test"
