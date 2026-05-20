package com.luis.diaz.hotelreservationbackend.repository;

import com.luis.diaz.hotelreservationbackend.model.EstadoHabitacion;
import com.luis.diaz.hotelreservationbackend.model.Habitacion;
import com.luis.diaz.hotelreservationbackend.model.TipoHabitacion;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Optional;

public interface HabitacionRepository extends JpaRepository<Habitacion, Long> {

    Optional<Habitacion> findByNumero(String numero);

    @Query("""
            SELECT h
            FROM Habitacion h
            WHERE (:estado IS NULL OR h.estado = :estado)
              AND (:tipo IS NULL OR h.tipo = :tipo)
              AND (:activo IS NULL OR h.activo = :activo)
            """)
    Page<Habitacion> findByFilters(
            @Param("estado") EstadoHabitacion estado,
            @Param("tipo") TipoHabitacion tipo,
            @Param("activo") Boolean activo,
            Pageable pageable
    );

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT h FROM Habitacion h WHERE h.id = :id")
    Optional<Habitacion> findByIdForUpdate(@Param("id") Long id);

    @Query("""
            SELECT h
            FROM Habitacion h
            WHERE h.activo = true
              AND h.estado <> com.luis.diaz.hotelreservationbackend.model.EstadoHabitacion.MANTENIMIENTO
              AND NOT EXISTS (
                    SELECT r
                    FROM Reserva r
                    WHERE r.habitacion = h
                      AND r.estadoReserva IN (
                            com.luis.diaz.hotelreservationbackend.model.EstadoReserva.PENDIENTE,
                            com.luis.diaz.hotelreservationbackend.model.EstadoReserva.CONFIRMADA
                      )
                      AND r.fechaEntrada < :fechaSalida
                      AND r.fechaSalida > :fechaEntrada
              )
            """)
    Page<Habitacion> findDisponiblesPorFechas(
            @Param("fechaEntrada") LocalDate fechaEntrada,
            @Param("fechaSalida") LocalDate fechaSalida,
            Pageable pageable
    );
}