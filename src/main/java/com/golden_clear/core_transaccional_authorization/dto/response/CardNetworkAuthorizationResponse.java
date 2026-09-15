package com.golden_clear.core_transaccional_authorization.dto.response;


public record CardNetworkAuthorizationResponse(
        String responseCode,
        String authorizationCode
) {
}
