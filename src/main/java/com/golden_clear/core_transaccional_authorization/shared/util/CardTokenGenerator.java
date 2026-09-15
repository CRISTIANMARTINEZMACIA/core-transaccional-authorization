package com.golden_clear.core_transaccional_authorization.shared.util;

import java.nio.charset.StandardCharsets;
import java.util.UUID;


public final class CardTokenGenerator {

    private CardTokenGenerator() {
    }

    public static String generate(String pan) {
        return "tok_" + UUID.nameUUIDFromBytes(pan.getBytes(StandardCharsets.UTF_8));
    }
}
