package com.luis.diaz.hotelreservationbackend.service;

import com.luis.diaz.hotelreservationbackend.dto.ClienteRequestDTO;
import com.luis.diaz.hotelreservationbackend.mapper.ClienteMapper;
import com.luis.diaz.hotelreservationbackend.model.Cliente;
import com.luis.diaz.hotelreservationbackend.repository.ClienteRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ClienteService {

    private final ClienteRepository clienteRepository;

    @Transactional(readOnly = true)
    public Page<Cliente> listar(Pageable pageable, Boolean activo) {
        if (activo == null) {
            return clienteRepository.findAll(pageable);
        }
        return clienteRepository.findByActivo(activo, pageable);
    }

    @Transactional(readOnly = true)
    public Cliente obtenerPorId(Long id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cliente no encontrado con id: " + id));
    }

    @Transactional
    public Cliente crear(ClienteRequestDTO dto) {
        Cliente entity = ClienteMapper.toEntity(dto);
        entity.setActivo(true);
        return clienteRepository.save(entity);
    }

    @Transactional
    public Cliente actualizar(Long id, ClienteRequestDTO dto) {
        Cliente existente = obtenerPorId(id);
        existente.setNombre(dto.getNombre());
        existente.setEmail(dto.getEmail());
        existente.setDocumento(dto.getDocumento());
        existente.setTelefono(dto.getTelefono());
        return clienteRepository.save(existente);
    }

    @Transactional
    public void desactivar(Long id) {
        Cliente cliente = obtenerPorId(id);
        cliente.setActivo(false);
        clienteRepository.save(cliente);
    }

    @Transactional
    public void activar(Long id) {
        Cliente cliente = obtenerPorId(id);
        cliente.setActivo(true);
        clienteRepository.save(cliente);
    }
}

