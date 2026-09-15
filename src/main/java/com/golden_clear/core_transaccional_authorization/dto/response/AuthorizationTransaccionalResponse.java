package com.golden_clear.core_transaccional_authorization.dto.response;

import com.golden_clear.core_transaccional_authorization.shared.enums.AuthorizationStatus;

import java.math.BigDecimal;

public record AuthorizationTransaccionalResponse(
        String transactionId,
        String stan,
        AuthorizationStatus status,
        String authorizationCode,
        BigDecimal approvedAmount,
        String declineReason ) {
}
