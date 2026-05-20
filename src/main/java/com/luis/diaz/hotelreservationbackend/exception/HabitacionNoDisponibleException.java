package com.luis.diaz.hotelreservationbackend.exception;

public class HabitacionNoDisponibleException extends RuntimeException {
    public HabitacionNoDisponibleException() { super(); }
    public HabitacionNoDisponibleException(String message) { super(message); }
    public HabitacionNoDisponibleException(String message, Throwable cause) { super(message, cause); }
}
