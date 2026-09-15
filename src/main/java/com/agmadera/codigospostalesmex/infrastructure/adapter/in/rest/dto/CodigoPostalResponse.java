package com.agmadera.codigospostalesmex.infrastructure.adapter.in.rest.dto;

import java.util.List;

public record CodigoPostalResponse(
        String codigoPostal,
        String estado,
        String municipio,
        List<ColoniaDTO> colonias
) {}
