package com.golden_clear.core_transaccional_authorization.shared.mappers;

import com.golden_clear.core_transaccional_authorization.dto.request.AuthorizationTransaccionalRequest;
import com.golden_clear.core_transaccional_authorization.dto.response.AuthorizationTransaccionalResponse;
import com.golden_clear.core_transaccional_authorization.infraestructure.entity.AuthorizationTransaccional;
import com.golden_clear.core_transaccional_authorization.service.external.CardNetworkAuthorizationResponse;
import com.golden_clear.core_transaccional_authorization.shared.enums.AuthorizationStatus;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.UUID;

/**
 * Traduce entre el request/response de la API, la entity persistida y la respuesta de la
 * red de tarjetas. El estado y el motivo de rechazo no se persisten como columnas propias:
 * se derivan del {@code responseCode} (estilo ISO 8583) ya almacenado en la entity.
 */
public final class AuthorizationMapper {

    private static final Map<String, String> DECLINE_REASONS_BY_RESPONSE_CODE = Map.of(
            "05", "Transacción declinada por el emisor",
            "51", "Fondos insuficientes",
            "14", "Tarjeta inválida"
    );
    private static final String APPROVED_RESPONSE_CODE = "00";
    private static final int PAN_VISIBLE_PREFIX = 6;
    private static final int PAN_VISIBLE_SUFFIX = 4;

    private AuthorizationMapper() {
    }

    public static AuthorizationTransaccional toEntity(AuthorizationTransaccionalRequest request,
                                                        CardNetworkAuthorizationResponse networkResponse) {
        AuthorizationTransaccional entity = new AuthorizationTransaccional();
        entity.setCardToken(generateCardToken(request.pan()));
        entity.setPanMasked(maskPan(request.pan()));
        entity.setTerminalId(request.terminalId());
        entity.setMerchantId(request.merchantId());
        entity.setAmount(request.amount());
        entity.setCurrency(request.currency());
        entity.setStan(request.stan());
        entity.setTransmissionDateTime(request.transmissionDateTime());
        entity.setPosEntryMode(request.posEntryMode());
        entity.setResponseCode(networkResponse.responseCode());
        entity.setAuthorizationCode(networkResponse.authorizationCode());
        return entity;
    }

    public static AuthorizationTransaccionalResponse toResponse(AuthorizationTransaccional entity) {
        AuthorizationStatus status = resolveStatus(entity.getResponseCode());
        boolean approved = status == AuthorizationStatus.APPROVED;
        return new AuthorizationTransaccionalResponse(
                entity.getTransactionId(),
                entity.getStan(),
                status,
                entity.getAuthorizationCode(),
                approved ? entity.getAmount() : null,
                approved ? null : describeResponseCode(entity.getResponseCode())
        );
    }

    public static AuthorizationStatus resolveStatus(String responseCode) {
        if (responseCode == null) {
            return AuthorizationStatus.ERROR;
        }
        return APPROVED_RESPONSE_CODE.equals(responseCode) ? AuthorizationStatus.APPROVED : AuthorizationStatus.DECLINED;
    }

    public static String describeResponseCode(String responseCode) {
        return DECLINE_REASONS_BY_RESPONSE_CODE.getOrDefault(responseCode, "Transacción declinada");
    }

    public static String maskPan(String pan) {
        if (pan == null || pan.length() < PAN_VISIBLE_PREFIX + PAN_VISIBLE_SUFFIX) {
            return "****";
        }
        String firstSix = pan.substring(0, PAN_VISIBLE_PREFIX);
        String lastFour = pan.substring(pan.length() - PAN_VISIBLE_SUFFIX);
        String masked = "*".repeat(pan.length() - PAN_VISIBLE_PREFIX - PAN_VISIBLE_SUFFIX);
        return firstSix + masked + lastFour;
    }

    private static String generateCardToken(String pan) {
        return "tok_" + UUID.nameUUIDFromBytes(pan.getBytes(StandardCharsets.UTF_8));
    }
}
