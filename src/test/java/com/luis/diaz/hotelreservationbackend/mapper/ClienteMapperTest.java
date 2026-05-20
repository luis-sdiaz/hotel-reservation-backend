package com.luis.diaz.hotelreservationbackend.mapper;

import com.luis.diaz.hotelreservationbackend.dto.ClienteResponseDTO;
import com.luis.diaz.hotelreservationbackend.model.Cliente;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ClienteMapperTest {

    @Test
    void toResponse_cliente_mapeaCamposCorrectamente() {
        Cliente cliente = new Cliente();
        cliente.setId(1L);
        cliente.setNombre("Ana Perez");
        cliente.setEmail("ana@example.com");
        cliente.setDocumento("DOC1234");
        cliente.setTelefono("555");
        cliente.setActivo(true);

        ClienteResponseDTO dto = ClienteMapper.toResponse(cliente);

        assertEquals(1L, dto.getId());
        assertEquals("Ana Perez", dto.getNombre());
        assertEquals("ana@example.com", dto.getEmail());
        assertEquals("DOC1234", dto.getDocumento());
        assertEquals("555", dto.getTelefono());
        assertTrue(dto.isActivo());
    }
}
