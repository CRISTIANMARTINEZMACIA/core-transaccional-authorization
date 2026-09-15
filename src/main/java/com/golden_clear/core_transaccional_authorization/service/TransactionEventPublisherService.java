package com.golden_clear.core_transaccional_authorization.service;

import com.golden_clear.core_transaccional_authorization.dto.response.TransactionEvent;

public interface TransactionEventPublisherService {
    void publish(TransactionEvent event);
}
