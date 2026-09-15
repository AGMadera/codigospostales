package com.agmadera.codigospostalesmex.infrastructure.adapter.out.file;

import com.agmadera.codigospostalesmex.domain.model.Asentamiento;
import com.agmadera.codigospostalesmex.domain.port.out.AsentamientoRepositoryPort;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public final class TxtFileAsentamientoAdapter implements AsentamientoRepositoryPort {

    private static final Logger log = LoggerFactory.getLogger(TxtFileAsentamientoAdapter.class);
    private static final Pattern SEPARATOR = Pattern.compile("\\|");   // pipe escapado para split
    private static final int MIN_CAMPOS = 14;
    // Índices de columnas del archivo SEPOMEX
    private static final int IDX_CODIGO    = 0;   // d_codigo
    private static final int IDX_ASENTA    = 1;   // d_asenta
    private static final int IDX_TIPO      = 2;   // d_tipo_asenta
    private static final int IDX_MUNICIPIO = 3;   // D_mnpio
    private static final int IDX_ESTADO    = 4;   // d_estado
    private static final int IDX_ZONA      = 13;  // d_zona
    private final Path archivo;

    // Índice en memoria: CP -> lista de asentamientos
    private volatile Map<String, List<Asentamiento>> indicePorCp = Map.of();

    public TxtFileAsentamientoAdapter(String rutaArchivo) {
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
                    .collect(Collectors.groupingBy(
                            Asentamiento::codigoPostal,
                            Collectors.toUnmodifiableList()
                    ));

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
        String[] campos = SEPARATOR.split(linea, -1);   // -1 para preservar vacíos

        if (campos.length < MIN_CAMPOS) {
            log.warn("Línea con formato inválido, ignorada: {}", linea);
            return null;
        }

        return new Asentamiento(
                campos[IDX_CODIGO].trim(),    // d_codigo
                campos[IDX_ASENTA].trim(),    // d_asenta
                campos[IDX_TIPO].trim(),    // d_tipo_asenta
                campos[IDX_MUNICIPIO].trim(),    // D_mnpio
                campos[IDX_ESTADO].trim(),    // d_estado
                campos[IDX_ZONA].trim()    // d_zona
        );
    }

    @Scheduled(cron = "0 0 3 * * *")   // todos los días a las 3am
    public void recargar() {
        log.info("Recargando archivo SEPOMEX...");
        try {
            cargarArchivo();
        }catch (Exception e){
            log.error("Fallo al recargar SEPOMEX, se conserva el índice anterior", e);
        }

    }
}
