package com.luis.diaz.hotelreservationbackend.service;

import com.luis.diaz.hotelreservationbackend.dto.HabitacionRequestDTO;
import com.luis.diaz.hotelreservationbackend.model.EstadoHabitacion;
import com.luis.diaz.hotelreservationbackend.model.Habitacion;
import com.luis.diaz.hotelreservationbackend.model.TipoHabitacion;
import com.luis.diaz.hotelreservationbackend.repository.HabitacionRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HabitacionServiceTest {

    @Mock
    private HabitacionRepository habitacionRepository;

    @InjectMocks
    private HabitacionService habitacionService;

    @Test
    void crear_deberiaMarcarActivoYEstadoDefault() {
        HabitacionRequestDTO dto = new HabitacionRequestDTO();
        dto.setNumero("101");
        dto.setTipo(TipoHabitacion.SIMPLE);
        dto.setPrecioBase(new BigDecimal("120.00"));
        dto.setEstado(null);

        when(habitacionRepository.save(any(Habitacion.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Habitacion creada = habitacionService.crear(dto);

        assertTrue(Boolean.TRUE.equals(creada.getActivo()));
        assertEquals(EstadoHabitacion.DISPONIBLE, creada.getEstado());
        verify(habitacionRepository, times(1)).save(any(Habitacion.class));
    }

    @Test
    void obtenerPorId_noExiste_lanzaExcepcion() {
        when(habitacionRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> habitacionService.obtenerPorId(99L));
    }

    @Test
    void buscarDisponibles_habitacionDisponible_deberiaAparecer() {
        LocalDate entrada = LocalDate.now().plusDays(1);
        LocalDate salida = entrada.plusDays(2);
        Habitacion habitacion = habitacion(1L, true, EstadoHabitacion.DISPONIBLE);
        when(habitacionRepository.findDisponiblesPorFechas(entrada, salida, PageRequest.of(0, 10)))
                .thenReturn(new PageImpl<>(List.of(habitacion)));

        Page<Habitacion> disponibles = habitacionService.listarDisponiblesPorFechas(entrada, salida, PageRequest.of(0, 10));

        assertEquals(1, disponibles.getTotalElements());
        assertEquals(1L, disponibles.getContent().get(0).getId());
    }

    @Test
    void buscarDisponibles_habitacionInactiva_noDeberiaAparecer() {
        LocalDate entrada = LocalDate.now().plusDays(1);
        LocalDate salida = entrada.plusDays(2);
        when(habitacionRepository.findDisponiblesPorFechas(entrada, salida, PageRequest.of(0, 10)))
                .thenReturn(Page.empty());

        Page<Habitacion> disponibles = habitacionService.listarDisponiblesPorFechas(entrada, salida, PageRequest.of(0, 10));

        assertTrue(disponibles.isEmpty());
    }

    @Test
    void buscarDisponibles_habitacionEnMantenimiento_noDeberiaAparecer() {
        LocalDate entrada = LocalDate.now().plusDays(1);
        LocalDate salida = entrada.plusDays(2);
        when(habitacionRepository.findDisponiblesPorFechas(entrada, salida, PageRequest.of(0, 10)))
                .thenReturn(Page.empty());

        Page<Habitacion> disponibles = habitacionService.listarDisponiblesPorFechas(entrada, salida, PageRequest.of(0, 10));

        assertTrue(disponibles.isEmpty());
    }

    private Habitacion habitacion(Long id, boolean activo, EstadoHabitacion estado) {
        Habitacion habitacion = new Habitacion();
        habitacion.setId(id);
        habitacion.setNumero("10" + id);
        habitacion.setTipo(TipoHabitacion.SIMPLE);
        habitacion.setPrecioBase(new BigDecimal("100.00"));
        habitacion.setActivo(activo);
        habitacion.setEstado(estado);
        return habitacion;
    }
}

