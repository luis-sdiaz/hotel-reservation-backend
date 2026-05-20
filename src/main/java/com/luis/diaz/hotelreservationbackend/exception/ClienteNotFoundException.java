package com.luis.diaz.hotelreservationbackend.exception;

public class ClienteNotFoundException extends RuntimeException {
    public ClienteNotFoundException() { super(); }
    public ClienteNotFoundException(String message) { super(message); }
    public ClienteNotFoundException(String message, Throwable cause) { super(message, cause); }
}
