package com.luis.diaz.hotelreservationbackend.service;

import com.luis.diaz.hotelreservationbackend.dto.HabitacionRequestDTO;
import com.luis.diaz.hotelreservationbackend.mapper.HabitacionMapper;
import com.luis.diaz.hotelreservationbackend.model.EstadoHabitacion;
import com.luis.diaz.hotelreservationbackend.model.Habitacion;
import com.luis.diaz.hotelreservationbackend.model.TipoHabitacion;
import com.luis.diaz.hotelreservationbackend.repository.HabitacionRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
public class HabitacionService {

    private final HabitacionRepository habitacionRepository;

    public HabitacionService(HabitacionRepository habitacionRepository) {
        this.habitacionRepository = habitacionRepository;
    }

    @Transactional(readOnly = true)
    public Page<Habitacion> listar(
            Pageable pageable,
            EstadoHabitacion estado,
            TipoHabitacion tipo,
            Boolean activo
    ) {
        return habitacionRepository.findByFilters(estado, tipo, activo, pageable);
    }

    @Transactional(readOnly = true)
    public Page<Habitacion> listarDisponiblesPorFechas(
            LocalDate fechaEntrada,
            LocalDate fechaSalida,
            Pageable pageable
    ) {
        validarRangoFechas(fechaEntrada, fechaSalida);
        return habitacionRepository.findDisponiblesPorFechas(fechaEntrada, fechaSalida, pageable);
    }

    @Transactional(readOnly = true)
    public Habitacion obtenerPorId(Long id) {
        return habitacionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Habitacion no encontrada con id: " + id));
    }

    @Transactional
    public Habitacion crear(HabitacionRequestDTO dto) {
        validarNumeroUnico(dto.getNumero(), null);

        Habitacion entity = HabitacionMapper.toEntity(dto);
        entity.setActivo(true);
        if (entity.getEstado() == null) {
            entity.setEstado(EstadoHabitacion.DISPONIBLE);
        }

        return habitacionRepository.save(entity);
    }

    @Transactional
    public Habitacion actualizar(Long id, HabitacionRequestDTO dto) {
        Habitacion existente = obtenerPorId(id);

        validarNumeroUnico(dto.getNumero(), id);

        existente.setNumero(dto.getNumero());
        existente.setTipo(dto.getTipo());
        existente.setPrecioBase(dto.getPrecioBase());

        if (dto.getEstado() != null) {
            existente.setEstado(dto.getEstado());
        }

        return habitacionRepository.save(existente);
    }

    @Transactional
    public void desactivar(Long id) {
        Habitacion habitacion = obtenerPorId(id);
        habitacion.setActivo(false);
        habitacionRepository.save(habitacion);
    }

    @Transactional
    public void activar(Long id) {
        Habitacion habitacion = obtenerPorId(id);
        habitacion.setActivo(true);
        habitacionRepository.save(habitacion);
    }

    private void validarRangoFechas(LocalDate fechaEntrada, LocalDate fechaSalida) {
        if (fechaEntrada == null || fechaSalida == null) {
            throw new IllegalArgumentException("Las fechas de entrada y salida son obligatorias");
        }

        if (!fechaSalida.isAfter(fechaEntrada)) {
            throw new IllegalArgumentException("La fecha de salida debe ser posterior a la fecha de entrada");
        }
    }

    private void validarNumeroUnico(String numero, Long idActual) {
        habitacionRepository.findByNumero(numero)
                .filter(habitacion -> idActual == null || !habitacion.getId().equals(idActual))
                .ifPresent(habitacion -> {
                    throw new IllegalArgumentException("Ya existe una habitacion registrada con el numero: " + numero);
                });
    }
}
