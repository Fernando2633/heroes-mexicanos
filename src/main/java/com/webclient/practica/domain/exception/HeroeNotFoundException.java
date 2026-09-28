package com.webclient.practica.domain.exception;

public class HeroeNotFoundException extends RuntimeException {
    public HeroeNotFoundException(String message) {
        super(message);
    }
}
