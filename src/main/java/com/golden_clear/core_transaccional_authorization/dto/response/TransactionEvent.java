package com.golden_clear.core_transaccional_authorization.dto.response;

import java.math.BigDecimal;
import java.time.Instant;

public record TransactionEvent(
        String eventId,
        String eventType,
        String transactionId,
        String merchantId,
        String terminalId,
        String cardTokenMasked,
        BigDecimal amount,
        String currency,
        String responseCode,
        Instant occurredAt,
        int schemaVersion
) {
}
