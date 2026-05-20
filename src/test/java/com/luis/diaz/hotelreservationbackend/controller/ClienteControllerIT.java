package com.luis.diaz.hotelreservationbackend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.luis.diaz.hotelreservationbackend.dto.ClienteRequestDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ClienteControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void crearCliente_valido_retorna201() throws Exception {
        ClienteRequestDTO dto = new ClienteRequestDTO();
        dto.setNombre("Juan Perez");
        dto.setEmail("juan.perez@example.com");
        dto.setDocumento("DOC123");
        dto.setTelefono("555");

        mockMvc.perform(post("/api/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.email").value("juan.perez@example.com"))
                .andExpect(jsonPath("$.activo").value(true));
    }

    @Test
    void crearCliente_invalido_retorna400() throws Exception {
        ClienteRequestDTO dto = new ClienteRequestDTO();
        dto.setNombre(" ");
        dto.setEmail("no-email");
        dto.setDocumento("");

        mockMvc.perform(post("/api/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").exists());
    }
}

