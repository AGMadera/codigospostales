package com.agmadera.codigospostalesmex.domain.port.in;

import com.agmadera.codigospostalesmex.domain.model.CodigoPostalInfo;

public interface ConsultarCodigoPostalCaseUse {
    CodigoPostalInfo consultar(String codigoPostal);
}
