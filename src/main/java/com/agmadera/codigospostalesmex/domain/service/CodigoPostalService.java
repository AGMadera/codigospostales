package com.agmadera.codigospostalesmex.domain.service;

import com.agmadera.codigospostalesmex.domain.exception.CodigoPostalNoEncontradoException;
import com.agmadera.codigospostalesmex.domain.model.Asentamiento;
import com.agmadera.codigospostalesmex.domain.model.CodigoPostalInfo;
import com.agmadera.codigospostalesmex.domain.port.in.ConsultarCodigoPostalCaseUse;
import com.agmadera.codigospostalesmex.domain.port.out.AsentamientoRepositoryPort;

import java.util.List;

public class CodigoPostalService implements ConsultarCodigoPostalCaseUse {

    private final AsentamientoRepositoryPort repositoryPort;

    public CodigoPostalService(AsentamientoRepositoryPort repositoryPort) {
        this.repositoryPort = repositoryPort;
    }

    @Override
    public CodigoPostalInfo consultar(String codigoPostal) {
        if (codigoPostal == null || codigoPostal.isBlank()) {
            throw new IllegalArgumentException("El código postal es obligatorio");
        }
        if (!codigoPostal.matches("\\d{5}")){
            throw new IllegalArgumentException("CP no valido, debe de tener 5 caracteres");
        }
        List<Asentamiento> asentamientos = repositoryPort.buscarPorCodigoPostal(codigoPostal);

        if(asentamientos.isEmpty()) {
            throw new CodigoPostalNoEncontradoException(codigoPostal);
        }

        Asentamiento asentamiento = asentamientos.get(0);

        return new CodigoPostalInfo(asentamiento.codigoPostal(),asentamiento.estado(),asentamiento.municipio(), asentamientos);
    }
}
