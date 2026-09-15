package com.golden_clear.core_transaccional_authorization.shared.exception;

import java.time.Instant;

public record ApiError(Instant timestamp, int status, String message) {
}
