package com.golden_clear.core_transaccional_authorization.service.external;


public record CardNetworkAuthorizationResponse(
        String responseCode,
        String authorizationCode
) {
}
