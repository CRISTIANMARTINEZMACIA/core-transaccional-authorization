package com.golden_clear.core_transaccional_authorization.shared.mappers;

import com.golden_clear.core_transaccional_authorization.shared.enums.AuthorizationStatus;

import java.util.Map;

/**
 * Lógica de derivación usada por {@link AuthorizationMapper} vía {@code expression}.
 * Se mantiene fuera de la interfaz @Mapper a propósito: MapStruct trata cualquier método
 * (String) -> String declarado dentro de un @Mapper como candidato implícito para CUALQUIER
 * propiedad String -> String, lo que produce "Ambiguous mapping methods" apenas hay más de uno.
 */
final class AuthorizationMappingSupport {

    static final String APPROVED_RESPONSE_CODE = "00";
    private static final Map<String, String> DECLINE_REASONS_BY_RESPONSE_CODE = Map.of(
            "05", "Transacción declinada por el emisor",
            "14", "Tarjeta inválida",
            "51", "Fondos insuficientes",
            "54", "Tarjeta expirada",
            "61", "Excede el límite diario permitido",
            "62", "Tarjeta restringida"
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
        int visiblePrefix = 6;
        int visibleSuffix = 4;
        if (pan == null || pan.length() < visiblePrefix + visibleSuffix) {
            return "****";
        }
        String firstSix = pan.substring(0, visiblePrefix);
        String lastFour = pan.substring(pan.length() - visibleSuffix);
        String masked = "*".repeat(pan.length() - visiblePrefix - visibleSuffix);
        return firstSix + masked + lastFour;
    }
}
