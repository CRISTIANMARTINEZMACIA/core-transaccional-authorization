package com.golden_clear.core_transaccional_authorization.service.external;

import java.math.BigDecimal;

/**
 * Representa el mensaje de autorización que se envía a la red de tarjetas (hoy simulada).
 * Solo viajan los datos que un switch/red de tarjetas necesitaría (equivalente a un mensaje ISO 8583).
 */
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
