package com.luis.diaz.hotelreservationbackend.service;

import com.luis.diaz.hotelreservationbackend.dto.ServicioAdicionalRequestDTO;
import com.luis.diaz.hotelreservationbackend.mapper.ServicioAdicionalMapper;
import com.luis.diaz.hotelreservationbackend.model.ServicioAdicional;
import com.luis.diaz.hotelreservationbackend.repository.ServicioAdicionalRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ServicioAdicionalService {

    private final ServicioAdicionalRepository servicioAdicionalRepository;

    public ServicioAdicionalService(ServicioAdicionalRepository servicioAdicionalRepository) {
        this.servicioAdicionalRepository = servicioAdicionalRepository;
    }

    @Transactional(readOnly = true)
    public List<ServicioAdicional> listarActivos() {
        return servicioAdicionalRepository.findByActivoTrueOrderByNombreAsc();
    }

    @Transactional(readOnly = true)
    public List<ServicioAdicional> listarTodos() {
        return servicioAdicionalRepository.findAll();
    }

    @Transactional(readOnly = true)
    public ServicioAdicional obtenerPorId(Long id) {
        return servicioAdicionalRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Servicio adicional no encontrado con id: " + id));
    }

    @Transactional
    public ServicioAdicional crear(ServicioAdicionalRequestDTO dto) {
        servicioAdicionalRepository.findByNombreIgnoreCase(dto.getNombre())
                .ifPresent(servicio -> {
                    throw new IllegalArgumentException(
                            "Ya existe un servicio adicional con el nombre: " + dto.getNombre()
                    );
                });

        ServicioAdicional entity = ServicioAdicionalMapper.toEntity(dto);
        return servicioAdicionalRepository.save(entity);
    }

    @Transactional
    public ServicioAdicional actualizar(Long id, ServicioAdicionalRequestDTO dto) {
        ServicioAdicional existente = obtenerPorId(id);

        servicioAdicionalRepository.findByNombreIgnoreCase(dto.getNombre())
                .filter(servicio -> !servicio.getId().equals(id))
                .ifPresent(servicio -> {
                    throw new IllegalArgumentException(
                            "Ya existe otro servicio adicional con el nombre: " + dto.getNombre()
                    );
                });

        existente.setNombre(dto.getNombre());
        existente.setDescripcion(dto.getDescripcion());
        existente.setPrecio(dto.getPrecio());

        return servicioAdicionalRepository.save(existente);
    }

    @Transactional
    public void desactivar(Long id) {
        ServicioAdicional servicio = obtenerPorId(id);
        servicio.setActivo(false);
        servicioAdicionalRepository.save(servicio);
    }

    @Transactional
    public void activar(Long id) {
        ServicioAdicional servicio = obtenerPorId(id);
        servicio.setActivo(true);
        servicioAdicionalRepository.save(servicio);
    }
}