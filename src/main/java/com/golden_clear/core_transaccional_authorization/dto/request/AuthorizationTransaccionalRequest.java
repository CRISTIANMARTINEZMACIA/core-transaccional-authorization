package com.golden_clear.core_transaccional_authorization.dto.request;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.Instant;

public record AuthorizationTransaccionalRequest(
       @NotNull String pan,
       @NotNull String expirationDate,
       @NotNull String cvv,
       @NotNull String pinBlockEncrypted,
       @NotNull String emvCryptogram,
       @NotNull String terminalId,
       @NotNull String merchantId,
       @NotNull BigDecimal amount,
       @NotNull String currency,
       @NotNull String stan,
       @NotNull Instant transmissionDateTime,
       @NotNull String posEntryMode
) {
}
