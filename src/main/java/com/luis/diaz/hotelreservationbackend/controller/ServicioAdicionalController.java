package com.luis.diaz.hotelreservationbackend.controller;

import com.luis.diaz.hotelreservationbackend.dto.ServicioAdicionalRequestDTO;
import com.luis.diaz.hotelreservationbackend.dto.ServicioAdicionalResponseDTO;
import com.luis.diaz.hotelreservationbackend.mapper.ServicioAdicionalMapper;
import com.luis.diaz.hotelreservationbackend.service.ServicioAdicionalService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@Validated
@RestController
@RequestMapping("/api/servicios-adicionales")
public class ServicioAdicionalController {

    private final ServicioAdicionalService servicioAdicionalService;

    public ServicioAdicionalController(ServicioAdicionalService servicioAdicionalService) {
        this.servicioAdicionalService = servicioAdicionalService;
    }

    @GetMapping
    @Operation(summary = "Listar servicios adicionales activos")
    public ResponseEntity<List<ServicioAdicionalResponseDTO>> listarActivos() {
        List<ServicioAdicionalResponseDTO> response = servicioAdicionalService.listarActivos()
                .stream()
                .map(ServicioAdicionalMapper::toResponse)
                .toList();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/all")
    @Operation(summary = "Listar todos los servicios adicionales")
    public ResponseEntity<List<ServicioAdicionalResponseDTO>> listarTodos() {
        List<ServicioAdicionalResponseDTO> response = servicioAdicionalService.listarTodos()
                .stream()
                .map(ServicioAdicionalMapper::toResponse)
                .toList();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener servicio adicional por id")
    public ResponseEntity<ServicioAdicionalResponseDTO> obtener(@PathVariable @Positive Long id) {
        return ResponseEntity.ok(
                ServicioAdicionalMapper.toResponse(servicioAdicionalService.obtenerPorId(id))
        );
    }

    @PostMapping
    @Operation(summary = "Crear servicio adicional")
    public ResponseEntity<ServicioAdicionalResponseDTO> crear(
            @Valid @org.springframework.web.bind.annotation.RequestBody ServicioAdicionalRequestDTO dto
    ) {
        var saved = servicioAdicionalService.crear(dto);

        return ResponseEntity
                .created(URI.create("/api/servicios-adicionales/" + saved.getId()))
                .body(ServicioAdicionalMapper.toResponse(saved));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar servicio adicional")
    public ResponseEntity<ServicioAdicionalResponseDTO> actualizar(
            @PathVariable @Positive Long id,
            @Valid @org.springframework.web.bind.annotation.RequestBody ServicioAdicionalRequestDTO dto
    ) {
        var updated = servicioAdicionalService.actualizar(id, dto);

        return ResponseEntity.ok(ServicioAdicionalMapper.toResponse(updated));
    }

    @PatchMapping("/{id}/deactivate")
    @Operation(summary = "Desactivar servicio adicional")
    public ResponseEntity<Void> desactivar(@PathVariable @Positive Long id) {
        servicioAdicionalService.desactivar(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/activate")
    @Operation(summary = "Activar servicio adicional")
    public ResponseEntity<Void> activar(@PathVariable @Positive Long id) {
        servicioAdicionalService.activar(id);
        return ResponseEntity.noContent().build();
    }
}