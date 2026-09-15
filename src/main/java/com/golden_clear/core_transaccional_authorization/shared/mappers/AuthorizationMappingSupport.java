package com.golden_clear.core_transaccional_authorization.shared.mappers;

import com.golden_clear.core_transaccional_authorization.shared.enums.AuthorizationStatus;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.UUID;


final class AuthorizationMappingSupport {

    static final String APPROVED_RESPONSE_CODE = "00";
    private static final int PAN_VISIBLE_PREFIX = 6;
    private static final int PAN_VISIBLE_SUFFIX = 4;
    private static final Map<String, String> DECLINE_REASONS_BY_RESPONSE_CODE = Map.of(
            "05", "Transacción declinada por el emisor",
            "51", "Fondos insuficientes",
            "14", "Tarjeta inválida"
    );

    private AuthorizationMappingSupport() {
    }

    static AuthorizationStatus resolveStatus(String responseCode) {
        if (responseCode == null) {
            return AuthorizationStatus.ERROR;
        }
        return APPROVED_RESPONSE_CODE.equals(responseCode) ? AuthorizationStatus.APPROVED : AuthorizationStatus.DECLINED;
    }

    static String describeResponseCode(String responseCode) {
        return DECLINE_REASONS_BY_RESPONSE_CODE.getOrDefault(responseCode, "Transacción declinada");
    }

    static String maskPan(String pan) {
        if (pan == null || pan.length() < PAN_VISIBLE_PREFIX + PAN_VISIBLE_SUFFIX) {
            return "****";
        }
        String firstSix = pan.substring(0, PAN_VISIBLE_PREFIX);
        String lastFour = pan.substring(pan.length() - PAN_VISIBLE_SUFFIX);
        String masked = "*".repeat(pan.length() - PAN_VISIBLE_PREFIX - PAN_VISIBLE_SUFFIX);
        return firstSix + masked + lastFour;
    }

    static String generateCardToken(String pan) {
        return "tok_" + UUID.nameUUIDFromBytes(pan.getBytes(StandardCharsets.UTF_8));
    }
}
