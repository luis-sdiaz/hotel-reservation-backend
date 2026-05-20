package com.luis.diaz.hotelreservationbackend.controller;

import com.luis.diaz.hotelreservationbackend.dto.ClienteRequestDTO;
import com.luis.diaz.hotelreservationbackend.dto.ClienteResponseDTO;
import com.luis.diaz.hotelreservationbackend.mapper.ClienteMapper;
import com.luis.diaz.hotelreservationbackend.service.ClienteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@Validated
@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

    private final ClienteService clienteService;

    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @GetMapping
    @Operation(summary = "Listar clientes")
    public ResponseEntity<Page<ClienteResponseDTO>> listar(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Boolean activo) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ClienteResponseDTO> result = clienteService.listar(pageable, activo)
                .map(ClienteMapper::toResponse);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener cliente por id")
    public ResponseEntity<ClienteResponseDTO> obtener(@PathVariable @Positive Long id) {
        return ResponseEntity.ok(ClienteMapper.toResponse(clienteService.obtenerPorId(id)));
    }

    @PostMapping
    @Operation(summary = "Crear un cliente", responses = @ApiResponse(responseCode = "201", description = "Cliente creado"))
    public ResponseEntity<ClienteResponseDTO> crear(@Valid @org.springframework.web.bind.annotation.RequestBody ClienteRequestDTO c) {
        var saved = clienteService.crear(c);
        return ResponseEntity.created(URI.create("/api/clientes/" + saved.getId())).body(ClienteMapper.toResponse(saved));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar un cliente")
    public ResponseEntity<ClienteResponseDTO> actualizar(@PathVariable @Positive Long id,
                                                         @Valid @org.springframework.web.bind.annotation.RequestBody ClienteRequestDTO c) {
        var updated = clienteService.actualizar(id, c);
        return ResponseEntity.ok(ClienteMapper.toResponse(updated));
    }

    @PatchMapping("/{id}/deactivate")
    @Operation(summary = "Desactivar un cliente")
    public ResponseEntity<Void> desactivar(@PathVariable @Positive Long id) {
        clienteService.desactivar(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/activate")
    @Operation(summary = "Activar un cliente")
    public ResponseEntity<Void> activar(@PathVariable @Positive Long id) {
        clienteService.activar(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un cliente (eliminacion logica)")
    public ResponseEntity<Void> eliminar(@PathVariable @Positive Long id) {
        clienteService.desactivar(id);
        return ResponseEntity.noContent().build();
    }
}
