package com.golden_clear.core_transaccional_authorization.service;

import com.golden_clear.core_transaccional_authorization.dto.request.CardNetworkAuthorizationRequest;
import com.golden_clear.core_transaccional_authorization.dto.response.CardNetworkAuthorizationResponse;

public interface CardNetworkService {

    CardNetworkAuthorizationResponse authorize(CardNetworkAuthorizationRequest request);
}
