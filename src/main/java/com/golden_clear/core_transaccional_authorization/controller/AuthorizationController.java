package com.golden_clear.core_transaccional_authorization.controller;

import com.golden_clear.core_transaccional_authorization.dto.request.AuthorizationTransaccionalRequest;
import com.golden_clear.core_transaccional_authorization.dto.response.AuthorizationTransaccionalResponse;
import com.golden_clear.core_transaccional_authorization.service.AuthorizationService;
import com.golden_clear.core_transaccional_authorization.shared.exception.ApiError;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/authorizations")
@RequiredArgsConstructor
@Tag(name = "Autorizaciones", description = "Operaciones para autorizar y consultar transacciones con tarjeta")
public class AuthorizationController {

    private final AuthorizationService authorizationService;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
            summary = "Autorizar una transacción",
            description = "Envía la transacción a la red de tarjetas (simulada) y persiste el resultado de la autorización."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Transacción procesada (aprobada o declinada)",
                    content = @Content(schema = @Schema(implementation = AuthorizationTransaccionalResponse.class))),
            @ApiResponse(responseCode = "400", description = "Datos de la solicitud inválidos",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    public ResponseEntity<AuthorizationTransaccionalResponse> authorize(
            @Valid @RequestBody AuthorizationTransaccionalRequest request) {
        AuthorizationTransaccionalResponse response = authorizationService.authorize(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping(value = "/{transactionId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
            summary = "Consultar una transacción",
            description = "Obtiene el resultado de una autorización previamente procesada por su identificador."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Transacción encontrada",
                    content = @Content(schema = @Schema(implementation = AuthorizationTransaccionalResponse.class))),
            @ApiResponse(responseCode = "404", description = "Transacción no encontrada",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    public ResponseEntity<AuthorizationTransaccionalResponse> getByTransactionId(
            @Parameter(description = "Identificador de la transacción", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
            @PathVariable String transactionId) {
        return ResponseEntity.ok(authorizationService.findByTransactionId(transactionId));
    }
}
