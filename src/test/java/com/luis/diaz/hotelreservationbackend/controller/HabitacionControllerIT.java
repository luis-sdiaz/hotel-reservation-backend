package com.luis.diaz.hotelreservationbackend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.luis.diaz.hotelreservationbackend.dto.HabitacionRequestDTO;
import com.luis.diaz.hotelreservationbackend.model.EstadoHabitacion;
import com.luis.diaz.hotelreservationbackend.model.Habitacion;
import com.luis.diaz.hotelreservationbackend.model.TipoHabitacion;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class HabitacionControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private HabitacionRepository habitacionRepository;

    @Autowired
    private ReservaRepository reservaRepository;

    @BeforeEach
    void setUp() {
        reservaRepository.deleteAll();
        habitacionRepository.deleteAll();
    }

    @Test
    void crearHabitacion_valida_retorna201() throws Exception {
        HabitacionRequestDTO dto = new HabitacionRequestDTO();
        dto.setNumero("101");
        dto.setTipo(TipoHabitacion.SIMPLE);
        dto.setPrecioBase(new BigDecimal("120.00"));

        mockMvc.perform(post("/api/habitaciones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.numero").value("101"))
                .andExpect(jsonPath("$.activo").value(true));
    }

    @Test
    void crearHabitacion_invalida_retorna400() throws Exception {
        HabitacionRequestDTO dto = new HabitacionRequestDTO();
        dto.setNumero("");
        dto.setTipo(TipoHabitacion.SIMPLE);
        dto.setPrecioBase(new BigDecimal("-10.00"));

        mockMvc.perform(post("/api/habitaciones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").exists());
    }

    @Test
    void listarDisponibles_fechasValidas_retorna200() throws Exception {
        Habitacion habitacion = new Habitacion();
        habitacion.setNumero("401");
        habitacion.setTipo(TipoHabitacion.SIMPLE);
        habitacion.setPrecioBase(new BigDecimal("120.00"));
        habitacion.setEstado(EstadoHabitacion.DISPONIBLE);
        habitacion.setActivo(true);
        habitacionRepository.save(habitacion);

        mockMvc.perform(get("/api/habitaciones/disponibles")
                        .param("fechaEntrada", "2026-06-01")
                        .param("fechaSalida", "2026-06-03"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].numero").value("401"));
    }

    @Test
    void listarDisponibles_fechaSalidaAntesDeEntrada_retorna400() throws Exception {
        mockMvc.perform(get("/api/habitaciones/disponibles")
                        .param("fechaEntrada", "2026-06-03")
                        .param("fechaSalida", "2026-06-01"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }
}

