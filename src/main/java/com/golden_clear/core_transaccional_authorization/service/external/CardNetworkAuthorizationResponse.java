package com.golden_clear.core_transaccional_authorization.service.external;

/**
 * Respuesta devuelta por la red de tarjetas (hoy simulada) para una solicitud de autorización.
 *
 * @param responseCode      código de respuesta estilo ISO 8583 (ej. "00" aprobada, "05"/"51" declinada)
 * @param authorizationCode código de autorización otorgado por la red, presente únicamente si fue aprobada
 */
public record CardNetworkAuthorizationResponse(
        String responseCode,
        String authorizationCode
) {
}
