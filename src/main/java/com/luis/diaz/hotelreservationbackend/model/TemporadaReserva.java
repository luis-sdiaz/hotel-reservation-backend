package com.luis.diaz.hotelreservationbackend.model;

import java.math.BigDecimal;

public enum TemporadaReserva {

    TEMPORADA_BAJA(new BigDecimal("1.00")),
    TEMPORADA_MEDIA(new BigDecimal("1.15")),
    TEMPORADA_ALTA(new BigDecimal("1.30"));

    private final BigDecimal multiplicador;

    TemporadaReserva(BigDecimal multiplicador) {
        this.multiplicador = multiplicador;
    }

    public BigDecimal getMultiplicador() {
        return multiplicador;
    }
}