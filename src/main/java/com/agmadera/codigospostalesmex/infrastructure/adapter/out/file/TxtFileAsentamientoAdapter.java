package com.agmadera.codigospostalesmex.infrastructure.adapter.out.file;

import com.agmadera.codigospostalesmex.domain.model.Asentamiento;
import com.agmadera.codigospostalesmex.domain.port.out.AsentamientoRepositoryPort;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class TxtFileAsentamientoAdapter implements AsentamientoRepositoryPort {

    private static final Logger log = LoggerFactory.getLogger(TxtFileAsentamientoAdapter.class);
    private static final String SEPARATOR = "\\|";   // pipe escapado para split

    private final Path archivo;

    // Índice en memoria: CP -> lista de asentamientos
    private Map<String, List<Asentamiento>> indicePorCp = Map.of();

    //No se utiliza porque se declaro en BeanConfiguration
    public TxtFileAsentamientoAdapter(
            @Value("${app.sepomex.archivo}") String rutaArchivo) {
        this.archivo = Path.of(rutaArchivo);
    }

    @PostConstruct
    public void cargarArchivo() {
        long inicio = System.currentTimeMillis();

        try (BufferedReader reader = Files.newBufferedReader(archivo, StandardCharsets.ISO_8859_1)) {

            List<Asentamiento> todos = reader.lines()
                    .skip(1)                             // saltar encabezado
                    .filter(linea -> !linea.isBlank())   // ignorar líneas vacías
                    .map(this::parsearLinea)
                    .filter(Objects::nonNull)
                    .toList();

            this.indicePorCp = todos.stream()
                    .collect(Collectors.groupingBy(Asentamiento::codigoPostal));

            long ms = System.currentTimeMillis() - inicio;
            log.info("Cargados {} asentamientos en {} CPs desde {} ({} ms)",
                    todos.size(), indicePorCp.size(), archivo, ms);


        } catch (IOException e) {
            throw new IllegalStateException("No se pudo leer el archivo: " + archivo, e);
        }
    }
    @Override
    public List<Asentamiento> buscarPorCodigoPostal(String codigoPostal) {
        return indicePorCp.getOrDefault(codigoPostal, List.of());
    }

    private Asentamiento parsearLinea(String linea) {
        String[] campos = linea.split(SEPARATOR, -1);   // -1 para preservar vacíos

        if (campos.length < 5) {
            log.warn("Línea con formato inválido, ignorada: {}", linea);
            return null;
        }

        return new Asentamiento(
                campos[0].trim(),    // d_codigo
                campos[1].trim(),    // d_asenta
                campos[2].trim(),    // d_tipo_asenta
                campos[3].trim(),    // D_mnpio
                campos[4].trim(),    // d_estado
                campos[13].trim()    // d_zona
        );
    }

    @Scheduled(cron = "0 0 3 * * *")   // todos los días a las 3am
    public void recargar() {
        log.info("Recargando archivo SEPOMEX...");
        cargarArchivo();
    }
}
