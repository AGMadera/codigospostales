package com.agmadera.codigospostalesmex.domain.port.out;

import com.agmadera.codigospostalesmex.domain.model.Asentamiento;

import java.util.List;

public interface AsentamientoRepositoryPort {
    List<Asentamiento> buscarPorCodigoPostal(String codigoPostal);
}
