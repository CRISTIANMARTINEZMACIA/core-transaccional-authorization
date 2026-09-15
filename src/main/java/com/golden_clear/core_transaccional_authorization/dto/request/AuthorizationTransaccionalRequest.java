package com.golden_clear.core_transaccional_authorization.dto.request;

import java.math.BigDecimal;
import java.time.Instant;

public record AuthorizationTransaccionalRequest(
        String pan,
        String expirationDate,
        String cvv,
        String pinBlockEncrypted,
        String emvCryptogram,
        String terminalId,
        String merchantId,
        BigDecimal amount,
        String currency,
        String stan,
        Instant transmissionDateTime,
        String posEntryMode
) {
}
