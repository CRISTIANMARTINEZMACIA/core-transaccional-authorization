package com.golden_clear.core_transaccional_authorization.service;

import com.golden_clear.core_transaccional_authorization.dto.request.AuthorizationTransaccionalRequest;
import com.golden_clear.core_transaccional_authorization.dto.response.AuthorizationTransaccionalResponse;

public interface AuthorizationService {

    /**
     * Autoriza una transacción contra la red de tarjetas y persiste el resultado.
     */
    AuthorizationTransaccionalResponse authorize(AuthorizationTransaccionalRequest request);

    /**
     * Consulta el resultado de una autorización previamente procesada.
     */
    AuthorizationTransaccionalResponse findByTransactionId(String transactionId);
}
