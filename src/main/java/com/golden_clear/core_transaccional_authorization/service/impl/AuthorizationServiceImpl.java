package com.golden_clear.core_transaccional_authorization.service.impl;

import com.golden_clear.core_transaccional_authorization.dto.request.AuthorizationTransaccionalRequest;
import com.golden_clear.core_transaccional_authorization.dto.response.AuthorizationTransaccionalResponse;
import com.golden_clear.core_transaccional_authorization.infraestructure.entity.AuthorizationTransaccional;
import com.golden_clear.core_transaccional_authorization.infraestructure.repository.AuthorizationTransaccionalRepository;
import com.golden_clear.core_transaccional_authorization.service.AuthorizationService;
import com.golden_clear.core_transaccional_authorization.service.CardNetworkService;
import com.golden_clear.core_transaccional_authorization.service.external.CardNetworkAuthorizationRequest;
import com.golden_clear.core_transaccional_authorization.service.external.CardNetworkAuthorizationResponse;
import com.golden_clear.core_transaccional_authorization.shared.exception.AuthorizationNotFoundException;
import com.golden_clear.core_transaccional_authorization.shared.mappers.AuthorizationMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthorizationServiceImpl implements AuthorizationService {

    private static final Logger log = LoggerFactory.getLogger(AuthorizationServiceImpl.class);

    private final AuthorizationTransaccionalRepository repository;
    private final CardNetworkService cardNetworkService;

    @Override
    @Transactional
    public AuthorizationTransaccionalResponse authorize(AuthorizationTransaccionalRequest request) {
        CardNetworkAuthorizationRequest networkRequest = new CardNetworkAuthorizationRequest(
                request.pan(),
                request.expirationDate(),
                request.cvv(),
                request.emvCryptogram(),
                request.merchantId(),
                request.terminalId(),
                request.amount(),
                request.currency(),
                request.stan()
        );

        CardNetworkAuthorizationResponse networkResponse = cardNetworkService.authorize(networkRequest);

        AuthorizationTransaccional entity = AuthorizationMapper.toEntity(request, networkResponse);
        AuthorizationTransaccional saved = repository.save(entity);

        log.info("Transacción procesada transactionId={} stan={} responseCode={}",
                saved.getTransactionId(), saved.getStan(), saved.getResponseCode());

        return AuthorizationMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public AuthorizationTransaccionalResponse findByTransactionId(String transactionId) {
        AuthorizationTransaccional entity = repository.findById(transactionId)
                .orElseThrow(() -> new AuthorizationNotFoundException(transactionId));
        return AuthorizationMapper.toResponse(entity);
    }
}
