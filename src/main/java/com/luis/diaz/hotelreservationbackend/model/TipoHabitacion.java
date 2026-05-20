package com.luis.diaz.hotelreservationbackend.model;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum TipoHabitacion {
    SIMPLE,
    DOUBLE,
    SUITE;

    @JsonCreator
    public static TipoHabitacion fromJson(String value) {
        if (value == null) {
            return null;
        }

        String normalized = value.trim().toUpperCase();

        if ("DOBLE".equals(normalized)) {
            return DOUBLE;
        }

        return TipoHabitacion.valueOf(normalized);
    }
}

