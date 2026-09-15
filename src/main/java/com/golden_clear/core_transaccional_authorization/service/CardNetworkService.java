package com.golden_clear.core_transaccional_authorization.service;

import com.golden_clear.core_transaccional_authorization.service.external.CardNetworkAuthorizationRequest;
import com.golden_clear.core_transaccional_authorization.service.external.CardNetworkAuthorizationResponse;

/**
 * Puerto hacia la red de tarjetas (Visa/Mastercard/etc.) encargada de autorizar la transacción.
 * La implementación actual es una simulación; una futura implementación real haría la conexión
 * ISO 8583 / API del adquirente correspondiente.
 */
public interface CardNetworkService {

    CardNetworkAuthorizationResponse authorize(CardNetworkAuthorizationRequest request);
}
