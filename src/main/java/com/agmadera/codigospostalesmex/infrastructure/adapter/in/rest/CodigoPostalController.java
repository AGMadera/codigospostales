package com.agmadera.codigospostalesmex.infrastructure.adapter.in.rest;

import com.agmadera.codigospostalesmex.domain.model.CodigoPostalInfo;
import com.agmadera.codigospostalesmex.domain.port.in.ConsultarCodigoPostalCaseUse;
import com.agmadera.codigospostalesmex.infrastructure.adapter.in.rest.dto.CodigoPostalResponse;
import com.agmadera.codigospostalesmex.infrastructure.adapter.in.rest.dto.ColoniaDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.constraints.Pattern;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/codigos-postales")
@Validated
public class CodigoPostalController {

    private final ConsultarCodigoPostalCaseUse useCase;  // depende del PUERTO, no del servicio

    public CodigoPostalController(ConsultarCodigoPostalCaseUse useCase) {
        this.useCase = useCase;
    }

    @Operation(summary = "Consulta colonias por código postal")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Encontrado"),
            @ApiResponse(responseCode = "404", description = "CP no existe"),
            @ApiResponse(responseCode = "400", description = "CP inválido")
    })
    @GetMapping("/{cp}")
    public ResponseEntity<CodigoPostalResponse> consultar(
            @Pattern(regexp = "\\d{5}", message = "El código postal debe tener 5 dígitos")
            @PathVariable String cp) {
        CodigoPostalInfo info = useCase.consultar(cp);

        CodigoPostalResponse response = new CodigoPostalResponse(
                info.codigoPostal(),
                info.estado(),
                info.municipio(),
                info.colonias().stream()
                        .map(a -> new ColoniaDTO(a.nombre(), a.tipo()))
                        .toList()
        );

        return ResponseEntity.ok(response);
    }
}
