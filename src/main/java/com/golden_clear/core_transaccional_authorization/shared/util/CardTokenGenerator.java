package com.golden_clear.core_transaccional_authorization.shared.util;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

/**
 * Genera el token determinístico de tarjeta (mismo PAN -> mismo token) usado tanto para
 * persistir la transacción como para buscar la cuenta en {@code card_account}.
 * Vive fuera de {@code shared.mappers} para poder ser reutilizado por servicios (p. ej.
 * el simulador de red) sin acoplarlos al paquete de mappers de MapStruct.
 */
public final class CardTokenGenerator {

    private CardTokenGenerator() {
    }

    public static String generate(String pan) {
        return "tok_" + UUID.nameUUIDFromBytes(pan.getBytes(StandardCharsets.UTF_8));
    }
}
