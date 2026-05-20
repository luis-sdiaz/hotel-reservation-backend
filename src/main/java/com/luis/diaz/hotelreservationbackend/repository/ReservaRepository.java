package com.luis.diaz.hotelreservationbackend.repository;

import com.luis.diaz.hotelreservationbackend.model.EstadoReserva;
import com.luis.diaz.hotelreservationbackend.model.Habitacion;
import com.luis.diaz.hotelreservationbackend.model.Reserva;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Optional;

public interface ReservaRepository extends JpaRepository<Reserva, Long> {

    @Query("""
            SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END
            FROM Reserva r
            WHERE r.habitacion = :habitacion
              AND r.estadoReserva IN (
                    com.luis.diaz.hotelreservationbackend.model.EstadoReserva.PENDIENTE,
                    com.luis.diaz.hotelreservationbackend.model.EstadoReserva.CONFIRMADA
              )
              AND r.fechaEntrada < :fechaSalida
              AND r.fechaSalida > :fechaEntrada
            """)
    boolean existsOverlappingActiveReservation(
            @Param("habitacion") Habitacion habitacion,
            @Param("fechaEntrada") LocalDate fechaEntrada,
            @Param("fechaSalida") LocalDate fechaSalida
    );

    @Query("""
            SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END
            FROM Reserva r
            WHERE r.habitacion = :habitacion
              AND r.estadoReserva IN (
                    com.luis.diaz.hotelreservationbackend.model.EstadoReserva.PENDIENTE,
                    com.luis.diaz.hotelreservationbackend.model.EstadoReserva.CONFIRMADA
              )
              AND r.fechaEntrada < :fechaSalida
              AND r.fechaSalida > :fechaEntrada
            """)
    boolean existsOverlappingReservation(
            @Param("habitacion") Habitacion habitacion,
            @Param("fechaEntrada") LocalDate fechaEntrada,
            @Param("fechaSalida") LocalDate fechaSalida
    );

    @Query("""
            SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END
            FROM Reserva r
            WHERE r.habitacion = :habitacion
              AND r.id <> :reservaId
              AND r.estadoReserva IN (
                    com.luis.diaz.hotelreservationbackend.model.EstadoReserva.PENDIENTE,
                    com.luis.diaz.hotelreservationbackend.model.EstadoReserva.CONFIRMADA
              )
            """)
    boolean existsAnotherActiveReservationForHabitacion(
            @Param("habitacion") Habitacion habitacion,
            @Param("reservaId") Long reservaId
    );

    @EntityGraph(attributePaths = {"cliente", "habitacion", "serviciosAdicionales"})
    @Query(
            value = """
            SELECT r
            FROM Reserva r
            WHERE (:clienteId IS NULL OR r.cliente.id = :clienteId)
              AND (:habitacionId IS NULL OR r.habitacion.id = :habitacionId)
              AND (:estado IS NULL OR r.estadoReserva = :estado)
            """,
            countQuery = """
            SELECT COUNT(r)
            FROM Reserva r
            WHERE (:clienteId IS NULL OR r.cliente.id = :clienteId)
              AND (:habitacionId IS NULL OR r.habitacion.id = :habitacionId)
              AND (:estado IS NULL OR r.estadoReserva = :estado)
            """
    )
    Page<Reserva> findByFilters(
            @Param("clienteId") Long clienteId,
            @Param("habitacionId") Long habitacionId,
            @Param("estado") EstadoReserva estado,
            Pageable pageable
    );

    @EntityGraph(attributePaths = {"cliente", "habitacion", "serviciosAdicionales"})
    @Query("SELECT r FROM Reserva r WHERE r.id = :id")
    Optional<Reserva> findByIdWithDetails(@Param("id") Long id);
}
