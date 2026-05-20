package com.luis.diaz.hotelreservationbackend.repository;

import com.luis.diaz.hotelreservationbackend.model.Cliente;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
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