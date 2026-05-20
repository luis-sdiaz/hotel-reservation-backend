package com.luis.diaz.hotelreservationbackend.repository;

import com.luis.diaz.hotelreservationbackend.model.ServicioAdicional;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ServicioAdicionalRepository extends JpaRepository<ServicioAdicional, Long> {

    Optional<ServicioAdicional> findByNombreIgnoreCase(String nombre);

    List<ServicioAdicional> findByActivoTrueOrderByNombreAsc();
}