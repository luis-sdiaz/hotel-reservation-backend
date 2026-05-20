package com.luis.diaz.hotelreservationbackend.repository;

import com.luis.diaz.hotelreservationbackend.model.Cliente;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    Optional<Cliente> findByEmail(String email);

    Page<Cliente> findByActivo(Boolean activo, Pageable pageable);
}
