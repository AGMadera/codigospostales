package com.agmadera.codigospostalesmex.infrastructure.adapter.in.rest;

import com.agmadera.codigospostalesmex.domain.exception.CodigoPostalNoEncontradoException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
/*
    @ExceptionHandler(CodigoPostalNoEncontradoException.class)
    public ResponseEntity<Map<String, String>> handleNotFound(CodigoPostalNoEncontradoException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", ex.getMessage()));
    }*/
/*
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleBadRequest(IllegalArgumentException ex) {
        return ResponseEntity.badRequest()
                .body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, Object>> handleConstraintViolation(
            ConstraintViolationException ex) {

        String mensaje = ex.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .findFirst()
                .orElse("Parámetro inválido");

        Map<String, Object> body = Map.of(
                "error", "SOLICITUD_INVALIDA",
                "mensaje", mensaje,
                "timestamp", LocalDateTime.now().toString()
        );

        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(CodigoPostalNoEncontradoException.class)
    public ProblemDetail handleNotFound(CodigoPostalNoEncontradoException ex) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(
                HttpStatus.NOT_FOUND, ex.getMessage());
        pd.setTitle("Código postal no encontrado");
        pd.setType(URI.create("localhost:8080/errors/cp-no-encontrado"));
        pd.setProperty("codigoPostal", ex.getCodigoPostal());
        pd.setProperty("timestamp", ex.getTimestamp().toString());
        return pd;
    }

 */
private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(CodigoPostalNoEncontradoException.class)
    public ProblemDetail handleNotFound(CodigoPostalNoEncontradoException ex) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(
                HttpStatus.NOT_FOUND, ex.getMessage());
        pd.setTitle("Código postal no encontrado");
        pd.setType(URI.create("/errors/cp-no-encontrado"));
        pd.setProperty("codigoPostal", ex.getCodigoPostal());
        pd.setProperty("timestamp", ex.getTimestamp().toString());
        return pd;
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ProblemDetail handleBadRequest(IllegalArgumentException ex) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST, ex.getMessage());
        pd.setTitle("Solicitud inválida");
        pd.setType(URI.create("/errors/solicitud-invalida"));
        pd.setProperty("timestamp", LocalDateTime.now().toString());
        return pd;
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ProblemDetail handleConstraintViolation(ConstraintViolationException ex) {
        String mensaje = ex.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .findFirst()
                .orElse("Parámetro inválido");

        ProblemDetail pd = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST, mensaje);
        pd.setTitle("Solicitud inválida");
        pd.setType(URI.create("/errors/solicitud-invalida"));
        pd.setProperty("timestamp", LocalDateTime.now().toString());
        return pd;
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGeneric(Exception ex) {
        log.error("Error inesperado", ex);
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Ocurrió un error inesperado");
        pd.setTitle("Error interno");
        pd.setType(URI.create("/errors/error-interno"));
        return pd;
    }
}
