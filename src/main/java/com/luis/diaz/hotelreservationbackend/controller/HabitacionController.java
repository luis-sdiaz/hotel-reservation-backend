package com.luis.diaz.hotelreservationbackend.controller;

import com.luis.diaz.hotelreservationbackend.dto.HabitacionRequestDTO;
import com.luis.diaz.hotelreservationbackend.dto.HabitacionResponseDTO;
import com.luis.diaz.hotelreservationbackend.mapper.HabitacionMapper;
import com.luis.diaz.hotelreservationbackend.model.EstadoHabitacion;
import com.luis.diaz.hotelreservationbackend.model.TipoHabitacion;
import com.luis.diaz.hotelreservationbackend.service.HabitacionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDate;

@Validated
@RestController
@RequestMapping("/api/habitaciones")
public class HabitacionController {

    private final HabitacionService habitacionService;

    public HabitacionController(HabitacionService habitacionService) {
        this.habitacionService = habitacionService;
    }

    @GetMapping
    @Operation(summary = "Listar habitaciones")
    public ResponseEntity<Page<HabitacionResponseDTO>> listar(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) EstadoHabitacion estado,
            @RequestParam(required = false) TipoHabitacion tipo,
            @RequestParam(required = false) Boolean activo
    ) {
        Pageable pageable = PageRequest.of(page, size);

        Page<HabitacionResponseDTO> result = habitacionService
                .listar(pageable, estado, tipo, activo)
                .map(HabitacionMapper::toResponse);

        return ResponseEntity.ok(result);
    }

    @GetMapping("/disponibles")
    @Operation(summary = "Consultar habitaciones disponibles por rango de fechas")
    public ResponseEntity<Page<HabitacionResponseDTO>> listarDisponiblesPorFechas(
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate fechaEntrada,

            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate fechaSalida,

            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);

        Page<HabitacionResponseDTO> result = habitacionService
                .listarDisponiblesPorFechas(fechaEntrada, fechaSalida, pageable)
                .map(HabitacionMapper::toResponse);

        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener habitacion por id")
    public ResponseEntity<HabitacionResponseDTO> obtener(@PathVariable @Positive Long id) {
        return ResponseEntity.ok(
                HabitacionMapper.toResponse(habitacionService.obtenerPorId(id))
        );
    }

    @PostMapping
    @Operation(
            summary = "Crear una nueva habitacion",
            responses = @ApiResponse(responseCode = "201", description = "Habitacion creada")
    )
    public ResponseEntity<HabitacionResponseDTO> crear(
            @Valid @org.springframework.web.bind.annotation.RequestBody HabitacionRequestDTO request
    ) {
        var saved = habitacionService.crear(request);

        return ResponseEntity
                .created(URI.create("/api/habitaciones/" + saved.getId()))
                .body(HabitacionMapper.toResponse(saved));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar una habitacion existente")
    public ResponseEntity<HabitacionResponseDTO> actualizar(
            @PathVariable @Positive Long id,
            @Valid @org.springframework.web.bind.annotation.RequestBody HabitacionRequestDTO request
    ) {
        var updated = habitacionService.actualizar(id, request);

        return ResponseEntity.ok(HabitacionMapper.toResponse(updated));
    }

    @PatchMapping("/{id}/deactivate")
    @Operation(summary = "Desactivar una habitacion")
    public ResponseEntity<Void> desactivar(@PathVariable @Positive Long id) {
        habitacionService.desactivar(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/activate")
    @Operation(summary = "Activar una habitacion")
    public ResponseEntity<Void> activar(@PathVariable @Positive Long id) {
        habitacionService.activar(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar una habitacion mediante eliminacion logica")
    public ResponseEntity<Void> eliminar(@PathVariable @Positive Long id) {
        habitacionService.desactivar(id);
        return ResponseEntity.noContent().build();
    }
}
