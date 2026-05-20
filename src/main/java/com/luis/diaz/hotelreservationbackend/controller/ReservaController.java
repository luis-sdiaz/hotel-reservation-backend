package com.luis.diaz.hotelreservationbackend.controller;

import com.luis.diaz.hotelreservationbackend.dto.ReservaRequestDTO;
import com.luis.diaz.hotelreservationbackend.dto.ReservaResponseDTO;
import com.luis.diaz.hotelreservationbackend.dto.ResumenPagoResponseDTO;
import com.luis.diaz.hotelreservationbackend.mapper.ReservaMapper;
import com.luis.diaz.hotelreservationbackend.model.EstadoReserva;
import com.luis.diaz.hotelreservationbackend.service.ReservaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
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
@RequestMapping("/api/reservas")
public class ReservaController {

    private final ReservaService reservaService;

    public ReservaController(ReservaService reservaService) {
        this.reservaService = reservaService;
    }

    @GetMapping
    @Operation(
            summary = "Listar reservas",
            description = "Devuelve la lista de reservas existentes con filtros opcionales"
    )
    public ResponseEntity<Page<ReservaResponseDTO>> listar(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Long clienteId,
            @RequestParam(required = false) Long habitacionId,
            @RequestParam(required = false) EstadoReserva estado
    ) {
        Pageable pageable = PageRequest.of(page, size);

        Page<ReservaResponseDTO> lista = reservaService.listar(pageable, clienteId, habitacionId, estado);

        return ResponseEntity.ok(lista);
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Obtener reserva",
            description = "Obtiene una reserva por su id",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Reserva encontrada",
                            content = @Content(schema = @Schema(implementation = ReservaResponseDTO.class))
                    ),
                    @ApiResponse(responseCode = "404", description = "Reserva no encontrada")
            }
    )
    public ResponseEntity<ReservaResponseDTO> obtener(@PathVariable @Positive Long id) {
        return ResponseEntity.ok(
                ReservaMapper.toResponse(reservaService.obtenerPorIdConDetalles(id))
        );
    }

    @PostMapping
    @Operation(
            summary = "Crear reserva",
            description = "Crea una reserva si la habitacion esta disponible en el rango de fechas solicitado",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos de la reserva a crear",
                    required = true,
                    content = @Content(schema = @Schema(implementation = ReservaRequestDTO.class))
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Reserva creada",
                            content = @Content(schema = @Schema(implementation = ReservaResponseDTO.class))
                    ),
                    @ApiResponse(responseCode = "409", description = "Habitacion no disponible")
            }
    )
    public ResponseEntity<ReservaResponseDTO> crear(
            @Valid @org.springframework.web.bind.annotation.RequestBody ReservaRequestDTO dto
    ) {
        var saved = reservaService.crearReserva(ReservaMapper.toEntity(dto));

        return ResponseEntity
                .created(URI.create("/api/reservas/" + saved.getId()))
                .body(ReservaMapper.toResponse(saved));
    }

    @PatchMapping("/{id}/cancelar")
    @Operation(
            summary = "Cancelar reserva",
            description = "Marca una reserva como CANCELADA y libera la habitacion si corresponde"
    )
    public ResponseEntity<ReservaResponseDTO> cancelar(@PathVariable @Positive Long id) {
        var updated = reservaService.cancelarReserva(id);
        return ResponseEntity.ok(ReservaMapper.toResponse(updated));
    }

    @PatchMapping("/{id}/finalizar")
    @Operation(
            summary = "Finalizar reserva",
            description = "Marca una reserva como FINALIZADA y libera la habitacion si corresponde"
    )
    public ResponseEntity<ReservaResponseDTO> finalizar(@PathVariable @Positive Long id) {
        var updated = reservaService.finalizarReserva(id);
        return ResponseEntity.ok(ReservaMapper.toResponse(updated));
    }

    @GetMapping("/{id}/resumen-pago")
    @Operation(
            summary = "Generar resumen de pago",
            description = "Devuelve el resumen de pago o factura de una reserva"
    )
    public ResponseEntity<ResumenPagoResponseDTO> resumenPago(@PathVariable @Positive Long id) {
        return ResponseEntity.ok(reservaService.generarResumenPago(id));
    }
}
