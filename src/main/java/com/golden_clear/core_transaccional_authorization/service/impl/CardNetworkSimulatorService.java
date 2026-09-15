package com.golden_clear.core_transaccional_authorization.service.impl;

import com.golden_clear.core_transaccional_authorization.service.CardNetworkService;
import com.golden_clear.core_transaccional_authorization.service.external.CardNetworkAuthorizationRequest;
import com.golden_clear.core_transaccional_authorization.service.external.CardNetworkAuthorizationResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Simula la comunicación con una red de tarjetas real: agrega latencia de red y decide
 * la aprobación/declinación de la transacción mediante reglas simples, generando un
 * código de respuesta estilo ISO 8583 y, si aplica, un código de autorización.
 * <p>
 * Reemplazar por una implementación real de {@link CardNetworkService} cuando exista
 * integración con el adquirente/red de tarjetas.
 */
@Service
public class CardNetworkSimulatorService implements CardNetworkService {

    private static final Logger log = LoggerFactory.getLogger(CardNetworkSimulatorService.class);

    private static final String RESPONSE_CODE_APPROVED = "00";
    private static final String RESPONSE_CODE_DECLINED_GENERIC = "05";
    private static final String RESPONSE_CODE_INSUFFICIENT_FUNDS = "51";

    private static final BigDecimal INSUFFICIENT_FUNDS_THRESHOLD = new BigDecimal("5000000");
    private static final double RANDOM_DECLINE_PROBABILITY = 0.1;
    private static final int MIN_LATENCY_MS = 50;
    private static final int MAX_LATENCY_MS = 300;

    @Override
    public CardNetworkAuthorizationResponse authorize(CardNetworkAuthorizationRequest request) {
        simulateNetworkLatency();

        if (request.amount().compareTo(INSUFFICIENT_FUNDS_THRESHOLD) > 0) {
            log.info("Red de tarjetas: fondos insuficientes stan={} amount={}", request.stan(), request.amount());
            return new CardNetworkAuthorizationResponse(RESPONSE_CODE_INSUFFICIENT_FUNDS, null);
        }

        if (ThreadLocalRandom.current().nextDouble() < RANDOM_DECLINE_PROBABILITY) {
            log.info("Red de tarjetas: declinada por el emisor stan={}", request.stan());
            return new CardNetworkAuthorizationResponse(RESPONSE_CODE_DECLINED_GENERIC, null);
        }

        String authorizationCode = generateAuthorizationCode();
        log.info("Red de tarjetas: aprobada stan={} authorizationCode={}", request.stan(), authorizationCode);
        return new CardNetworkAuthorizationResponse(RESPONSE_CODE_APPROVED, authorizationCode);
    }

    private String generateAuthorizationCode() {
        return String.valueOf(ThreadLocalRandom.current().nextInt(100000, 999999));
    }

    private void simulateNetworkLatency() {
        try {
            Thread.sleep(ThreadLocalRandom.current().nextInt(MIN_LATENCY_MS, MAX_LATENCY_MS));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
