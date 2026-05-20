package com.luis.diaz.hotelreservationbackend.repository;

import com.luis.diaz.hotelreservationbackend.model.EstadoHabitacion;
import com.luis.diaz.hotelreservationbackend.model.TipoHabitacion;
import com.luis.diaz.hotelreservationbackend.model.Habitacion;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import jakarta.persistence.EntityManager;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
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
        h.setTipo(TipoHabitacion.SIMPLE); // CORREGIDO: no dejar null
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
        h.setTipo(TipoHabitacion.SIMPLE); // CORREGIDO: no dejar null
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
