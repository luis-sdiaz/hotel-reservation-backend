package com.luis.diaz.hotelreservationbackend.service;

import com.luis.diaz.hotelreservationbackend.dto.ClienteRequestDTO;
import com.luis.diaz.hotelreservationbackend.model.Cliente;
import com.luis.diaz.hotelreservationbackend.repository.ClienteRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClienteServiceTest {

    @Mock
    private ClienteRepository clienteRepository;

    @InjectMocks
    private ClienteService clienteService;

    @Test
    void crear_deberiaMarcarActivo() {
        ClienteRequestDTO dto = new ClienteRequestDTO();
        dto.setNombre("Juan Perez");
        dto.setEmail("juan@example.com");
        dto.setDocumento("DOC123");
        dto.setTelefono("555");

        when(clienteRepository.save(any(Cliente.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Cliente creado = clienteService.crear(dto);

        assertTrue(Boolean.TRUE.equals(creado.getActivo()));
        assertEquals("Juan Perez", creado.getNombre());
        verify(clienteRepository, times(1)).save(any(Cliente.class));
    }

    @Test
    void obtenerPorId_noExiste_lanzaExcepcion() {
        when(clienteRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> clienteService.obtenerPorId(99L));
    }

    @Test
    void desactivar_cambiaActivoAFalse() {
        Cliente existente = new Cliente();
        existente.setId(1L);
        existente.setActivo(true);

        when(clienteRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(clienteRepository.save(any(Cliente.class))).thenAnswer(invocation -> invocation.getArgument(0));

        clienteService.desactivar(1L);

        assertFalse(Boolean.TRUE.equals(existente.getActivo()));
        verify(clienteRepository).save(existente);
    }
}

