package com.golden_clear.core_transaccional_authorization.service;

import com.golden_clear.core_transaccional_authorization.dto.request.AuthorizationTransaccionalRequest;
import com.golden_clear.core_transaccional_authorization.dto.response.AuthorizationTransaccionalResponse;

public interface AuthorizationService {

    AuthorizationTransaccionalResponse authorize(AuthorizationTransaccionalRequest request);

    AuthorizationTransaccionalResponse findByTransactionId(String transactionId);
}
