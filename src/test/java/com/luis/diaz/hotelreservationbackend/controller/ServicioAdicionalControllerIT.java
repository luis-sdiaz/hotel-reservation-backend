package com.luis.diaz.hotelreservationbackend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.luis.diaz.hotelreservationbackend.dto.ServicioAdicionalRequestDTO;
import com.luis.diaz.hotelreservationbackend.model.ServicioAdicional;
import com.luis.diaz.hotelreservationbackend.repository.ServicioAdicionalRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ServicioAdicionalControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ServicioAdicionalRepository servicioAdicionalRepository;

    @BeforeEach
    void setUp() {
        servicioAdicionalRepository.deleteAll();
    }

    @Test
    void listarActivos_retornaSoloServiciosActivos() throws Exception {
        servicioAdicionalRepository.save(servicio("DESAYUNO_TEST", true));
        servicioAdicionalRepository.save(servicio("SPA_TEST", false));

        mockMvc.perform(get("/api/servicios-adicionales"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].nombre").value("DESAYUNO_TEST"))
                .andExpect(jsonPath("$[0].activo").value(true));
    }

    @Test
    void crearServicioAdicional_valido_retorna201() throws Exception {
        ServicioAdicionalRequestDTO dto = new ServicioAdicionalRequestDTO();
        dto.setNombre("PARQUEADERO_TEST");
        dto.setDescripcion("Parqueadero cubierto");
        dto.setPrecio(new BigDecimal("25000.00"));

        mockMvc.perform(post("/api/servicios-adicionales")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.nombre").value("PARQUEADERO_TEST"))
                .andExpect(jsonPath("$.activo").value(true));
    }

    @Test
    void desactivarServicioAdicional_existente_retorna204() throws Exception {
        ServicioAdicional servicio = servicioAdicionalRepository.save(servicio("LAVANDERIA_TEST", true));

        mockMvc.perform(patch("/api/servicios-adicionales/{id}/deactivate", servicio.getId()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/servicios-adicionales/{id}", servicio.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.activo").value(false));
    }

    private ServicioAdicional servicio(String nombre, boolean activo) {
        ServicioAdicional servicio = new ServicioAdicional();
        servicio.setNombre(nombre);
        servicio.setDescripcion("Servicio de prueba");
        servicio.setPrecio(new BigDecimal("10000.00"));
        servicio.setActivo(activo);
        return servicio;
    }
}
