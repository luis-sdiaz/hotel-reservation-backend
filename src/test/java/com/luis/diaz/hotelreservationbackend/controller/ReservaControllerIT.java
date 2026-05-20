package com.luis.diaz.hotelreservationbackend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.luis.diaz.hotelreservationbackend.dto.ReservaRequestDTO;
import com.luis.diaz.hotelreservationbackend.model.EstadoHabitacion;
import com.luis.diaz.hotelreservationbackend.model.Habitacion;
import com.luis.diaz.hotelreservationbackend.model.TipoHabitacion;
import com.luis.diaz.hotelreservationbackend.model.Cliente;
import com.luis.diaz.hotelreservationbackend.repository.ClienteRepository;
import com.luis.diaz.hotelreservationbackend.repository.HabitacionRepository;
import com.luis.diaz.hotelreservationbackend.repository.ReservaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class ReservaControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private HabitacionRepository habitacionRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private ReservaRepository reservaRepository;

    @BeforeEach
    void setUp() {
        reservaRepository.deleteAll();
        habitacionRepository.deleteAll();
        clienteRepository.deleteAll();
    }

    @Test
    void crearReserva_valida_deberiaRetornar201() throws Exception {
        Habitacion h = new Habitacion();
        h.setNumero("101");
        h.setTipo(TipoHabitacion.SIMPLE);
        h.setPrecioBase(new BigDecimal("100.00"));
        h.setEstado(EstadoHabitacion.DISPONIBLE);
        h = habitacionRepository.save(h);

        Cliente c = new Cliente();
        c.setNombre("Test Cliente");
        c.setEmail("test@example.com");
        c.setDocumento("DOC123");
        c.setTelefono("555");
        c = clienteRepository.save(c);

        ReservaRequestDTO req = new ReservaRequestDTO();
        req.setHabitacionId(h.getId());
        req.setClienteId(c.getId());
        req.setFechaEntrada(LocalDate.now().plusDays(1));
        req.setFechaSalida(LocalDate.now().plusDays(4));

        mockMvc.perform(post("/api/reservas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.habitacionId").value(h.getId().intValue()))
                .andExpect(jsonPath("$.clienteId").value(c.getId().intValue()))
                .andExpect(jsonPath("$.estadoReserva").value("CONFIRMADA"));
    }

    @Test
    void crearReserva_sinCliente_deberiaRetornar400() throws Exception {
        Habitacion h = new Habitacion();
        h.setNumero("201");
        h.setTipo(TipoHabitacion.DOUBLE);
        h.setPrecioBase(new BigDecimal("150.00"));
        h.setEstado(EstadoHabitacion.DISPONIBLE);
        h = habitacionRepository.save(h);

        // cliente no creado / clienteId ausente
        String payload = "{\"habitacionId\":" + h.getId() + ", \"fechaEntrada\": \"2026-06-01\", \"fechaSalida\": \"2026-06-03\"}";

        mockMvc.perform(post("/api/reservas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.errors").exists());
    }

    @Test
    void crearReserva_habitacionNoDisponible_deberiaRetornar409() throws Exception {
        Habitacion h = new Habitacion();
        h.setNumero("301");
        h.setTipo(TipoHabitacion.SUITE);
        h.setPrecioBase(new BigDecimal("300.00"));
        h.setEstado(EstadoHabitacion.MANTENIMIENTO);
        h = habitacionRepository.save(h);

        Cliente c = new Cliente();
        c.setNombre("Cliente2");
        c.setEmail("c2@example.com");
        c.setDocumento("DOC456");
        c.setTelefono("555");
        c = clienteRepository.save(c);

        ReservaRequestDTO req = new ReservaRequestDTO();
        req.setHabitacionId(h.getId());
        req.setClienteId(c.getId());
        req.setFechaEntrada(LocalDate.now().plusDays(1));
        req.setFechaSalida(LocalDate.now().plusDays(2));

        mockMvc.perform(post("/api/reservas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.message").exists());
    }
}
