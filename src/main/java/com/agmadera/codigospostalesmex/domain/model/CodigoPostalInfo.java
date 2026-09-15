package com.agmadera.codigospostalesmex.domain.model;

import java.util.List;

public record CodigoPostalInfo(String codigoPostal, String estado, String municipio, List<Asentamiento> colonias) {}
