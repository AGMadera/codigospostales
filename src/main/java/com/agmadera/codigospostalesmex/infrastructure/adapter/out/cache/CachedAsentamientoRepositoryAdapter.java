package com.agmadera.codigospostalesmex.infrastructure.adapter.out.cache;

import com.agmadera.codigospostalesmex.domain.model.Asentamiento;
import com.agmadera.codigospostalesmex.domain.port.out.AsentamientoRepositoryPort;
import org.springframework.cache.annotation.Cacheable;

import java.util.List;

public class CachedAsentamientoRepositoryAdapter implements AsentamientoRepositoryPort {

    private final AsentamientoRepositoryPort delegate;  // el adaptador real (BD)

    public CachedAsentamientoRepositoryAdapter(AsentamientoRepositoryPort delegate) {
        this.delegate = delegate;
    }

    @Override
    @Cacheable(value = "asentamientosPorCp", key = "#codigoPostal")
    public List<Asentamiento> buscarPorCodigoPostal(String codigoPostal) {
        return delegate.buscarPorCodigoPostal(codigoPostal);
    }
}