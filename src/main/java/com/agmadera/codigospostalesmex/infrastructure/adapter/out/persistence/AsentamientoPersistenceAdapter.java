package com.agmadera.codigospostalesmex.infrastructure.adapter.out.persistence;

import com.agmadera.codigospostalesmex.domain.model.Asentamiento;
import com.agmadera.codigospostalesmex.domain.port.out.AsentamientoRepositoryPort;

import java.util.List;

public class AsentamientoPersistenceAdapter /*implements AsentamientoRepositoryPort*/ {
    /*
    private final AsentamientoJpaRepository jpaRepository;
    private final AsentamientoMapper mapper;

    public AsentamientoPersistenceAdapter(AsentamientoJpaRepository jpaRepository, AsentamientoMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }


    @Override
    public List<Asentamiento> buscarPorCodigoPostal(String codigoPostal) {
        return jpaRepository.findByCodigoPostalOrderByAsentamientoAsc(codigoPostal)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

     */
}
