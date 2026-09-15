package com.agmadera.codigospostalesmex.domain.exception;

import java.time.Instant;

public class CodigoPostalNoEncontradoException extends RuntimeException{
    private final String codigoPostal;
    private final Instant timestamp;

    public CodigoPostalNoEncontradoException(String codigoPostal) {
        super("Código postal no encontrado: " + codigoPostal);
        this.codigoPostal = codigoPostal;
        this.timestamp = Instant.now();
    }

    public String getCodigoPostal() {
        return codigoPostal;
    }

    public Instant getTimestamp() {
        return timestamp;
    }
}
