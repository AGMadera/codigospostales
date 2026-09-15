package com.agmadera.codigospostalesmex.infrastructure.config;

import com.agmadera.codigospostalesmex.domain.port.in.ConsultarCodigoPostalCaseUse;
import com.agmadera.codigospostalesmex.domain.port.out.AsentamientoRepositoryPort;
import com.agmadera.codigospostalesmex.domain.service.CodigoPostalService;
import com.agmadera.codigospostalesmex.infrastructure.adapter.out.cache.CachedAsentamientoRepositoryAdapter;
import com.agmadera.codigospostalesmex.infrastructure.adapter.out.file.TxtFileAsentamientoAdapter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class BeanConfiguration {
    @Value("${app.sepomex.archivo}")
    private String rutaArchivo;

    @Bean
    public TxtFileAsentamientoAdapter txtFileAsentamientoAdapter() {
        return new TxtFileAsentamientoAdapter(rutaArchivo);
    }

    @Bean
    @Primary
    public AsentamientoRepositoryPort asentamientoRepositoryPort(
            TxtFileAsentamientoAdapter fileAdapter) {
        // Antes envolvíamos el PersistenceAdapter.
        // Ahora envolvemos el FileAdapter.
        return new CachedAsentamientoRepositoryAdapter(fileAdapter);
    }

    @Bean
    public ConsultarCodigoPostalCaseUse consultarCodigoPostalUseCase(
            AsentamientoRepositoryPort repository) {
        return new CodigoPostalService(repository);
    }
}
