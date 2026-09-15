package com.golden_clear.core_transaccional_authorization.service.external;

import java.math.BigDecimal;


public record CardNetworkAuthorizationRequest(
        String pan,
        String expirationDate,
        String cvv,
        String emvCryptogram,
        String merchantId,
        String terminalId,
        BigDecimal amount,
        String currency,
        String stan
) {
}
