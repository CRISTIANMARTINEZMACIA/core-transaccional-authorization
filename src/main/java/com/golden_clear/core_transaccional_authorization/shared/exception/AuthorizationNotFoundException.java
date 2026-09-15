package com.golden_clear.core_transaccional_authorization.shared.exception;

public class AuthorizationNotFoundException extends RuntimeException {

    public AuthorizationNotFoundException(String transactionId) {
        super("No se encontró la transacción con id: " + transactionId);
    }
}
